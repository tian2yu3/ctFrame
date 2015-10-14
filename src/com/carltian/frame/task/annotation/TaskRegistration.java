package com.carltian.frame.task.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a container task.
 * 
 * @version 1.0
 * @author Carl Tian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface TaskRegistration {
	/**
	 * The name string in the container. It will be empty if the task is anonymous.
	 */
	String name() default "";

	/**
	 * The millisecond before the task start.
	 */
	long delay() default 0;

	/**
	 * The period millisecond of the task.
	 */
	long period() default 0;
}
