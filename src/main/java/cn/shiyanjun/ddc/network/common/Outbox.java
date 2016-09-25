package cn.shiyanjun.ddc.network.common;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.network.constants.MessageStatus;
import cn.shiyanjun.ddc.network.constants.RpcConstants;
import io.netty.channel.ChannelFuture;

public class Outbox extends MessageBox<OutboxMessage> {

	private static final Log LOG = LogFactory.getLog(Outbox.class);
	private final int askRetryTimes;
	private final int askTimeout;
	
	public Outbox(Context context, MessageDispatcher dispatcher) {
		super(context, dispatcher);
		LOG.info("RPC configurations:");
		String key = RpcConstants.RPC_ASK_RETRY_TIMES;
		this.askRetryTimes = context.getInt(key, 0);
		LOG.info(key + "\t=\t" + askRetryTimes);
		
		key = RpcConstants.RPC_ASK_TIMEOUT;
		this.askTimeout = context.getInt(key, 30000);
		LOG.info(key + "\t=\t" + askTimeout);
		
		final Thread sender = new Sender();
		sender.start();
	}
	
	private class Sender extends Thread {
		
		@Override
		public void run() {
			while(true) {
				OutboxMessage message = null;
				try {
					message = messageBox.take();
					if(message != null) {
						int timeout = askTimeout;
						if(message.getTimeoutMillis() > 0) {
							timeout = message.getTimeoutMillis();
						}
						
						ChannelFuture future = null;
						int retryTimes = askRetryTimes + 1;
						while(retryTimes > 0) {
							try {
								future = message.getChannel().writeAndFlush(message);
								future.get(timeout, TimeUnit.MILLISECONDS);
								// notify message sent
								AckMessage ack = new AckMessage(message);
								ack.setMessageStatus(MessageStatus.SUCCESS);
								dispatcher.dispatch(ack);
							} catch (TimeoutException e) {
								retryTimes--;
								continue;
							}
						}
					}
				} catch (Exception e) {
					LOG.info("Fail to send message: " + message, e);
					if(message != null) {
						AckMessage ack = new AckMessage(message);
						ack.setMessageStatus(MessageStatus.FAILURE);
						dispatcher.dispatch(ack);
					}
				}
			}
		}
	}
}
