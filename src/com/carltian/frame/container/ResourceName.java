package com.carltian.frame.container;

/**
 * 容器中的资源名称枚举类
 * 
 * @version 1.0
 * @author Carl Tian
 */
public enum ResourceName {
	/**
	 * 未定义
	 */
	Undefined,
	/**
	 * 容器管理器
	 */
	Container,

	/**
	 * 本地化管理器
	 */
	LocalizationManager,

	/**
	 * 任务管理器
	 */
	TaskManager,

	/**
	 * 服务管理器
	 */
	ServiceManager,

	/**
	 * 数据库管理器
	 */
	DatabaseManager,

	/**
	 * 远程管理器
	 */
	RemoteManager;

	@Override
	public String toString() {
		return this.getClass().getName() + "[" + this.name() + "]";
	}

}
