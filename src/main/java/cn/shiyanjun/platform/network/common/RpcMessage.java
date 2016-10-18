package cn.shiyanjun.platform.network.common;

public class RpcMessage extends AbstractMessage<String> {

	private static final long serialVersionUID = 1L;

	public RpcMessage() {
		super();
	}
	
	public RpcMessage(RpcMessage message) {
		super();
		this.id = message.id;
		this.type = message.type;
		this.body = message.body;
		this.timestamp = message.timestamp;
	}
	
	public RpcMessage(Long id, int type) {
		super(id, type);
	}
	
	@Override
	public String toString() {
		return new StringBuffer()
				.append("id=" + id)
				.append(", messageType=" + type)
				.append(", needReply=" + needReply)
				.append(", body=" + body)
				.append(", timestamp=" + timestamp)
				.toString();
	}
}
