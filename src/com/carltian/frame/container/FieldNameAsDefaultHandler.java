package com.carltian.frame.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.carltian.frame.container.reg.ArgInfo;

/**
 * 实现以成员名称作为默认值的Handler
 * 
 * @author Carl Tian
 */
public class FieldNameAsDefaultHandler extends InjectAnnotationHandler {

	@Override
	public ArgInfo parseField(ArgInfo argInfo, Annotation annotation, Field field) {
		if (argInfo.getValue() == null || "".equals(argInfo.getValue())) {
			argInfo.setValue(field.getName());
		}
		return argInfo;
	}

	@Override
	public ArgInfo parseParam(ArgInfo argInfo, Annotation annotation, Type paramType, Annotation[] paramAnnotations) {
		if (argInfo.getValue() == null || "".equals(argInfo.getValue())) {
			throw new RuntimeException("无法从函数参数推导默认名称，请使用注解明确的指定名称");
		}
		return argInfo;
	}

}
