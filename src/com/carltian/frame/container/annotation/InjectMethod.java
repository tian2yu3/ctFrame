package com.carltian.frame.container.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate the method need to inject.
 * 
 * @author Carl Tian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
@Documented
public @interface InjectMethod {

	/**
	 * An invoking priority of all inject methods, default is 0.
	 */
	int priority() default 0;
}
