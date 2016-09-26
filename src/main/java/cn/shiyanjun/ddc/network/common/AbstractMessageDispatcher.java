package cn.shiyanjun.ddc.network.common;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.api.common.AbstractComponent;
import cn.shiyanjun.ddc.api.utils.NamedThreadFactory;

public abstract class AbstractMessageDispatcher extends AbstractComponent implements MessageDispatcher {

	private static final Log LOG = LogFactory.getLog(AbstractMessageDispatcher.class);
	private final ConcurrentMap<Integer, RunnableMessageListener<RpcMessage>> listeners = Maps.newConcurrentMap();
	private ExecutorService executorService;
	private RpcMessageHandler rpcMessageHandler;
	
	public AbstractMessageDispatcher(Context context) {
		super(context);
	}

	@Override
	public void dispatch(RpcMessage message) {
		if(message != null) {
			final RunnableMessageListener<RpcMessage> listener = listeners.get(message.getType());
			if(listener != null) {
				listener.handle(message);
			} else {
				LOG.warn("Unknown message: " + message);
			}
		}
	}
	
	@Override
	public void register(RunnableMessageListener<RpcMessage> messageListener) {
		int messageType = messageListener.getMessageType();
		if(listeners.putIfAbsent(messageType, messageListener) == null) {
			LOG.info("Message listener registered: type=" + messageType + ", listener=" + messageListener.getClass().getName());
		} else {
			Throwables.propagate(new IllegalStateException("Message listener already registered: type=" + messageType));
		}
	}
	
	@Override
	public RunnableMessageListener<RpcMessage> getMessageListener(int messageType) {
		return listeners.get(messageType);
	}
	
	@Override
	public void setRpcMessageHandler(RpcMessageHandler rpcMessageHandler) {
		this.rpcMessageHandler = rpcMessageHandler;
	}
	
	@Override
	public RpcMessageHandler getRpcMessageHandler() {
		return rpcMessageHandler;
	}
	
	@Override
	public void start() {
		Preconditions.checkArgument(rpcMessageHandler != null, "RPC message handler not set.");
		Preconditions.checkArgument(!listeners.isEmpty(), "No message listener registered.");
		executorService = Executors.newCachedThreadPool(new NamedThreadFactory("MESSAGE-LISTENER"));
		for(int type : listeners.keySet()) {
			final RunnableMessageListener<RpcMessage> listener = listeners.get(type);
			listener.start();
			executorService.execute(listener);
			LOG.info("Message listener started: type=" + type + ", listener=" + listener.getClass().getName());
		}
	}
	
	@Override
	public void stop() {
		executorService.shutdown();		
	}

}
