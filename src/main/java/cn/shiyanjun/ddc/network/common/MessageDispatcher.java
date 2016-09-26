package cn.shiyanjun.ddc.network.common;

import cn.shiyanjun.ddc.api.LifecycleAware;

public interface MessageDispatcher extends LifecycleAware {

	void dispatch(RpcMessage message);
	void register(RunnableMessageListener<RpcMessage> messageListener);
	
	RunnableMessageListener<RpcMessage> getMessageListener(int messageType);
	
	void setRpcMessageHandler(RpcMessageHandler rpcMessageHandler);
	RpcMessageHandler getRpcMessageHandler();
}
