package cn.shiyanjun.ddc.network.common;

import io.netty.channel.Channel;

public class InboxMessage extends RpcMessage {

	private static final long serialVersionUID = 1L;
	private Channel channel;
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public InboxMessage(Long id) {
		super(id);
	}
	
	public InboxMessage(RpcMessage message) {
		this.id = message.getId();
		this.type = message.getType();
		this.timestamp = message.getTimestamp();
		this.body = message.getBody();
	}
	
}
