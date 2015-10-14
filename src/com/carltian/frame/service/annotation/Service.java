package com.carltian.frame.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.carltian.frame.container.FieldNameAsDefaultHandler;
import com.carltian.frame.container.annotation.SysTypeInject;
import com.carltian.frame.container.reg.RegistrationType;

/**
 * Annotate a parameter or field what name it is in the service container.
 * 
 * @author Carl Tian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Documented
@SysTypeInject(type = RegistrationType.Service, handler = FieldNameAsDefaultHandler.class)
public @interface Service {
	/**
	 * The name string in the service container, default is the field name
	 */
	String value() default "";
}
