package cn.shiyanjun.platform.network.api;

import cn.shiyanjun.platform.api.LifecycleAware;
import cn.shiyanjun.platform.network.common.PeerMessage;
import cn.shiyanjun.platform.network.common.RunnableMessageListener;

public interface MessageDispatcher extends LifecycleAware {

	void dispatch(PeerMessage message);
	void register(RunnableMessageListener<PeerMessage> messageListener);
	
	RunnableMessageListener<PeerMessage> getMessageListener(int messageType);
	
}
