package com.carltian.frame.remote;

import com.carltian.frame.container.reg.Registrable;
import com.carltian.frame.container.reg.Registration;

/**
 * Action配置的值对象，每个实例代表一个Action配置。
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class ActionConfig implements Registrable {
	private Registration registration;
	private String module = "";

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
	 * 读取远程调用时的模块名称
	 * 
	 * @return 远程调用时的模块名称
	 */
	public String getModule() {
		return module;
	}

	/**
	 * 设置远程调用时的模块名称
	 * 
	 * @param module
	 *           远程调用时的模块名称
	 */
	public void setModule(String module) {
		this.module = module;
	}
}
