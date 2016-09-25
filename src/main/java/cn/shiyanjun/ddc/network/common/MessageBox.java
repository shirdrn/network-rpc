package cn.shiyanjun.ddc.network.common;

import java.util.concurrent.BlockingQueue;

import com.google.common.collect.Queues;

import cn.shiyanjun.ddc.api.Context;

public class MessageBox<M> {

	protected final BlockingQueue<M> messageBox = Queues.newLinkedBlockingQueue();
	protected final Context context;
	protected final MessageDispatcher dispatcher;
	
	public MessageBox(Context context, MessageDispatcher dispatcher) {
		super();
		this.context = context;
		this.dispatcher = dispatcher;
	}
	public void collect(M message) {
		messageBox.add(message);
	}
	
	public boolean isEmpty() {
		return messageBox.isEmpty();
	}
	
	public int size() {
		return messageBox.size();
	}
}
