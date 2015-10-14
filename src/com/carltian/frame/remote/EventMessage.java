package com.carltian.frame.remote;

import java.lang.reflect.Type;

import com.alibaba.fastjson.TypeReference;

public interface EventMessage {
	public abstract <T> T fetchData(Class<T> clazz);

	public abstract <T> T fetchData(String key, Class<T> clazz);

	public abstract <T> T fetchData(TypeReference<T> reference);

	public abstract <T> T fetchData(String key, TypeReference<T> reference);

	public abstract <T> T fetchData(Type type);

	public abstract <T> T fetchData(String key, Type type);

	public abstract EventMessage addData(Object value);

	public abstract EventMessage addData(String key, Object value);

	public abstract EventMessage removeData();

	public abstract EventMessage removeData(String key);
}
