package com.carltian.frame.container.reg;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 资源注册信息实现类<br/>
 * 由于ContainerImpl对注册信息检查有一些默认规则，因此为了防止子类没有遵守这些隐含规则，暂不允许注册信息类被继承
 * 
 * @version 1.0
 * @author Carl Tian
 */
public final class RegistrationImpl implements Registration, Cloneable {

	/**
	 * 资源类型
	 */
	private RegistrationType type = null;

	/**
	 * 资源扩展类型
	 */
	private String extType = null;

	/**
	 * 资源名称
	 */
	private String name = null;

	/**
	 * 资源类
	 */
	private Class<?> clazz = null;

	/**
	 * 在容器中是否为单例模式
	 */
	private boolean singleton = true;

	/**
	 * 构造函数
	 */
	private Constructor<?> constructor = null;

	/**
	 * 构造函数参数信息
	 */
	private final List<InjectInfo> constructorParams = new ArrayList<InjectInfo>();

	/**
	 * 需要注入的方法信息
	 */
	private final List<MethodInfo> injectMethods = new ArrayList<MethodInfo>();

	/**
	 * 需要注入的成员信息
	 */
	private final Map<Field, InjectInfo> injectFields = new HashMap<Field, InjectInfo>();

	/**
	 * 初始化参数
	 */
	private final Map<String, ArgInfo> initArgMap = new HashMap<String, ArgInfo>();

	public RegistrationImpl() {
	}

	public RegistrationImpl(RegistrationType type, String extType, String name, Class<?> clazz, boolean singleton) {
		this.type = type;
		this.extType = extType;
		this.name = name;
		this.clazz = clazz;
		this.singleton = singleton;
	}

	@Override
	public String toString() {
		if (type == RegistrationType.Extension) {
			return "[EXT]" + extType + " / " + name + " / " + hashCode();
		} else {
			return type + " / " + name + " / " + hashCode();
		}
	}

	@Override
	public RegistrationImpl clone() {
		RegistrationImpl obj = new RegistrationImpl(type, extType, name, clazz, singleton);
		obj.constructor = constructor;
		// 深拷贝constructorParams
		for (InjectInfo constructorParam : constructorParams) {
			if (constructorParam == null) {
				obj.constructorParams.add(null);
			} else {
				obj.constructorParams.add(constructorParam.clone());
			}
		}
		// 深拷贝injectMethods
		for (MethodInfo injectMethod : injectMethods) {
			if (injectMethod == null) {
				obj.injectMethods.add(null);
			} else {
				obj.injectMethods.add(injectMethod.clone());
			}
		}
		// 深拷贝injectFields
		Set<Entry<Field, InjectInfo>> injectFieldEntrySet = injectFields.entrySet();
		for (Entry<Field, InjectInfo> injectFieldEntry : injectFieldEntrySet) {
			InjectInfo injectInfo = injectFieldEntry.getValue();
			if (injectInfo == null) {
				obj.injectFields.put(injectFieldEntry.getKey(), null);
			} else {
				obj.injectFields.put(injectFieldEntry.getKey(), injectInfo.clone());
			}
		}
		// 深拷贝initArgMap
		Set<Entry<String, ArgInfo>> initArgMapEntrySet = initArgMap.entrySet();
		for (Entry<String, ArgInfo> initArgMapEntry : initArgMapEntrySet) {
			ArgInfo argInfo = initArgMapEntry.getValue();
			if (argInfo == null) {
				obj.initArgMap.put(initArgMapEntry.getKey(), null);
			} else {
				obj.initArgMap.put(initArgMapEntry.getKey(), argInfo.clone());
			}
		}
		return obj;
	}

	@Override
	public RegistrationType getType() {
		return type;
	}

	public void setType(RegistrationType type) {
		this.type = type;
	}

	@Override
	public String getExtType() {
		return extType;
	}

	public void setExtType(String extType) {
		this.extType = extType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public boolean isSingleton() {
		return singleton;
	}

	@Override
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public void setConstructor(Constructor<?> constructor) {
		this.constructor = constructor;
	}

	public List<InjectInfo> getConstructorParams() {
		return constructorParams;
	}

	public List<MethodInfo> getInjectMethods() {
		return injectMethods;
	}

	public Map<Field, InjectInfo> getInjectFields() {
		return injectFields;
	}

	@Override
	public Map<String, ArgInfo> getInitArgMap() {
		return initArgMap;
	}

}
