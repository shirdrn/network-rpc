package cn.shiyanjun.platform.network.common;

import io.netty.channel.Channel;

public class InboxMessage extends PeerMessage {

	private Channel channel;
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}	
}
