package cn.shiyanjun.ddc.network.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.api.LifecycleAware;
import cn.shiyanjun.ddc.api.network.RpcAskService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class RpcMessageHandler extends ChannelInboundHandlerAdapter implements LifecycleAware, RpcAskService<RpcMessage> {

	private static final Log LOG = LogFactory.getLog(RpcMessageHandler.class);
	protected final Context context;
	
	private final Inbox inbox;
	private final Outbox outbox;
	private Channel channel;
	
	public RpcMessageHandler(Context context, MessageDispatcher dispatcher) {
		super();
		this.context = context;
		inbox = new Inbox(context, dispatcher);
		outbox = new Outbox(context, dispatcher);
	}
	
	@Override
	public void start() {
		
	}
	
	@Override
	public void ask(RpcMessage request) {
		askWithRetry(request, 30000);
	}

	@Override
	public void askWithRetry(RpcMessage request, int timeoutMillis) {
		OutboxMessage message = new OutboxMessage(request);
		message.setChannel(channel);
		message.setTimeoutMillis(timeoutMillis);
		outbox.collect(message);		
	}
	
	@Override
	public void stop() {
		try {
			// check inbox
			while(!inbox.isEmpty()) {
				Thread.sleep(50);
			}
			// check outbox
			while(!outbox.isEmpty()) {
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			LOG.warn("Fail to check completion of message in REQ/RSP queue.");
		}
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		channel = ctx.channel();
		LOG.info("Endpoint connected: channel=" + ctx.channel());
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof RpcMessage) {
			RpcMessage m = (RpcMessage) msg;
			InboxMessage message = new InboxMessage(m);
			message.setChannel(ctx.channel());
			inbox.collect(message);
		}
	}

}
