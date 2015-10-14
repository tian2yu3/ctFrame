package com.carltian.frame;

import java.util.EnumSet;
import java.util.UUID;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

import org.atmosphere.cpr.AtmosphereServlet;

import com.carltian.frame.config.ConfigAnnotationParser;
import com.carltian.frame.config.ConfigParser;
import com.carltian.frame.config.FrameConfig;
import com.carltian.frame.container.Container;
import com.carltian.frame.container.ContainerImpl;
import com.carltian.frame.container.ResourceName;
import com.carltian.frame.container.reg.ArgInfo;
import com.carltian.frame.container.reg.Registration;
import com.carltian.frame.container.reg.RegistrationType;
import com.carltian.frame.local.LocalizationManagerImpl;
import com.carltian.frame.remote.ActionConfig;
import com.carltian.frame.remote.RemoteManager;
import com.carltian.frame.remote.RemoteManagerImpl;
import com.carltian.frame.service.ServiceConfig;
import com.carltian.frame.service.ServiceManager;
import com.carltian.frame.service.ServiceManagerImpl;
import com.carltian.frame.task.TaskConfig;
import com.carltian.frame.task.TaskManager;
import com.carltian.frame.task.TaskManagerImpl;
import com.carltian.frame.util.FrameLogger;

/**
 * 框架监听器，负责初始化框架，当框架启动时自动运行
 * 
 * @version 1.0
 * @author Carl Tian
 */
@WebListener
public class AppFrameListener implements ServletContextListener {

	static public final String CONFIG_PATH_PARAM = "AppFrameConfig";
	static public final String DEFAULT_CONFIG_PATH = "/WEB-INF/frame.xml";

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		CurrentContext.createContext();
		try {
			ServletContext context = event.getServletContext();
			CurrentContext.setServletContext(context);
			// 考虑不完全初始化的情况（初始化过程中就出现错误）
			Container container = FrameContext.getContainer();
			if (container != null) {
				TaskManager taskManager = container.lookup(RegistrationType.Resource, ResourceName.TaskManager);
				if (taskManager != null) {
					taskManager.stop();
				}
			}
		} finally {
			CurrentContext.destroyContext();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		CurrentContext.createContext();
		try {
			Registration reg;
			ServletContext context = event.getServletContext();
			CurrentContext.setServletContext(context);
			// 初始化config
			FrameConfig config = beforeInitConfig();
			if (config == null) {
				config = new FrameConfig();
			}
			String configPath = context.getInitParameter(CONFIG_PATH_PARAM);
			if (configPath == null || "".equals(configPath)) {
				configPath = DEFAULT_CONFIG_PATH;
			}
			ConfigParser.parse(context.getResourceAsStream(configPath), config);
			afterInitConfig(config);
			// 写入context
			FrameContext.setDefaultLocale(config.getDefaultLocale());
			// 初始化ioc容器
			ContainerImpl container = new ContainerImpl(ResourceName.Container);
			FrameContext.setContainer(container);
			// 注册DatabaseManager
			if (config.getDatabaseManagerRegistration() != null) {
				container.register(config.getDatabaseManagerRegistration());
			}
			// 注册LocalizationManager
			reg = ConfigAnnotationParser.parseRegistration(RegistrationType.Resource, ResourceName.LocalizationManager,
					LocalizationManagerImpl.class, true);
			reg.getInitArgMap().put("loaderClass", new ArgInfo(config.getLocalizationLoaderClass()));
			container.register(reg);
			// 注册ServiceManager
			reg = ConfigAnnotationParser.parseRegistration(RegistrationType.Resource, ResourceName.ServiceManager,
					ServiceManagerImpl.class, true);
			container.register(reg);
			// 注册TaskManager
			reg = ConfigAnnotationParser.parseRegistration(RegistrationType.Resource, ResourceName.TaskManager,
					TaskManagerImpl.class, true);
			container.register(reg);
			// 注册RemoteManager
			reg = ConfigAnnotationParser.parseRegistration(RegistrationType.Resource, ResourceName.RemoteManager,
					RemoteManagerImpl.class, true);
			container.register(reg);
			// 初始化全部Resource
			container.lookup(RegistrationType.Resource, ResourceName.DatabaseManager);
			container.lookup(RegistrationType.Resource, ResourceName.LocalizationManager);
			ServiceManager serviceManager = container.lookup(RegistrationType.Resource, ResourceName.ServiceManager);
			TaskManager taskManager = container.lookup(RegistrationType.Resource, ResourceName.TaskManager);
			RemoteManager remoteManager = container.lookup(RegistrationType.Resource, ResourceName.RemoteManager);
			// 注册Service
			for (ServiceConfig service : config.getServiceConfigList()) {
				serviceManager.register(service);
			}
			// 注册Task
			for (TaskConfig task : config.getTaskConfigList()) {
				taskManager.register(task);
			}
			// 注册Module
			for (ActionConfig action : config.getActionConfigList()) {
				remoteManager.register(action);
			}

			// 增加request filter
			FrameContext.setRequestFilterName(UUID.randomUUID().toString());
			FilterRegistration.Dynamic requestReg = context.addFilter(FrameContext.getRequestFilterName(),
					RequestFilter.class);
			requestReg.setAsyncSupported(true);
			requestReg.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD,
					DispatcherType.INCLUDE, DispatcherType.ERROR, DispatcherType.ASYNC), false, "/*");
			// 增加remote servlet
			String remotePathName = config.getRemotePathName();
			if (remotePathName != null) {
				// 规范化参数
				if (remotePathName.contains("/") || remotePathName.contains("\\")) {
					FrameLogger.error("remotePathName不允许包含/与\\");
					throw new RuntimeException("remotePathName不允许包含/与\\");
				}
				FrameContext.setSupportPushEvent(true);// FIXME 配置化
				FrameContext.setRemotePathName(remotePathName);
				FrameContext.setRemoteServletName(UUID.randomUUID().toString());
				ServletRegistration.Dynamic remoteReg = context.addServlet(FrameContext.getRemoteServletName(),
						AtmosphereServlet.class);
				remoteReg.setAsyncSupported(true);
				remoteReg.addMapping(new String[] { "/" + FrameContext.getRemotePathName() + "/*" });
			}
			// 初始化TaskManager
			taskManager.start();
		} finally {
			CurrentContext.destroyContext();
		}
	}

	protected void afterInitConfig(FrameConfig config) {
		// FIXME 使用AOP代替
	}

	protected FrameConfig beforeInitConfig() {
		// FIXME 使用AOP代替
		return null;
	}

}
