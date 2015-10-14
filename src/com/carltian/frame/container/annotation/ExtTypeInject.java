package com.carltian.frame.container.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.carltian.frame.container.InjectAnnotationHandler;

/**
 * Annotate an annotation as an extension type inject annotation
 * 
 * @author Carl Tian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@Documented
public @interface ExtTypeInject {
	/**
	 * The name of extension type, default is the annotation class name
	 */
	String name() default "";

	/**
	 * The name of inject annotation's key field, default is "value"
	 */
	String keyField() default "value";

	/**
	 * The class of inject annotation's handler, default is {@link InjectAnnotationHandler}
	 */
	Class<? extends InjectAnnotationHandler> handler() default InjectAnnotationHandler.class;
}
