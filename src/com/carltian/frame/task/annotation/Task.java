package com.carltian.frame.task.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.carltian.frame.container.FieldNameAsDefaultHandler;
import com.carltian.frame.container.annotation.SysTypeInject;
import com.carltian.frame.container.reg.RegistrationType;

/**
 * Annotate a parameter or field what name it is in the task container.
 * 
 * @author Carl Tian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Documented
@SysTypeInject(type = RegistrationType.Task, handler = FieldNameAsDefaultHandler.class)
public @interface Task {
	/**
	 * The name string in the task container, default is the field name
	 */
	String value() default "";
}
