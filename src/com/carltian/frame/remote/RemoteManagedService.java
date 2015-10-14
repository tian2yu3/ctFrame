package com.carltian.frame.remote;

import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Singleton;

import com.carltian.frame.FrameContext;
import com.carltian.frame.container.ResourceName;
import com.carltian.frame.container.reg.RegistrationType;
import com.carltian.frame.util.FrameLogger;
import com.carltian.frame.util.JsonConverter;

@Singleton
@ManagedService
public class RemoteManagedService {

	private final RemoteManagerImpl remote = FrameContext.getContainer().lookup(RegistrationType.Resource,
			ResourceName.RemoteManager);

	@Message
	public void onMessage(String msg) {
		try {
			EventMessageImpl message = JsonConverter.decode(msg, EventMessageImpl.class, false);
			if (message == null) {
				// 没有消息
				return;
			}
			remote.onMessage(message);
		} catch (Exception e) {
			// atmosphere造成不能抛出异常
			FrameLogger.error("远程消息处理失败！", e);
		}
	}
}
