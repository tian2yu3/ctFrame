package com.carltian.frame.container.reg;

public class ArgInfo implements Cloneable {
	private RegistrationType type = null;
	private String extType = null;
	private Object value = null;

	public ArgInfo() {
	}

	public ArgInfo(Object value) {
		this(null, null, value);
	}

	public ArgInfo(RegistrationType type, Object value) {
		this(type, null, value);
	}

	public ArgInfo(String extType, Object value) {
		this(RegistrationType.Extension, extType, value);
	}

	public ArgInfo(RegistrationType type, String extType, Object value) {
		this.type = type;
		this.extType = extType;
		this.value = value;
	}

	@Override
	public ArgInfo clone() {
		try {
			return (ArgInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public RegistrationType getType() {
		return type;
	}

	public void setType(RegistrationType type) {
		this.type = type;
	}

	public String getExtType() {
		return extType;
	}

	public void setExtType(String extType) {
		this.extType = extType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
