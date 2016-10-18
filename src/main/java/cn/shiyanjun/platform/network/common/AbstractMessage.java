package cn.shiyanjun.platform.network.common;

import java.io.Serializable;

import cn.shiyanjun.platform.network.api.Message;

public abstract class AbstractMessage<T> implements Message<T>, Serializable {

	private static final long serialVersionUID = -8652704309921969573L;
	protected Long id;
	protected T body;
	protected int type;
	protected boolean needReply;
	protected long timestamp;
	
	public AbstractMessage() {
		super();
	}
	
	public AbstractMessage(Long id) {
		super();
		this.id = id;
	}

	public AbstractMessage(Long id, int type) {
		super();
		this.id = id;
		this.type = type;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
	
	@Override
	public void setBody(T body) {
		this.body = body;		
	}

	@Override
	public T getBody() {
		return body;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	@Override
	public int getType() {
		return type;
	}
	
	public boolean isNeedReply() {
		return needReply;
	}

	public void setNeedReply(boolean needReply) {
		this.needReply = needReply;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
