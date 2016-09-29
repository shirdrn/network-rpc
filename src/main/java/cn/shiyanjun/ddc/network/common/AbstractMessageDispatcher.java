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
	private final ConcurrentMap<Integer, RunnableMessageListener<LocalMessage>> typedListeners = Maps.newConcurrentMap();
	private final ConcurrentMap<RunnableMessageListener<LocalMessage>, Set<Integer>> listenerToTypeSet = Maps.newConcurrentMap();
	private ExecutorService executorService;
	private RpcMessageHandler rpcMessageHandler;
	
	public AbstractMessageDispatcher(Context context) {
		super(context);
	}

	@Override
	public void dispatch(LocalMessage message) {
		if(message != null) {
			final RunnableMessageListener<LocalMessage> listener = typedListeners.get(message.getRpcMessage().getType());
			if(listener != null) {
				listener.handle(message);
			} else {
				LOG.warn("Unknown message: " + message);
			}
		}
	}
	
	@Override
	public void register(RunnableMessageListener<LocalMessage> messageListener) {
		Set<Integer> messageTypes = messageListener.getMessageTypes();
		for(int messageType : messageTypes) {
			if(typedListeners.putIfAbsent(messageType, messageListener) == null) {
				LOG.info("Message listener registered: type=" + messageType + ", listener=" + messageListener.getClass().getName());
			} else {
				Throwables.propagate(new IllegalStateException("Message listener already registered: type=" + messageType));
			}
			Set<Integer> types = listenerToTypeSet.get(messageListener);
			if(types == null) {
				types = Sets.newHashSet();
				listenerToTypeSet.putIfAbsent(messageListener, types);
			}
			types.add(messageType);
		}
	}
	
	@Override
	public void ask(LocalMessage request) {
		rpcMessageHandler.ask(request);		
	}
	
	@Override
	public void askWithRetry(LocalMessage request, int timeoutMillis) {
		rpcMessageHandler.askWithRetry(request, timeoutMillis);		
	}
	
	@Override
	public void send(LocalMessage request) {
		rpcMessageHandler.send(request);		
	}
	
	@Override
	public void sendWithRetry(LocalMessage request, int timeoutMillis) {
		rpcMessageHandler.sendWithRetry(request, timeoutMillis);		
	}
	
	@Override
	public void reply(LocalMessage request) {
		rpcMessageHandler.reply(request);		
	}
	
	@Override
	public void replyWithRetry(LocalMessage request, int timeoutMillis) {
		rpcMessageHandler.replyWithRetry(request, timeoutMillis);		
	}
	
	@Override
	public RunnableMessageListener<LocalMessage> getMessageListener(int messageType) {
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
		for(final RunnableMessageListener<LocalMessage> listener : listenerToTypeSet.keySet()) {
			listener.start();
			executorService.execute(listener);
			LOG.info("Message listener started: listener=" + listener.getClass().getName() + ", types=" + listenerToTypeSet.get(listener));
		}
	}
	
	@Override
	public void stop() {
		executorService.shutdown();		
	}

}
