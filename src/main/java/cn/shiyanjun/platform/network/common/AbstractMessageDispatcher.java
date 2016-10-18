package cn.shiyanjun.platform.network.common;

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

import cn.shiyanjun.platform.api.Context;
import cn.shiyanjun.platform.api.common.AbstractComponent;
import cn.shiyanjun.platform.api.utils.NamedThreadFactory;
import cn.shiyanjun.platform.network.api.MessageDispatcher;

public abstract class AbstractMessageDispatcher extends AbstractComponent implements MessageDispatcher {

	private static final Log LOG = LogFactory.getLog(AbstractMessageDispatcher.class);
	private final ConcurrentMap<Integer, RunnableMessageListener<PeerMessage>> typedListeners = Maps.newConcurrentMap();
	private final ConcurrentMap<RunnableMessageListener<PeerMessage>, Set<Integer>> listenerToTypeSet = Maps.newConcurrentMap();
	private ExecutorService executorService;
	
	public AbstractMessageDispatcher(Context context) {
		super(context);
	}

	@Override
	public void dispatch(PeerMessage message) {
		if(message != null) {
			final RunnableMessageListener<PeerMessage> listener = typedListeners.get(message.getRpcMessage().getType());
			if(listener != null) {
				listener.handle(message);
			} else {
				LOG.warn("Unknown message: " + message);
			}
		}
	}
	
	@Override
	public void register(RunnableMessageListener<PeerMessage> messageListener) {
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
	
	public RunnableMessageListener<PeerMessage> getMessageListener(int messageType) {
		return typedListeners.get(messageType);
	}
	
	@Override
	public void start() {
		Preconditions.checkArgument(!typedListeners.isEmpty(), "No message listener registered.");
		executorService = Executors.newCachedThreadPool(new NamedThreadFactory("MESSAGE-LISTENER"));
		for(final RunnableMessageListener<PeerMessage> listener : listenerToTypeSet.keySet()) {
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
