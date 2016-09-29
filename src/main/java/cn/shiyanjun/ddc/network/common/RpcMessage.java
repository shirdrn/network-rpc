package cn.shiyanjun.ddc.network.common;

import com.alibaba.fastjson.JSONObject;

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
	
	public JSONObject toJSONString() {
		JSONObject o = new JSONObject(true);
		o.put("id", id);
		o.put("type", type);
		o.put("body", JSONObject.parse(body));
		return o;
	}
	
	@Override
	public String toString() {
		return toJSONString().toString();
	}
}
