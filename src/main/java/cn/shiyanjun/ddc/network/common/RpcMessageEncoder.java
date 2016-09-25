package cn.shiyanjun.ddc.network.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
		out.writeLong(msg.getId().longValue())
			.writeInt(msg.getType())
			.writeLong(msg.getTimestamp())
			.writeBytes(msg.getBody().getBytes());
	}

}
