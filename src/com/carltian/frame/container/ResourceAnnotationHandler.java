package com.carltian.frame.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.carltian.frame.container.reg.ArgInfo;
import com.carltian.frame.db.DatabaseManager;
import com.carltian.frame.local.LocalizationManager;
import com.carltian.frame.remote.RemoteManager;
import com.carltian.frame.service.ServiceManager;
import com.carltian.frame.task.TaskManager;

/**
 * 根据数据类型设置需要注入的资源默认值
 * 
 * @author Carl Tian
 */
public class ResourceAnnotationHandler extends InjectAnnotationHandler {

	@Override
	public ArgInfo parseField(ArgInfo argInfo, Annotation annotation, Field field) {
		if (argInfo.getValue() == ResourceName.Undefined) {
			argInfo = addDefault(field.getGenericType(), argInfo);
		}
		return argInfo;
	}

	@Override
	public ArgInfo parseParam(ArgInfo argInfo, Annotation annotation, Type paramType, Annotation[] paramAnnotations) {
		if (argInfo.getValue() == ResourceName.Undefined) {
			argInfo = addDefault(paramType, argInfo);
		}
		return argInfo;
	}

	private ArgInfo addDefault(Type type, ArgInfo argInfo) {
		if (type instanceof Class<?>) {
			if (Container.class.isAssignableFrom((Class<?>) type)) {
				argInfo.setValue(ResourceName.Container);
			} else if (DatabaseManager.class.isAssignableFrom((Class<?>) type)) {
				argInfo.setValue(ResourceName.DatabaseManager);
			} else if (LocalizationManager.class.isAssignableFrom((Class<?>) type)) {
				argInfo.setValue(ResourceName.LocalizationManager);
			} else if (RemoteManager.class.isAssignableFrom((Class<?>) type)) {
				argInfo.setValue(ResourceName.RemoteManager);
			} else if (ServiceManager.class.isAssignableFrom((Class<?>) type)) {
				argInfo.setValue(ResourceName.ServiceManager);
			} else if (TaskManager.class.isAssignableFrom((Class<?>) type)) {
				argInfo.setValue(ResourceName.TaskManager);
			} else {
				argInfo = null;
			}
		} else {
			throw new UnsupportedOperationException("抱歉，暂时不支持依据参数化类型等特殊形式的类型确定资源名称，请使用注解明确的指定资源名称，或使用普通类型定义参数");
		}
		return argInfo;
	}
}
