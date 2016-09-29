package cn.shiyanjun.ddc.network.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.api.LifecycleAware;
import cn.shiyanjun.ddc.api.network.RpcAskService;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public abstract class RpcMessageHandler extends ChannelInboundHandlerAdapter implements LifecycleAware, RpcAskService<LocalMessage> {

	private static final Log LOG = LogFactory.getLog(RpcMessageHandler.class);
	protected final Context context;
	
	protected final Inbox inbox;
	protected final Outbox outbox;
	
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
	public void ask(LocalMessage request) {
		askWithRetry(request, 0);
	}

	@Override
	public void askWithRetry(LocalMessage request, int timeoutMillis) {
		sendToRemotePeer(request, true, timeoutMillis);
	}
	
	@Override
	public void send(LocalMessage request) {
		sendWithRetry(request, 0);		
	}

	@Override
	public void sendWithRetry(LocalMessage request, int timeoutMillis) {
		sendToRemotePeer(request, false, timeoutMillis);		
	}
	
	@Override
	public void reply(LocalMessage request) {
		send(request);		
	}

	@Override
	public void replyWithRetry(LocalMessage request, int timeoutMillis) {
		sendWithRetry(request, timeoutMillis);		
	}
	
	protected abstract void sendToRemotePeer(LocalMessage request, boolean needRelpy, int timeoutMillis);
	
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
		LOG.info("Endpoint connected: channel=" + ctx.channel());
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.channel().close();
	}
	
}
