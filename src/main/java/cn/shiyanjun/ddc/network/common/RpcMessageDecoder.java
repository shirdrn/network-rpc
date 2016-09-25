package cn.shiyanjun.ddc.network.common;

import java.nio.charset.Charset;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RpcMessageDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if(in.readableBytes() > 0) {
			final RpcMessage msg = new RpcMessage();
			if(in.readableBytes() >= 8) {
				msg.setId(in.readLong());
			}
			if(in.readableBytes() >= 4) {
				msg.setType(in.readInt());
			}
			if(in.readableBytes() >= 8) {
				msg.setTimestamp(in.readLong());
			}
			if(in.readableBytes() > 0) {
				final byte[] dst = new byte[in.readableBytes()];
				in.readBytes(dst);
				msg.setBody(new String(dst, Charset.forName("UTF-8")));
			}
			out.add(msg);
		}
	}

}
