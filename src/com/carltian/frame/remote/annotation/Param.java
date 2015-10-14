package com.carltian.frame.remote.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a parameter what key it is in the message data map.
 * 
 * @author Carl Tian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface Param {
	/**
	 * The key string in the message data map.By default will get the whole data object.
	 */
	String value() default "";
}
