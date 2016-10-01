package cn.shiyanjun.ddc.network.common;

import cn.shiyanjun.ddc.api.LifecycleAware;

public interface MessageDispatcher extends LifecycleAware {

	void dispatch(PeerMessage message);
	void register(RunnableMessageListener<PeerMessage> messageListener);
	
	RunnableMessageListener<PeerMessage> getMessageListener(int messageType);
	
	void setRpcService(RpcService rpcService);
	RpcService getRpcService();
	
}
