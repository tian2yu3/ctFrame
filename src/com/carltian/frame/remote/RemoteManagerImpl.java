package com.carltian.frame.remote;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.HeaderConfig;

import com.carltian.frame.CurrentContext;
import com.carltian.frame.FrameContext;
import com.carltian.frame.container.ContainerImpl;
import com.carltian.frame.container.annotation.Resource;
import com.carltian.frame.container.reg.Registration;
import com.carltian.frame.container.reg.RegistrationType;
import com.carltian.frame.remote.annotation.Context;
import com.carltian.frame.remote.annotation.Function;
import com.carltian.frame.remote.annotation.Param;
import com.carltian.frame.remote.annotation.Session;
import com.carltian.frame.remote.dto.ResultDto;
import com.carltian.frame.util.FrameLogger;

public class RemoteManagerImpl implements RemoteManager {

	@Resource
	private ContainerImpl container;

	private final ConcurrentHashMap<ModuleFunction, ModuleFunctionInfo> functionMap = new ConcurrentHashMap<ModuleFunction, ModuleFunctionInfo>();

	@Override
	public void register(ActionConfig config) {
		// 加入容器
		Registration reg = config.getRegistration();
		if (reg.getName() == null || reg.getName().isEmpty()) {
			// 匿名Action
			reg.setName(UUID.randomUUID().toString());
		}
		container.register(reg);
		// 参数转换与有效性校验
		if (config.getModule() == null || config.getModule().isEmpty()) {
			// 默认Module名称
			config.setModule(reg.getClazz().getSimpleName());
		}
		Method[] methodArray = reg.getClazz().getMethods();
		for (Method method : methodArray) {
			// 读取方法的Function注解
			Function function = method.getAnnotation(Function.class);
			if (function != null) {
				String functionName = function.value();
				if (functionName.isEmpty()) {
					functionName = method.getName();
				}
				ModuleFunctionInfo prevInfo = functionMap.putIfAbsent(new ModuleFunction(config.getModule(), functionName),
						new ModuleFunctionInfo(method, reg.getName()));
				if (prevInfo != null) {
					// 发现已经存在相同的映射
					throw new RuntimeException("远程响应接口名称冲突：" + config.getModule() + "[" + functionName + "]");
				}
			}
		}
	}

	public void onMessage(EventMessageImpl message) {
		switch (message.getType()) {
		case Call:
		case Event:
			onInvoke(message);
			break;
		case Result:
			// FIXME onResult
			break;
		default:
			break;
		}
	}

	@Override
	public <T> ResultDto<T> remoteCall(String remoteId, EventMessage msg) {
		// FIXME Remote Call
		return null;
	}

	@Override
	public void pushEvent(String remoteId, EventMessage msg) {
		if (!FrameContext.isSupportPushEvent()) {
			FrameLogger.error("不支持服务器推送消息！");
			throw new RuntimeException("不支持服务器推送消息！");
		}
		((EventMessageImpl) msg).setType(MessageType.Event);
		String json = ((EventMessageImpl) msg).toJsonStr();
		pushEvent(remoteId, json);
	}

	@Override
	public void pushEvent(Set<String> remoteSet, EventMessage msg) {
		if (!FrameContext.isSupportPushEvent()) {
			FrameLogger.error("不支持服务器推送消息！");
			throw new RuntimeException("不支持服务器推送消息！");
		}
		((EventMessageImpl) msg).setType(MessageType.Event);
		String json = ((EventMessageImpl) msg).toJsonStr();
		Iterator<String> i = remoteSet.iterator();
		while (i.hasNext()) {
			pushEvent(i.next(), json);
		}
	}

	private void pushEvent(String remoteId, String json) {
		if (remoteId == null) {
			BroadcasterFactory.getDefault().lookup("/", true).broadcast(json);
		} else {
			AtmosphereResource r = AtmosphereResourceFactory.getDefault().find(remoteId);
			if (r == null) {
				throw new IllegalArgumentException("无法找到Id为'" + remoteId + "'的远程资源！");
			}
			r.getBroadcaster().broadcast(json, r);
		}
	}

	private void onInvoke(EventMessageImpl message) {
		ModuleFunctionInfo info = functionMap.get(new ModuleFunction(message.getModule(), message.getFn()));
		if (info.method == null || info.annotations == null || info.types == null) {
			// 没有找到方法
			return;
		}
		// 初始化resource
		AtmosphereResource resource = AtmosphereResourceFactory.getDefault().find(
				CurrentContext.getRequest().getParameter(HeaderConfig.X_ATMOSPHERE_TRACKING_ID));
		if (resource == null) {
			// 参数错误
			return;
		}
		// 初始化session
		HttpSession session = CurrentContext.getRequest().getSession(true);
		// 映射方法参数
		Object[] args = new Object[info.types.length];
		for (int i = 0; i < args.length; i++) {
			// 读取每个参数的注解进行映射
			for (Annotation annotation : info.annotations[i]) {
				if (annotation instanceof Param) {
					args[i] = message.fetchData(((Param) annotation).value(), info.types[i]);
					break;
				} else if (annotation instanceof Session) {
					String attr = ((Session) annotation).value();
					if (attr.isEmpty()) {
						args[i] = session;
					} else {
						if (session == null) {
							args[i] = null;
						} else {
							args[i] = session.getAttribute(attr);
						}
					}
					break;
				} else if (annotation instanceof Context) {
					switch (((Context) annotation).value()) {
					case RemoteId:
						args[i] = resource.uuid();
						break;
					}
				}
			}
			// 修正为null的基本类型参数
			if (args[i] == null && info.types[i] instanceof Class && ((Class<?>) info.types[i]).isPrimitive()) {
				if (info.types[i] == Boolean.TYPE) {
					args[i] = false;
				} else if (info.types[i] == Character.TYPE) {
					args[i] = '\0';
				} else {
					args[i] = (byte) 0;
				}
			}
		}
		// 调用函数
		try {
			Object result = info.method.invoke(container.lookup(RegistrationType.Action, info.instanceName), args);
			if (message.getType() == MessageType.Call) {
				// 将函数返回值推送到前端
				if (info.method.getReturnType() != ResultDto.class) {
					FrameLogger.error("Unexpect Return Type of Call!");
					throw new RuntimeException("Unexpect Return Type of Call!");
				}
				EventMessageImpl reply = message.buildResultMessage((ResultDto<?>) result);
				resource.getBroadcaster().broadcast(reply.toJsonStr(), resource);
			} else {
				if (info.method.getReturnType() != Void.TYPE) {
					FrameLogger.error("Unexpect Return Type of Event!");
					throw new RuntimeException("Unexpect Return Type of Event!");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("调用" + message.getModule() + "模块" + message.getFn() + "方法抛出异常", e);
		}
	}

	private class ModuleFunctionInfo {

		private final Method method;
		private final String instanceName;
		private final Annotation[][] annotations;
		private final Type[] types;

		private ModuleFunctionInfo(Method method, String instanceName) {
			this.method = method;
			this.instanceName = instanceName;
			annotations = method.getParameterAnnotations();
			types = method.getGenericParameterTypes();
		}
	}

	private class ModuleFunction {
		private final String module;
		private final String function;

		private ModuleFunction(String module, String function) {
			this.module = module;
			this.function = function;
		}

		private boolean equals(Object obj1, Object obj2) {
			if (obj1 == null) {
				return obj2 == null;
			} else {
				return obj1.equals(obj2);
			}
		}

		@Override
		public boolean equals(Object obj) {
			return ((obj instanceof ModuleFunction) && equals(module, ((ModuleFunction) obj).module) && equals(function,
					((ModuleFunction) obj).function));
		}

		@Override
		public int hashCode() {
			int result = 17;
			result = result * 37 + module == null ? 0 : module.hashCode();
			result = result * 37 + function == null ? 0 : function.hashCode();
			return result;
		}
	}
}
