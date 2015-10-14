package com.carltian.frame.remote;

public enum MessageType {
	Undefined(""), Call, Result, Event;

	private MessageType() {
		value = this.name();
	}

	private MessageType(String value) {
		this.value = value;
	}

	private String value;

	public static MessageType get(String value) {
		for (MessageType type : values()) {
			if (type.value().equals(value)) {
				return type;
			}
		}
		return Undefined;
	}

	public String value() {
		return value;
	}
}
