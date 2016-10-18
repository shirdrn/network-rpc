package cn.shiyanjun.platform.network.common;

import io.netty.channel.Channel;

public class OutboxMessage extends PeerMessage {

	private transient Channel channel;
	private transient int timeoutMillis;
	
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
