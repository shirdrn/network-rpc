package cn.shiyanjun.ddc.network.common;

import cn.shiyanjun.ddc.network.constants.MessageStatus;

public class AckMessage extends RpcMessage {

	private static final long serialVersionUID = 1L;
	private MessageStatus messageStatus;
	
	public AckMessage(OutboxMessage message) {
		super(message);
	}

	public MessageStatus getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(MessageStatus messageStatus) {
		this.messageStatus = messageStatus;
	}

}
