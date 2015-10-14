package com.carltian.frame.remote.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a container module.
 * 
 * @author Carl Tian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ActionRegistration {
	/**
	 * The name string in the container.
	 */
	String name() default "";

	/**
	 * A module path name string, default is the type simple name.
	 */
	String module() default "";

	/**
	 * If the module is singleton.
	 */
	boolean singleton() default true;
}
