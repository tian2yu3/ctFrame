package com.carltian.frame.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a container service.
 * 
 * @version 1.0
 * @author Carl Tian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ServiceRegistration {
	/**
	 * The name string in the container.
	 */
	String name();

	/**
	 * If the service is a singleton.
	 */
	boolean singleton() default true;
}
