package cn.shiyanjun.ddc.network.common;

import cn.shiyanjun.ddc.api.LifecycleAware;
import cn.shiyanjun.ddc.api.network.RpcAskService;

public interface MessageDispatcher extends LifecycleAware, RpcAskService<LocalMessage> {

	void dispatch(LocalMessage message);
	void register(RunnableMessageListener<LocalMessage> messageListener);
	
	RunnableMessageListener<LocalMessage> getMessageListener(int messageType);
	
	void setRpcMessageHandler(RpcMessageHandler rpcMessageHandler);
	RpcMessageHandler getRpcMessageHandler();
	
}
