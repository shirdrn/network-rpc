package cn.shiyanjun.platform.network.common;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import cn.shiyanjun.platform.network.api.MessageListener;

public abstract class RunnableMessageListener<T> implements MessageListener<T>, Runnable {

	private static final Log LOG = LogFactory.getLog(RunnableMessageListener.class);
	private final BlockingQueue<T> q = Queues.newLinkedBlockingQueue();
	private final Set<Integer> messageTypes = Sets.newHashSet();;
	
	public RunnableMessageListener(Integer... messageTypes) {
		for(int messageType : messageTypes) {
			this.messageTypes.add(messageType);
		}
	}
	
	public void addMessage(T message) {
		q.add(message);		
	}
	
	public Set<Integer> getMessageTypes() {
		return messageTypes;
	}
	
	@Override
	public void run() {
		while(true) {
			T message = null;
			try {
				message = q.take();
				handle(message);
			} catch (Exception e) {
				if(message != null) {
					LOG.warn("Fail to process message: " + message, e);
				}
			}
		}
	}
	
	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
