package cn.shiyanjun.ddc.network.common;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.network.api.MessageDispatcher;
import cn.shiyanjun.ddc.network.constants.RpcConstants;
import io.netty.channel.ChannelFuture;

public class Outbox extends MessageBox<OutboxMessage> {

	private static final Log LOG = LogFactory.getLog(Outbox.class);
	private final int askRetryTimes;
	private final int askTimeout;
	
	public Outbox(Context context, MessageDispatcher dispatcher) {
		super(context, dispatcher);
		name = "OUTBOX";
		LOG.info("RPC configurations:");
		String key = RpcConstants.RPC_ASK_RETRY_TIMES;
		askRetryTimes = context.getInt(key, 0);
		LOG.info("RPC config: " + key + "\t=\t" + askRetryTimes);
		
		key = RpcConstants.RPC_ASK_TIMEOUT;
		askTimeout = context.getInt(key, 30000);
		LOG.info("RPC config: " + key + "\t=\t" + askTimeout);
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
					int timeout = askTimeout;
					int retryTimes = askRetryTimes + 1;
					if(message.getTimeoutMillis() > 0) {
						timeout = message.getTimeoutMillis();
					}
					
					ChannelFuture future = null;
					if(askRetryTimes > 0) {
						while(retryTimes > 0) {
							try {
								future = message.getChannel().writeAndFlush(message.getRpcMessage());
								future.get(timeout, TimeUnit.MILLISECONDS);
								break;
							} catch (TimeoutException e) {
								retryTimes--;
								continue;
							}
						}
					} else {
						future = message.getChannel().writeAndFlush(message.getRpcMessage());
					}
					LOG.debug("Rpc message sent: rpcMessage=" + message.getRpcMessage());
				}
			} catch (Exception e) {
				LOG.warn("Fail to send message: " + message, e);
			}
		}
	}
		
}
