package com.carltian.frame.remote;

import java.util.Set;

import com.carltian.frame.remote.dto.ResultDto;

public interface RemoteManager {

	public abstract void register(ActionConfig config);

	public abstract void pushEvent(String remoteId, EventMessage msg);

	public abstract void pushEvent(Set<String> remoteSet, EventMessage msg);

	public abstract <T> ResultDto<T> remoteCall(String remoteId, EventMessage msg);
}
