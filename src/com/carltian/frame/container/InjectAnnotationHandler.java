package com.carltian.frame.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.carltian.frame.container.annotation.ExtTypeInject;
import com.carltian.frame.container.reg.ArgInfo;

/**
 * 扩展类型注解注入时的默认Handler类，全部Handler必须是该类或其子类<br/>
 * 可以在{@link ExtTypeInject}中指定，以便在容器处理该注解时执行自定义注入动作
 * 
 * @author Carl Tian
 */
public class InjectAnnotationHandler {

	/**
	 * 处理成员变量的参数信息
	 * 
	 * @param argInfo
	 *           依据{@link ExtTypeInject}信息生成的{@link ArgInfo}
	 * @param annotation
	 *           需要当前Handler处理的对应注解
	 * @param field
	 *           需要当前Handler生成参数信息的对应成员变量
	 * @return 返回注入参数信息，如果返回{@code null}则表示忽略该注入注解
	 */
	public ArgInfo parseField(ArgInfo argInfo, Annotation annotation, Field field) {
		return argInfo;
	}

	/**
	 * 处理注入函数参数的参数信息
	 * 
	 * @param argInfo
	 *           依据{@link ExtTypeInject}信息生成的{@link ArgInfo}
	 * @param annotation
	 *           需要当前Handler处理的对应注解
	 * @param paramType
	 *           需要当前Handler生成参数信息的对应注入函数参数的类型
	 * @param paramAnnotations
	 *           需要当前Handler生成参数信息的对应注入函数参数的全部注解
	 * @return 返回注入参数信息，如果返回{@code null}则表示忽略该注入注解
	 */
	public ArgInfo parseParam(ArgInfo argInfo, Annotation annotation, Type paramType, Annotation[] paramAnnotations) {
		return argInfo;
	}
}
