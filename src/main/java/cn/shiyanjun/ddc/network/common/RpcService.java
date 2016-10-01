package cn.shiyanjun.ddc.network.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Throwables;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.api.LifecycleAware;
import cn.shiyanjun.ddc.api.common.AbstractComponent;
import cn.shiyanjun.ddc.api.network.RpcAskService;
import cn.shiyanjun.ddc.api.network.RpcReceiveService;
import io.netty.channel.Channel;

public abstract class RpcService extends AbstractComponent implements RpcAskService<PeerMessage>, RpcReceiveService<Channel, RpcMessage>, LifecycleAware {

	private static final Log LOG = LogFactory.getLog(RpcService.class);
	protected final Inbox inbox;
	protected final Outbox outbox;
	
	public RpcService(Context context, MessageDispatcher dispatcher) {
		super(context);
		inbox = new Inbox(context, dispatcher);
		outbox = new Outbox(context, dispatcher);
	}
	
	@Override
	public void ask(PeerMessage request) {
		askWithRetry(request, 0);
	}

	@Override
	public void askWithRetry(PeerMessage request, int timeoutMillis) {
		sendToRemotePeer(request, true, timeoutMillis);
	}
	
	@Override
	public void send(PeerMessage request) {
		sendWithRetry(request, 0);		
	}

	@Override
	public void sendWithRetry(PeerMessage request, int timeoutMillis) {
		sendToRemotePeer(request, false, timeoutMillis);		
	}
	
	@Override
	public void reply(PeerMessage request) {
		send(request);		
	}

	@Override
	public void replyWithRetry(PeerMessage request, int timeoutMillis) {
		sendWithRetry(request, timeoutMillis);		
	}
	
	@Override
	public void start() {
		inbox.start();
		LOG.info("Inbox started.");
		
		outbox.start();
		LOG.info("Outbox started.");
	}
	
	@Override
	public void stop() {
		try {
			// check inbox
			while(!inbox.isEmpty()) {
				Thread.sleep(50);
			}
			inbox.stop();
			
			// check outbox
			while(!outbox.isEmpty()) {
				Thread.sleep(50);
			}
			outbox.stop();
		} catch (Exception e) {
			LOG.error("Fail to check completion of message in REQ/RSP queue.");
			Throwables.propagate(e);
		}
	}
	
	/**
	 * Send encapsulated peer message to remote peer. Actually deliver message to the network layer
	 * to process message.
	 * @param message
	 * @param needRelpy
	 * @param timeoutMillis
	 */
	protected abstract void sendToRemotePeer(PeerMessage message, boolean needRelpy, int timeoutMillis);
	
}
