package com.carltian.frame.service;

import com.carltian.frame.container.ContainerImpl;
import com.carltian.frame.container.annotation.Resource;

/**
 * 服务管理器实现类
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class ServiceManagerImpl implements ServiceManager {
	@Resource
	private ContainerImpl container;

	@Override
	public void register(ServiceConfig config) {
		// 加入容器
		container.register(config.getRegistration());
	}
}
