package com.carltian.frame.container.reg;

import java.util.Map;

/**
 * 资源注册信息接口
 * 
 * @version 1.0
 * @author Carl Tian
 */
public interface Registration {

	/**
	 * 读取注册类型
	 * 
	 * @return 注册的类型
	 */
	public abstract RegistrationType getType();

	/**
	 * 读取注册扩展类型
	 * 
	 * @return 注册的扩展类型
	 */
	public abstract String getExtType();

	/**
	 * 读取注册名称
	 * 
	 * @return 注册的唯一名称
	 */
	public abstract String getName();

	/**
	 * 设置注册名称
	 * 
	 * @param name
	 *           注册的唯一名称
	 */
	public abstract void setName(String name);

	/**
	 * 读取注册类
	 * 
	 * @return 注册的类
	 */
	public abstract Class<?> getClazz();

	/**
	 * 读取是否是单例
	 * 
	 * @return 是否是单例
	 */
	public abstract boolean isSingleton();

	/**
	 * 设置是否是单例
	 * 
	 * @param singleton
	 *           是否是单例
	 */
	public abstract void setSingleton(boolean singleton);

	/**
	 * 读取初始化参数集合
	 * 
	 * @return 初始化所需的参数集合
	 */
	public abstract Map<String, ArgInfo> getInitArgMap();
}
