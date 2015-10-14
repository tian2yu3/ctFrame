package com.carltian.frame.service;

/**
 * 服务管理器接口
 * 
 * @version 1.0
 * @author Carl Tian
 */
public interface ServiceManager {

	/**
	 * 框架启动时，系统每读取到一条服务配置，就会调用一次该函数以注册服务。
	 * 
	 * @param config
	 *           服务配置信息
	 * @see ServiceConfig
	 */
	public abstract void register(ServiceConfig config);

}
