package cn.shiyanjun.ddc.network.common;

import com.google.common.base.Preconditions;

import io.netty.channel.Channel;

public class PeerMessage {

	private String fromEndpointId;
	private String toEndpointId;
	private volatile Channel channel;
	private RpcMessage rpcMessage;
	
	public String getFromEndpointId() {
		return fromEndpointId;
	}
	public void setFromEndpointId(String fromEndpointId) {
		this.fromEndpointId = fromEndpointId;
	}
	public String getToEndpointId() {
		return toEndpointId;
	}
	public void setToEndpointId(String toEndpointId) {
		this.toEndpointId = toEndpointId;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public RpcMessage getRpcMessage() {
		return rpcMessage;
	}
	public void setRpcMessage(RpcMessage rpcMessage) {
		this.rpcMessage = rpcMessage;
	}
	@Override
	public String toString() {
		Preconditions.checkArgument(rpcMessage != null, "RPC message not set.");
		return rpcMessage.toString();
	}
	
}
