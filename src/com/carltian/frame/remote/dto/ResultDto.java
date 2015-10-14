package com.carltian.frame.remote.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ResultDto<T> implements Serializable {

	private static final long serialVersionUID = 627719165176284737L;

	private T data;
	private boolean successful = false;
	private List<MessageDto> messageList = new LinkedList<MessageDto>();

	public ResultDto() {

	}

	public ResultDto(boolean successful) {
		this.successful = successful;
	}

	public ResultDto(boolean successful, String message) {
		this.successful = successful;
		addMessage(message);
	}

	public ResultDto(boolean successful, T data) {
		this.successful = successful;
		this.data = data;
	}

	public ResultDto(ResultDto<?> dto) {
		this.successful = dto.successful;
		for (MessageDto message : dto.messageList) {
			this.messageList.add(message.clone());
		}
	}

	public ResultDto(ResultDto<?> dto, T data) {
		this.successful = dto.successful;
		this.data = data;
		for (MessageDto message : dto.messageList) {
			this.messageList.add(message.clone());
		}
	}

	public void addMessage(String message) {
		messageList.add(new MessageDto(message));
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public List<MessageDto> getMessageList() {
		return messageList;
	}

	public void setMessageList(List<MessageDto> messageList) {
		this.messageList = messageList;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
