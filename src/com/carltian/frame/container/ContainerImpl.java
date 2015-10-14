package com.carltian.frame.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.util.TypeUtils;
import com.carltian.frame.container.reg.ArgInfo;
import com.carltian.frame.container.reg.InjectInfo;
import com.carltian.frame.container.reg.MethodInfo;
import com.carltian.frame.container.reg.Registration;
import com.carltian.frame.container.reg.RegistrationImpl;
import com.carltian.frame.container.reg.RegistrationKey;
import com.carltian.frame.container.reg.RegistrationType;
import com.carltian.frame.util.FrameLogger;

/**
 * 容器实现类
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class ContainerImpl implements Container {

	/**
	 * 储存所有注册信息
	 */
	private final ConcurrentHashMap<RegistrationKey, RegistrationImpl> registrationMap = new ConcurrentHashMap<RegistrationKey, RegistrationImpl>();

	/**
	 * 储存所有容器维护的实例，主要是单例对象。
	 */
	private final ConcurrentHashMap<RegistrationImpl, Object> instanceMap = new ConcurrentHashMap<RegistrationImpl, Object>();

	/**
	 * 储存当前线程正在创建的类，用于检测循环注入。
	 */
	private final ThreadLocal<Set<RegistrationImpl>> creatingSetLocal = new ThreadLocal<Set<RegistrationImpl>>();

	public ContainerImpl(Object name) {
		if (name != null) {
			// 需要注册自身
			registerSingleton(RegistrationType.Resource, name, this);
		}
	}

	@Override
	public <T> T lookup(RegistrationType type, Object name) {
		return lookup(type, null, name);
	}

	@Override
	public <T> T lookup(String extType, Object name) {
		return lookup(RegistrationType.Extension, extType, name);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T lookup(RegistrationType type, String extType, Object name) {
		// 规范化参数，尽力查找匹配项
		if (type != RegistrationType.Extension) {
			extType = null;
		}
		return (T) getInstance(registrationMap.get(new RegistrationKey(type, extType, TypeUtils.castToString(name))));
	}

	public void register(Registration reg) {
		// 检查参数基本属性
		if (reg == null) {
			throw new IllegalArgumentException("注册信息不可为空");
		}
		if (!(reg instanceof RegistrationImpl)) {
			throw new IllegalArgumentException("注册信息必须是" + RegistrationImpl.class.getName() + "的实例：" + reg);
		}
		registerCopy(((RegistrationImpl) reg).clone());
	}

	public void registerSingleton(RegistrationType type, Object name, Object instance) {
		registerSingleton(type, null, name, instance);
	}

	public void registerSingleton(String extType, Object name, Object instance) {
		registerSingleton(RegistrationType.Extension, extType, name, instance);
	}

	private void registerSingleton(RegistrationType type, String extType, Object name, Object instance) {
		RegistrationImpl reg = new RegistrationImpl(type, extType, TypeUtils.castToString(name), null, true);
		if (instance == null) {
			throw new IllegalArgumentException("单例注册时必须提供单例实例： " + reg);
		}
		reg.setClazz(instance.getClass());
		instanceMap.put(reg, instance);
		registerCopy(reg);
	}

	/**
	 * 将已经复制好的非空的资源注册信息进行注册
	 * 
	 * @param reg
	 *           资源注册信息
	 */
	private void registerCopy(RegistrationImpl reg) {
		// 检查参数基本属性
		if (reg.getType() == null || reg.getName() == null || reg.getClazz() == null) {
			throw new IllegalArgumentException("注册信息的type/name/clazz属性不可为空：" + reg);
		}
		if (reg.getType() != RegistrationType.Extension && reg.getExtType() != null) {
			throw new IllegalArgumentException("注册非扩展类型资源时extType属性必须为空：" + reg);
		}
		if (reg.getType() == RegistrationType.Extension && reg.getExtType() == null) {
			throw new IllegalArgumentException("注册扩展类型资源时extType属性不可为空：" + reg);
		}
		// 检查是否存在该注册项并注册
		if (registrationMap.putIfAbsent(new RegistrationKey(reg.getType(), reg.getExtType(), reg.getName()), reg) != null) {
			throw new IllegalArgumentException("注册信息指定的类别与名称已经存在： " + reg);
		}
	}

	/**
	 * 获取可用的实例，属于1层方法。
	 * 
	 * @param reg
	 *           资源注册信息
	 * @return 已经初始化完成的可用实例
	 */
	private Object getInstance(RegistrationImpl reg) {
		if (reg == null) {
			return null;
		}
		// 获取可用的实例
		Object instance = null;
		if (reg.isSingleton()) {
			instance = instanceMap.get(reg);
			if (instance == null) {
				// 可能需要新增单例
				synchronized (instanceMap) {
					// 加写入锁
					instance = instanceMap.get(reg);
					if (instance == null) {
						// 需要新增单例
						instance = createInstance(reg);
						instanceMap.put(reg, instance);
					}
				}
			}
		} else {
			// 新增实例
			instance = createInstance(reg);
		}
		return instance;
	}

	/**
	 * 创建可用的实例，同时实现循环引用的校验，属于2层方法。<br/>
	 * 与{@link #getInstance(Registration) getInstance}的分工是该函数只负责创建新的实例，并检查循环引用，{@link #getInstance(Registration)
	 * getInstance}负责处理单例模式，维护{@link #instanceMap}
	 * 
	 * @param reg
	 *           需要实例化的注册信息
	 * @return 已经初始化完成的可用实例
	 */
	private Object createInstance(RegistrationImpl reg) {
		Object instance;
		Set<RegistrationImpl> creatingSet = creatingSetLocal.get();
		if (creatingSet == null) {
			creatingSet = new HashSet<RegistrationImpl>();
			creatingSetLocal.set(creatingSet);
		}
		if (creatingSet.contains(reg)) {
			FrameLogger.error("发现循环注入错误！正在注入的资源尚未完成初始化：" + reg);
			throw new RuntimeException("发现循环注入错误！正在注入的资源尚未完成初始化：" + reg);
		}
		creatingSet.add(reg);
		try {
			instance = newInstance(reg);
			injectInstance(instance, reg);
		} finally {
			creatingSet.remove(reg);
		}
		return instance;
	}

	/**
	 * 获取一个全新的实例，属于3层方法。
	 * 
	 * @param reg
	 *           需要实例化的注册信息
	 * @return 一个全新的实例，该实例只运行了构造函数，Field尚未注入资源。
	 */
	private Object newInstance(RegistrationImpl reg) {
		Constructor<?> constructor = reg.getConstructor();
		if (constructor == null) {
			FrameLogger.error("缺少资源构造函数，无法构造资源：" + reg);
			throw new RuntimeException("缺少资源构造函数，无法构造资源：" + reg);
		}
		try {
			List<InjectInfo> injectInfoList = reg.getConstructorParams();
			Type[] parameterTypes = constructor.getGenericParameterTypes();
			Map<String, ArgInfo> initArgMap = reg.getInitArgMap();
			return constructor.newInstance(getArguments(injectInfoList, parameterTypes, initArgMap));
		} catch (Exception e) {
			FrameLogger.error("无法构造资源：" + reg, e);
			throw new RuntimeException("无法构造资源：" + reg, e);
		}
	}

	/**
	 * 为一个实例注入资源，属于3层方法。
	 * 
	 * @param obj
	 *           需要注入资源的实例
	 * @param reg
	 *           实例的注册信息
	 */
	private void injectInstance(Object obj, RegistrationImpl reg) {
		// 设置注入属性
		Set<Entry<Field, InjectInfo>> fieldEntrySet = reg.getInjectFields().entrySet();
		for (Entry<Field, InjectInfo> fieldEntry : fieldEntrySet) {
			Field field = fieldEntry.getKey();
			if (field == null) {
				FrameLogger.warn("发现不完整的注入属性信息：" + reg);
				continue;
			}
			try {
				field.setAccessible(true);
				field.set(obj, getInjectObject(fieldEntry.getValue(), field.getGenericType(), reg.getInitArgMap()));
			} catch (Exception e) {
				FrameLogger.error("无法将资源设置到注入属性：[" + reg + "]" + field, e);
				throw new RuntimeException("无法将资源设置到注入属性：[" + reg + "]" + field, e);
			} finally {
				field.setAccessible(false);
			}
		}
		// 运行注入方法
		List<MethodInfo> methodList = reg.getInjectMethods();
		Collections.sort(methodList, Collections.reverseOrder());
		for (MethodInfo methodInfo : methodList) {
			if (methodInfo == null) {
				FrameLogger.warn("发现不完整的注入方法信息：" + reg);
				continue;
			}
			Method method = methodInfo.getMethod();
			if (method == null) {
				FrameLogger.warn("发现不完整的注入方法信息：" + reg);
				continue;
			}
			try {
				List<InjectInfo> injectInfoList = methodInfo.getParams();
				Type[] parameterTypes = method.getGenericParameterTypes();
				Map<String, ArgInfo> initArgMap = reg.getInitArgMap();
				method.setAccessible(true);
				method.invoke(obj, getArguments(injectInfoList, parameterTypes, initArgMap));
			} catch (Exception e) {
				FrameLogger.error("无法运行注入方法：[" + reg + "]" + method, e);
				throw new RuntimeException("无法运行注入方法：[" + reg + "]" + method, e);
			} finally {
				method.setAccessible(false);
			}
		}
	}

	private Object[] getArguments(List<InjectInfo> injectInfoList, Type[] parameterTypes, Map<String, ArgInfo> initArgMap) {
		if (parameterTypes.length != injectInfoList.size()) {
			throw new IllegalArgumentException("注册信息中提供的参数数目与函数要求的数目不符");
		}
		Object[] arguments = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			arguments[i] = getInjectObject(injectInfoList.get(i), parameterTypes[i], initArgMap);
		}
		return arguments;
	}

	private Object getInjectObject(InjectInfo injectInfo, Type targetType, Map<String, ArgInfo> initArgMap) {
		Object obj = null;
		if (injectInfo != null) {
			String argName = injectInfo.getName();
			ArgInfo argInfo = injectInfo.getDefaultValue();
			if (argName != null && initArgMap.containsKey(argName)) {
				// 存在对应名称的初始化参数
				argInfo = initArgMap.get(argName);
			}
			if (argInfo == null) {
				FrameLogger.warn("未明确指定以下注入参数的值：" + argName);
			} else {
				RegistrationType argRegType = argInfo.getType();
				if (argRegType == null) {
					obj = TypeUtils.cast(argInfo.getValue(), targetType, null);
				} else {
					obj = lookup(argRegType, argInfo.getExtType(), argInfo.getValue());
				}
			}
		}
		// 修正为null的基本类型参数
		if (obj == null && targetType instanceof Class && ((Class<?>) targetType).isPrimitive()) {
			if (targetType == Boolean.TYPE) {
				obj = false;
			} else if (targetType == Character.TYPE) {
				obj = '\0';
			} else {
				obj = (byte) 0;
			}
		}
		return obj;
	}
}
