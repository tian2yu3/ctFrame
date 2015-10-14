package com.carltian.frame.remote;

public class EventMessageFactory {
	public static EventMessage newMessage(String module, String function) {
		return new EventMessageImpl(module, function);
	}
}
