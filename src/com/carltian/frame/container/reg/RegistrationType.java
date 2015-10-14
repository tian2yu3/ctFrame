package com.carltian.frame.container.reg;

public enum RegistrationType {
	Resource, Action, Service, Task, Extension;

	public static RegistrationType get(String name) {
		try {
			return valueOf(name);
		} catch (Exception e) {
			return null;
		}
	}
}
