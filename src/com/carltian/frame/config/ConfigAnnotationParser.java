package com.carltian.frame.config;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.util.TypeUtils;
import com.carltian.frame.container.InjectAnnotationHandler;
import com.carltian.frame.container.annotation.ContainerConstructor;
import com.carltian.frame.container.annotation.ExtTypeInject;
import com.carltian.frame.container.annotation.InitArg;
import com.carltian.frame.container.annotation.InjectMethod;
import com.carltian.frame.container.annotation.SysTypeInject;
import com.carltian.frame.container.reg.ArgInfo;
import com.carltian.frame.container.reg.InjectInfo;
import com.carltian.frame.container.reg.MethodInfo;
import com.carltian.frame.container.reg.Registration;
import com.carltian.frame.container.reg.RegistrationImpl;
import com.carltian.frame.container.reg.RegistrationType;
import com.carltian.frame.remote.ActionConfig;
import com.carltian.frame.remote.annotation.ActionRegistration;
import com.carltian.frame.service.ServiceConfig;
import com.carltian.frame.service.annotation.ServiceRegistration;
import com.carltian.frame.task.TaskConfig;
import com.carltian.frame.task.annotation.TaskRegistration;
import com.carltian.frame.util.ClassScanner;
import com.carltian.frame.util.ClassTraverse;
import com.carltian.frame.util.FrameLogger;

/**
 * 注解配置解析器，解析类中注解的配置信息
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class ConfigAnnotationParser {
	private static final ConcurrentHashMap<Class<? extends InjectAnnotationHandler>, InjectAnnotationHandler> handlerCache = new ConcurrentHashMap<Class<? extends InjectAnnotationHandler>, InjectAnnotationHandler>();

	/**
	 * 扫描任务包，并将包中类的注解转化为配置信息
	 * 
	 * @param taskConfigList
	 *           已经通过XML配置的任务配置信息列表
	 * @param taskPkgList
	 *           XML配置的需要扫描的包路径列表
	 * @return 合并后的所有任务配置信息列表
	 */
	public static List<TaskConfig> parseTaskPkg(List<TaskConfig> taskConfigList, List<String> taskPkgList) {
		if (taskConfigList == null) {
			taskConfigList = new LinkedList<TaskConfig>();
		}
		// 创建比对信息
		final HashSet<Class<?>> clazzSet = new HashSet<Class<?>>(taskConfigList.size());
		for (TaskConfig config : taskConfigList) {
			clazzSet.add(config.getRegistration().getClazz());
		}
		// 复制已有的配置
		final List<TaskConfig> allTaskConfigList = new LinkedList<TaskConfig>(taskConfigList);
		if (taskPkgList != null) {
			for (String pkg : taskPkgList) {
				try {
					ClassScanner.scan(pkg, new ClassTraverse<TimerTask>(TimerTask.class) {
						@Override
						public void forEach(Class<? extends TimerTask> clazz) {
							if (clazzSet.contains(clazz)) {
								// 包内类与配置类重复 or 包内类已经被解析过
								return;
							}
							TaskConfig config = parseTaskClass(clazz);
							if (config == null) {
								// 类内无配置信息
								return;
							}
							clazzSet.add(config.getRegistration().getClazz());
							allTaskConfigList.add(config);
						}
					});
				} catch (IOException e) {
					FrameLogger.error("无法读取Task配置");
					throw new RuntimeException(e);
				}
			}
		}
		return allTaskConfigList;
	}

	/**
	 * 扫描服务包，并将包中类的注解转化为配置信息
	 * 
	 * @param serviceConfigList
	 *           已经通过XML配置的服务配置信息列表
	 * @param servicePkgList
	 *           XML配置的需要扫描的包路径列表
	 * @return 合并后的所有服务配置信息列表
	 */
	public static List<ServiceConfig> parseServicePkg(List<ServiceConfig> serviceConfigList, List<String> servicePkgList) {
		if (serviceConfigList == null) {
			serviceConfigList = new LinkedList<ServiceConfig>();
		}
		// 创建比对信息
		final HashSet<Class<?>> clazzSet = new HashSet<Class<?>>(serviceConfigList.size());
		for (ServiceConfig config : serviceConfigList) {
			clazzSet.add(config.getRegistration().getClazz());
		}
		// 复制已有的配置
		final List<ServiceConfig> allServiceConfigList = new LinkedList<ServiceConfig>(serviceConfigList);
		if (servicePkgList != null) {
			for (String pkg : servicePkgList) {
				try {
					ClassScanner.scan(pkg, new ClassTraverse<Object>(Object.class) {
						@Override
						public void forEach(Class<? extends Object> clazz) {
							if (clazzSet.contains(clazz)) {
								// 包内类与配置类重复 or 包内类已经被解析过
								return;
							}
							ServiceConfig config = parseServiceClass(clazz);
							if (config == null) {
								// 类内无配置信息
								return;
							}
							clazzSet.add(config.getRegistration().getClazz());
							allServiceConfigList.add(config);
						}
					});
				} catch (IOException e) {
					FrameLogger.error("无法读取Service配置");
					throw new RuntimeException(e);
				}
			}
		}
		return allServiceConfigList;
	}

	/**
	 * 扫描动作包，并将包中类的注解转化为配置信息
	 * 
	 * @param actionConfigList
	 *           已经通过XML配置的动作配置信息列表
	 * @param actionPkgList
	 *           XML配置的需要扫描的包路径列表
	 * @return 合并后的所有动作配置信息列表
	 */
	public static List<ActionConfig> parseActionPkg(List<ActionConfig> actionConfigList, List<String> actionPkgList) {
		if (actionConfigList == null) {
			actionConfigList = new LinkedList<ActionConfig>();
		}
		// 创建比对信息
		final HashSet<Class<?>> clazzSet = new HashSet<Class<?>>(actionConfigList.size());
		for (ActionConfig config : actionConfigList) {
			clazzSet.add(config.getRegistration().getClazz());
		}
		// 复制已有的配置
		final List<ActionConfig> allActionConfigList = new LinkedList<ActionConfig>(actionConfigList);
		if (actionPkgList != null) {
			for (String pkg : actionPkgList) {
				try {
					ClassScanner.scan(pkg, new ClassTraverse<Object>(Object.class) {
						@Override
						public void forEach(Class<? extends Object> clazz) {
							if (clazzSet.contains(clazz)) {
								// 包内类与配置类重复 or 包内类已经被解析过
								return;
							}
							ActionConfig config = parseActionClass(clazz);
							if (config == null) {
								// 类内无配置信息
								return;
							}
							clazzSet.add(config.getRegistration().getClazz());
							allActionConfigList.add(config);
						}
					});
				} catch (IOException e) {
					FrameLogger.error("无法读取Action配置");
					throw new RuntimeException(e);
				}
			}
		}
		return allActionConfigList;
	}

	/**
	 * 从任务类中注解抽取并解析配置信息
	 * 
	 * @param clazz
	 *           需要解析的类
	 * @return 类中注解包含的配置信息
	 */
	public static TaskConfig parseTaskClass(Class<? extends TimerTask> clazz) {
		TaskRegistration annotation = clazz.getAnnotation(TaskRegistration.class);
		if (annotation == null) {
			// 无效类
			return null;
		}
		TaskConfig config = new TaskConfig();
		config.setRegistration(parseRegistration(RegistrationType.Task, annotation.name(), clazz, true));
		config.setDelay(annotation.delay());
		config.setPeriod(annotation.period());
		return config;
	}

	/**
	 * 从服务类中注解抽取并解析配置信息
	 * 
	 * @param clazz
	 *           需要解析的类
	 * @return 类中注解包含的配置信息
	 */
	public static ServiceConfig parseServiceClass(Class<?> clazz) {
		ServiceRegistration annotation = clazz.getAnnotation(ServiceRegistration.class);
		if (annotation == null) {
			// 无效类
			return null;
		}
		ServiceConfig config = new ServiceConfig();
		config.setRegistration(parseRegistration(RegistrationType.Service, annotation.name(), clazz,
				annotation.singleton()));
		return config;
	}

	/**
	 * 从动作类中注解抽取并解析配置信息
	 * 
	 * @param clazz
	 *           需要解析的类
	 * @return 类中注解包含的配置信息
	 */
	public static ActionConfig parseActionClass(Class<?> clazz) {
		ActionRegistration annotation = clazz.getAnnotation(ActionRegistration.class);
		if (annotation == null) {
			// 无效类
			return null;
		}
		ActionConfig config = new ActionConfig();
		config.setRegistration(parseRegistration(RegistrationType.Action, annotation.name(), clazz,
				annotation.singleton()));
		config.setModule(annotation.module());
		return config;
	}

	/**
	 * 从类中解析出向容器注册所需的注册信息，注册类型为{@link RegistrationType}中包含的基本类型
	 * 
	 * @param type
	 *           注册类型
	 * @param name
	 *           注册名称
	 * @param clazz
	 *           需要解析的类
	 * @param singleton
	 *           是否注册为单例
	 * @return 类中注解包含的注册信息
	 */
	public static Registration parseRegistration(RegistrationType type, Object name, Class<?> clazz, boolean singleton) {
		return parseRegistration(type, null, name, clazz, singleton);
	}

	/**
	 * 从类中解析出向容器注册所需的注册信息，注册类型为扩展类型
	 * 
	 * @param extType
	 *           注册扩展类型
	 * @param name
	 *           注册名称
	 * @param clazz
	 *           需要解析的类
	 * @param singleton
	 *           是否注册为单例
	 * @return 类中注解包含的注册信息
	 */
	public static Registration parseRegistration(String extType, Object name, Class<?> clazz, boolean singleton) {
		return parseRegistration(RegistrationType.Extension, extType, name, clazz, singleton);
	}

	/**
	 * 从类中解析出向容器注册所需的注册信息
	 * 
	 * @param type
	 *           注册类型
	 * @param extType
	 *           注册扩展类型
	 * @param name
	 *           注册名称
	 * @param clazz
	 *           需要解析的类
	 * @param singleton
	 *           是否注册为单例
	 * @return 类中注解包含的注册信息
	 */
	private static Registration parseRegistration(RegistrationType type, String extType, Object name, Class<?> clazz,
			boolean singleton) {
		Annotation[][] paramsAnnotations;
		Type[] paramTypes;
		List<InjectInfo> paramInfoList;
		RegistrationImpl reg = new RegistrationImpl(type, extType, TypeUtils.castToString(name), clazz, singleton);
		// 寻找构造函数
		Constructor<?>[] constructors = clazz.getConstructors();
		Constructor<?> constructor = null;
		for (Constructor<?> orgConstructor : constructors) {
			if (orgConstructor.getAnnotation(ContainerConstructor.class) != null) {
				constructor = orgConstructor;
			}
		}
		if (constructor == null) {
			try {
				constructor = clazz.getConstructor();
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("无法找到可用的构造函数：" + clazz.getName(), e);
			}
		}
		reg.setConstructor(constructor);
		// 分析构造参数
		paramsAnnotations = constructor.getParameterAnnotations();
		paramTypes = constructor.getParameterTypes();
		paramInfoList = reg.getConstructorParams();
		for (int i = 0; i < paramTypes.length; i++) {
			InjectInfo injectInfo = getInjectInfo(paramsAnnotations[i], paramTypes[i]);
			if (injectInfo == null) {
				throw new RuntimeException("构造函数参数[" + (paramInfoList.size() + 1) + "]缺少注入信息：" + constructor);
			}
			paramInfoList.add(injectInfo);
		}
		// 解析方法注入信息
		Method[] methods = clazz.getMethods();
		List<MethodInfo> methodList = reg.getInjectMethods();
		for (Method method : methods) {
			InjectMethod annotation = method.getAnnotation(InjectMethod.class);
			if (annotation != null) {
				MethodInfo methodInfo = new MethodInfo(method, annotation.priority());
				paramsAnnotations = method.getParameterAnnotations();
				paramTypes = method.getParameterTypes();
				paramInfoList = methodInfo.getParams();
				for (int i = 0; i < paramTypes.length; i++) {
					InjectInfo injectInfo = getInjectInfo(paramsAnnotations[i], paramTypes[i]);
					if (injectInfo == null) {
						throw new RuntimeException("注入方法参数[" + (paramInfoList.size() + 1) + "]缺少注入信息：" + method);
					}
					paramInfoList.add(injectInfo);
				}
				methodList.add(methodInfo);
			}
		}
		// 解析成员注入信息
		Map<Field, InjectInfo> fieldMap = reg.getInjectFields();
		for (Class<?> curClazz = clazz; curClazz != Object.class; curClazz = curClazz.getSuperclass()) {
			Field[] fields = curClazz.getDeclaredFields();
			for (Field field : fields) {
				InjectInfo injectInfo = getInjectInfo(field.getAnnotations(), field);
				if (injectInfo != null) {
					fieldMap.put(field, injectInfo);
				}
			}
		}
		return reg;
	}

	/**
	 * 依据注解解析成员变量或方法为注入信息
	 * 
	 * @param annotations
	 *           成员变量或方法上的所有注解
	 * @param relatedObj
	 *           成员方法返回值的类型({@link Type})或成员变量对象({@link Field})
	 * @return 注入信息
	 */
	private static InjectInfo getInjectInfo(Annotation[] annotations, Object relatedObj) {
		// 解析注入信息
		InjectInfo info = null;
		ArgInfo arg = null;
		for (Annotation annotation : annotations) {
			if (annotation instanceof InitArg) {
				// 参数名称注解
				info = new InjectInfo(((InitArg) annotation).value(), null);
			} else if (arg == null) {
				// 可能是各种注入注解
				Class<? extends Annotation> annotationClazz = annotation.annotationType();
				SysTypeInject sysMeta = annotationClazz.getAnnotation(SysTypeInject.class);
				ExtTypeInject extMeta = annotationClazz.getAnnotation(ExtTypeInject.class);
				Object type;
				String keyField;
				Class<? extends InjectAnnotationHandler> handlerClazz;
				if (sysMeta != null) {
					// 找到注入注解
					type = sysMeta.type();
					keyField = sysMeta.keyField();
					handlerClazz = sysMeta.handler();
				} else if (extMeta != null) {
					// 找到扩展注入注解
					type = extMeta.name();
					if ("".equals(type)) {
						type = annotationClazz.getSimpleName();
					}
					keyField = extMeta.keyField();
					handlerClazz = extMeta.handler();
				} else {
					continue;
				}
				// 生成ArgInfo
				try {
					Method method = annotationClazz.getMethod(keyField);
					if (type instanceof RegistrationType) {
						arg = new ArgInfo((RegistrationType) type, method.invoke(annotation));
					} else if (type instanceof String) {
						arg = new ArgInfo((String) type, method.invoke(annotation));
					} else {
						throw new UnsupportedOperationException();
					}
				} catch (Exception e) {
					throw new RuntimeException("无法获取注入注解的指定属性：" + annotationClazz.getName() + " / " + keyField, e);
				}
				// 调用Handler
				try {
					InjectAnnotationHandler handler = handlerCache.get(handlerClazz);
					if (handler == null) {
						// 可能需要新建实例
						synchronized (handlerCache) {
							// 加写入锁
							handler = handlerCache.get(handlerClazz);
							if (handler == null) {
								// 需要新建实例
								handler = handlerClazz.newInstance();
								handlerCache.put(handlerClazz, handler);
							}
						}
					}
					if (relatedObj instanceof Type) {
						arg = handler.parseParam(arg, annotation, (Type) relatedObj, annotations);
					} else if (relatedObj instanceof Field) {
						arg = handler.parseField(arg, annotation, (Field) relatedObj);
					}
				} catch (Exception e) {
					throw new RuntimeException("注入注解的Handler调用失败：" + annotationClazz.getName(), e);
				}
			}
		}
		if (arg != null) {
			// 有参数
			if (info == null) {
				// 没有initArg名称
				info = new InjectInfo();
			}
			info.setDefaultValue(arg);
		}
		return info;
	}
}
