package cn.shiyanjun.ddc.network.common;

import io.netty.channel.Channel;

public class OutboxMessage extends RpcMessage {

	private static final long serialVersionUID = 1L;
	private transient Channel channel;
	private transient int timeoutMillis;
	
	public OutboxMessage(Long id) {
		super(id);
	}
	
	public OutboxMessage(RpcMessage message) {
		super(message);
	}
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public int getTimeoutMillis() {
		return timeoutMillis;
	}

	public void setTimeoutMillis(int timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}
}