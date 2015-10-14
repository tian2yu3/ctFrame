package com.carltian.frame.remote.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.carltian.frame.remote.ContextItem;

/**
 * Annotate a parameter what key it is in the context.
 * 
 * @author Carl Tian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface Context {
	/**
	 * The key string in the context.
	 */
	ContextItem value();

}
