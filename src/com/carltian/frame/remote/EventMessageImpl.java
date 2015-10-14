package com.carltian.frame.remote;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.carltian.frame.remote.dto.ResultDto;
import com.carltian.frame.util.JsonConverter;

public class EventMessageImpl implements EventMessage {

	private String id;

	private MessageType type = MessageType.Event;

	private String referenceId = "";

	private String module = "";

	private String fn = "";

	private HashMap<String, String> data = new HashMap<String, String>();

	@JSONField(serialize = false)
	private final HashMap<String, Object> dataMap = new HashMap<String, Object>();

	public EventMessageImpl() {
		id = UUID.randomUUID().toString();
	}

	public EventMessageImpl(String module, String fn) {
		id = UUID.randomUUID().toString();
		this.module = module;
		this.fn = fn;
	}

	public EventMessageImpl buildReplyMessage() {
		EventMessageImpl replyMsg = new EventMessageImpl();
		replyMsg.setReferenceId(id);
		replyMsg.setModule(module);
		replyMsg.setFn(fn);
		return replyMsg;
	}

	public EventMessageImpl buildResultMessage(ResultDto<?> result) {
		EventMessageImpl resultMsg = buildReplyMessage();
		resultMsg.setType(MessageType.Result);
		resultMsg.addData("", result);
		return resultMsg;
	}

	@Override
	public <T> T fetchData(Class<T> clazz) {
		return fetchData("", clazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T fetchData(String key, Class<T> clazz) {
		if (!dataMap.containsKey(key) && data.containsKey(key)) {
			dataMap.put(key, JsonConverter.decode(data.get(key), clazz, true));
		}
		return (T) dataMap.get(key);
	}

	@Override
	public <T> T fetchData(TypeReference<T> reference) {
		return fetchData("", reference);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T fetchData(String key, TypeReference<T> reference) {
		if (!dataMap.containsKey(key) && data.containsKey(key)) {
			dataMap.put(key, JsonConverter.decode(data.get(key), reference, true));
		}
		return (T) dataMap.get(key);
	}

	@Override
	public <T> T fetchData(Type type) {
		return fetchData("", type);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T fetchData(String key, Type type) {
		if (!dataMap.containsKey(key) && data.containsKey(key)) {
			dataMap.put(key, JsonConverter.decode(data.get(key), type, true));
		}
		return (T) dataMap.get(key);
	}

	@Override
	public EventMessage addData(Object value) {
		return addData("", value);
	}

	@Override
	public EventMessage addData(String key, Object value) {
		dataMap.put(key, value);
		return this;
	}

	@Override
	public EventMessage removeData() {
		return removeData("");
	}

	@Override
	public EventMessage removeData(String key) {
		dataMap.remove(key);
		data.remove(key);
		return this;
	}

	public HashMap<String, String> getData() {
		for (String key : dataMap.keySet()) {
			data.put(key, JsonConverter.encode(dataMap.get(key), true));
		}
		return data;
	}

	public String toJsonStr() {
		return JsonConverter.encode(this, false);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public void setData(HashMap<String, String> data) {
		this.data = data;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getFn() {
		return fn;
	}

	public void setFn(String fn) {
		this.fn = fn;
	}

}
