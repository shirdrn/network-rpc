package cn.shiyanjun.ddc.network.common;

import io.netty.channel.Channel;

public class InboxMessage extends LocalMessage {

	private Channel channel;
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
}
