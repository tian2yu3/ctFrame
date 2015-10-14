package com.carltian.frame.container.reg;

public class InjectInfo implements Cloneable {
	private String name = null;
	private ArgInfo defaultValue = null;

	public InjectInfo() {
	}

	public InjectInfo(String name) {
		this(name, new ArgInfo());
	}

	public InjectInfo(String name, ArgInfo defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public InjectInfo clone() {
		InjectInfo obj;
		try {
			obj = (InjectInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		if (defaultValue != null) {
			obj.defaultValue = defaultValue.clone();
		}
		return obj;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArgInfo getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(ArgInfo defaultValue) {
		this.defaultValue = defaultValue;
	}
}
