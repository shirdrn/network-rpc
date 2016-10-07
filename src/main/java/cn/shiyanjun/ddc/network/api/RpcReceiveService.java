package cn.shiyanjun.ddc.network.api;

public interface RpcReceiveService<C, M> {

	void receive(C channel, M message);
	void receive(C channel, Throwable cause);
}
