package com.carltian.frame.remote.dto;

import java.io.Serializable;

public class MessageDto implements Serializable, Cloneable {
	private static final long serialVersionUID = -7726846067565106744L;

	private String message;

	protected MessageDto(String message) {
		this.message = message;
	}

	@Override
	public MessageDto clone() {
		MessageDto newObj;
		try {
			newObj = (MessageDto) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		return newObj;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
