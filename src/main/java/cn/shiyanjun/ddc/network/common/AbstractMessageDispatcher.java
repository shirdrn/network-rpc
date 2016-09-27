package cn.shiyanjun.ddc.network.common;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.api.common.AbstractComponent;
import cn.shiyanjun.ddc.api.utils.NamedThreadFactory;

public abstract class AbstractMessageDispatcher extends AbstractComponent implements MessageDispatcher {

	private static final Log LOG = LogFactory.getLog(AbstractMessageDispatcher.class);
	private final ConcurrentMap<Integer, RunnableMessageListener<RpcMessage>> typedListeners = Maps.newConcurrentMap();
	private final Set<RunnableMessageListener<RpcMessage>> listenerSet = Sets.newCopyOnWriteArraySet();
	private ExecutorService executorService;
	private RpcMessageHandler rpcMessageHandler;
	
	public AbstractMessageDispatcher(Context context) {
		super(context);
	}

	@Override
	public void dispatch(RpcMessage message) {
		if(message != null) {
			final RunnableMessageListener<RpcMessage> listener = typedListeners.get(message.getType());
			if(listener != null) {
				listener.handle(message);
			} else {
				LOG.warn("Unknown message: " + message);
			}
		}
	}
	
	@Override
	public void register(RunnableMessageListener<RpcMessage> messageListener) {
		Set<Integer> messageTypes = messageListener.getMessageTypes();
		for(int messageType : messageTypes) {
			if(typedListeners.putIfAbsent(messageType, messageListener) == null) {
				LOG.info("Message listener registered: type=" + messageType + ", listener=" + messageListener.getClass().getName());
			} else {
				Throwables.propagate(new IllegalStateException("Message listener already registered: type=" + messageType));
			}
			if(!listenerSet.contains(messageListener)) {
				listenerSet.add(messageListener);
			}
		}
	}
	
	@Override
	public RunnableMessageListener<RpcMessage> getMessageListener(int messageType) {
		return typedListeners.get(messageType);
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
		Preconditions.checkArgument(!typedListeners.isEmpty(), "No message listener registered.");
		executorService = Executors.newCachedThreadPool(new NamedThreadFactory("MESSAGE-LISTENER"));
		for(final RunnableMessageListener<RpcMessage> listener : listenerSet) {
			listener.start();
			executorService.execute(listener);
			LOG.info("Message listener started: listener=" + listener.getClass().getName());
		}
	}
	
	@Override
	public void stop() {
		executorService.shutdown();		
	}

}
