package cn.shiyanjun.ddc.network.api;

import cn.shiyanjun.ddc.api.LifecycleAware;
import cn.shiyanjun.ddc.network.common.PeerMessage;
import cn.shiyanjun.ddc.network.common.RunnableMessageListener;

public interface MessageDispatcher extends LifecycleAware {

	void dispatch(PeerMessage message);
	void register(RunnableMessageListener<PeerMessage> messageListener);
	
	RunnableMessageListener<PeerMessage> getMessageListener(int messageType);
	
}
