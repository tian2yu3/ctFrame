package com.carltian.frame.task;

import com.carltian.frame.container.reg.Registrable;
import com.carltian.frame.container.reg.Registration;

/**
 * 任务配置的值对象，每个实例代表一个任务配置。
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class TaskConfig implements Registrable {
	private Registration registration;
	private long delay = 0;
	private long period = 0;

	/**
	 * 读取注册信息
	 * 
	 * @return 注册信息对象
	 */
	@Override
	public Registration getRegistration() {
		return registration;
	}

	/**
	 * 设置注册信息
	 * 
	 * @param registration
	 *           注册信息对象
	 */
	@Override
	public void setRegistration(Registration registration) {
		this.registration = registration;
	}

	/**
	 * 读取任务启动延迟
	 * 
	 * @return 从任务被注册且任务管理器被启动之后，到任务首次被运行间隔的毫秒数。
	 */
	public long getDelay() {
		return delay;
	}

	/**
	 * 设置任务启动延迟
	 * 
	 * @param delay
	 *           从任务被注册且任务管理器被启动之后，到任务首次被运行间隔的毫秒数。
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	/**
	 * 读取任务运行间隔
	 * 
	 * @return 任务每次运行之间间隔的毫秒数
	 */
	public long getPeriod() {
		return period;
	}

	/**
	 * 设置任务运行间隔
	 * 
	 * @param period
	 *           任务每次运行之间间隔的毫秒数
	 */
	public void setPeriod(long period) {
		this.period = period;
	}

}
