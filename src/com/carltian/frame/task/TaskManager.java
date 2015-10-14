package com.carltian.frame.task;

/**
 * 任务管理器接口
 * 
 * @version 1.0
 * @author Carl Tian
 */
public interface TaskManager {

	/**
	 * 框架启动时，系统每读取到一条任务配置，就会调用一次该函数以注册任务。
	 * 
	 * @param config
	 *           任务配置信息
	 * @see TaskConfig
	 */
	public abstract void register(TaskConfig config);

	/**
	 * 启动任务系统
	 */
	public abstract void start();

	/**
	 * 停止任务系统
	 */
	public abstract void stop();
}
