package cn.shiyanjun.ddc.network.common;

import com.alibaba.fastjson.JSONObject;

import io.netty.channel.Channel;

public class LocalMessage {

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
		return rpcMessage.toString();
	}
	
}
