package com.carltian.frame.service;

import com.carltian.frame.container.reg.Registrable;
import com.carltian.frame.container.reg.Registration;

/**
 * 服务配置的值对象，每个实例代表一个服务配置。
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class ServiceConfig implements Registrable {
	private Registration registration;

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
}
