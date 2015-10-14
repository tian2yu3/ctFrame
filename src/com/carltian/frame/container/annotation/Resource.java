package com.carltian.frame.container.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.carltian.frame.container.ResourceAnnotationHandler;
import com.carltian.frame.container.ResourceName;
import com.carltian.frame.container.reg.RegistrationType;

/**
 * Annotate a parameter or field what name it is in the resource container.
 * 
 * @author Carl Tian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Documented
@SysTypeInject(type = RegistrationType.Resource, handler = ResourceAnnotationHandler.class)
public @interface Resource {
	/**
	 * The name in the resource container, default is calculated with element class.
	 */
	ResourceName value() default ResourceName.Undefined;
}
