package cn.shiyanjun.ddc.network.common;

import cn.shiyanjun.ddc.api.common.AbstractMessage;

public class RpcMessage extends AbstractMessage<String> {

	private static final long serialVersionUID = -503554980049643194L;

	public RpcMessage() {
		super();
	}
	
	public RpcMessage(RpcMessage message) {
		super();
		this.id = message.id;
		this.type = message.type;
		this.timestamp = message.timestamp;
		this.body = message.body;
	}
	
	public RpcMessage(Long id) {
		super(id);
	}

	public RpcMessage(Long id, int type) {
		super(id, type);
	}
	
	@Override
	public String toString() {
		return "id=" + id + ", type=" + type + ", body=" + body;
	}
}
