package cn.shiyanjun.ddc.network.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.api.LifecycleAware;
import cn.shiyanjun.ddc.api.utils.NamedThreadFactory;

public abstract class MessageBox<M> implements LifecycleAware, Runnable {

	protected String name = "BOX";
	protected final BlockingQueue<M> messageBox = Queues.newLinkedBlockingQueue();
	protected final Context context;
	protected final MessageDispatcher dispatcher;
	private ExecutorService executorService;
	
	public MessageBox(Context context, MessageDispatcher dispatcher) {
		super();
		this.context = context;
		this.dispatcher = dispatcher;
	}
	
	@Override
	public void start() {
		executorService = Executors.newCachedThreadPool(new NamedThreadFactory(name));
	}
	
	protected ExecutorService getExecutorService() {
		Preconditions.checkArgument(executorService != null, "Executor service not created");
		return executorService;
	}
	
	@Override
	public void stop() {
		executorService.shutdownNow();		
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
