package com.carltian.frame.container.reg;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodInfo implements Comparable<MethodInfo>, Cloneable {
	private Method method = null;
	private int priority = 0;
	private final List<InjectInfo> params = new ArrayList<InjectInfo>();

	public MethodInfo() {

	}

	public MethodInfo(Method method, int priority) {
		this.method = method;
		this.priority = priority;
	}

	@Override
	public int compareTo(MethodInfo target) {
		return priority - target.priority;
	}

	@Override
	public MethodInfo clone() {
		MethodInfo obj = new MethodInfo(method, priority);
		// 深拷贝params
		for (InjectInfo param : params) {
			if (param == null) {
				obj.params.add(null);
			} else {
				obj.params.add(param.clone());
			}
		}
		return obj;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public List<InjectInfo> getParams() {
		return params;
	}

}
