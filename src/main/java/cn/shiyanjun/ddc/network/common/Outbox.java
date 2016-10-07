package cn.shiyanjun.ddc.network.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.network.api.MessageDispatcher;
import cn.shiyanjun.ddc.network.constants.RpcConstants;

public class Outbox extends MessageBox<OutboxMessage> {

	private static final Log LOG = LogFactory.getLog(Outbox.class);
	private final int askRetryTimes;
	private final int askTimeout;
	
	public Outbox(Context context, MessageDispatcher dispatcher) {
		super(context, dispatcher);
		name = "OUTBOX";
		LOG.info("RPC configurations:");
		String key = RpcConstants.RPC_ASK_RETRY_TIMES;
		this.askRetryTimes = context.getInt(key, 0);
		LOG.info(key + "\t=\t" + askRetryTimes);
		
		key = RpcConstants.RPC_ASK_TIMEOUT;
		this.askTimeout = context.getInt(key, 30000);
		LOG.info(key + "\t=\t" + askTimeout);
	}
	
	@Override
	public void start() {
		super.start();
		super.getExecutorService().execute(this);
	}
	
	@Override
	public void run() {
		while(true) {
			OutboxMessage message = null;
			try {
				message = messageBox.take();
				if(message != null) {
					message.getChannel().writeAndFlush(message.getRpcMessage());
					LOG.debug("Rpc message sent: rpcMessage=" + message.getRpcMessage());
//						int timeout = askTimeout;
//						if(message.getTimeoutMillis() > 0) {
//							timeout = message.getTimeoutMillis();
//						}
//						
//						ChannelFuture future = null;
//						int retryTimes = askRetryTimes + 1;
//						while(retryTimes > 0) {
//							try {
//								future = message.getChannel().writeAndFlush(message);
//								future.get(timeout, TimeUnit.MILLISECONDS);
//								// notify message sent
//								AckMessage ack = new AckMessage(message);
//								ack.setMessageStatus(MessageStatus.SUCCESS);
//								dispatcher.dispatch(ack);
//								break;
//							} catch (TimeoutException e) {
//								retryTimes--;
//								continue;
//							}
//						}
				}
			} catch (Exception e) {
				LOG.warn("Fail to send message: " + message, e);
				if(message != null) {
//						SentAckMessage ack = new SentAckMessage(message);
//						ack.setMessageStatus(MessageStatus.FAILURE);
//						dispatcher.dispatch(ack);
				}
			}
		}
	}
		
}
