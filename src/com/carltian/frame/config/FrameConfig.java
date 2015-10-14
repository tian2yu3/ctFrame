package com.carltian.frame.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.carltian.frame.container.reg.Registration;
import com.carltian.frame.local.LocalizationLoader;
import com.carltian.frame.remote.ActionConfig;
import com.carltian.frame.service.ServiceConfig;
import com.carltian.frame.task.TaskConfig;

public class FrameConfig {

	private Registration databaseManagerRegistration = null;
	private Locale defaultLocale = Locale.getDefault();
	private Class<? extends LocalizationLoader> localizationLoaderClass = null;
	private String remotePathName = null;
	private List<TaskConfig> taskConfigList = new ArrayList<TaskConfig>();
	private List<ActionConfig> actionConfigList = new ArrayList<ActionConfig>();
	private List<ServiceConfig> serviceConfigList = new ArrayList<ServiceConfig>();

	public Registration getDatabaseManagerRegistration() {
		return databaseManagerRegistration;
	}

	public void setDatabaseManagerRegistration(Registration databaseManagerRegistration) {
		this.databaseManagerRegistration = databaseManagerRegistration;
	}

	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public Class<? extends LocalizationLoader> getLocalizationLoaderClass() {
		return localizationLoaderClass;
	}

	public void setLocalizationLoaderClass(Class<? extends LocalizationLoader> localizationLoaderClass) {
		this.localizationLoaderClass = localizationLoaderClass;
	}

	public List<TaskConfig> getTaskConfigList() {
		return taskConfigList;
	}

	public void setTaskConfigList(List<TaskConfig> taskConfigList) {
		this.taskConfigList = taskConfigList;
	}

	public String getRemotePathName() {
		return remotePathName;
	}

	public void setRemotePathName(String remotePathName) {
		this.remotePathName = remotePathName;
	}

	public List<ActionConfig> getActionConfigList() {
		return actionConfigList;
	}

	public void setActionConfigList(List<ActionConfig> actionConfigList) {
		this.actionConfigList = actionConfigList;
	}

	public List<ServiceConfig> getServiceConfigList() {
		return serviceConfigList;
	}

	public void setServiceConfigList(List<ServiceConfig> serviceConfigList) {
		this.serviceConfigList = serviceConfigList;
	}

}
