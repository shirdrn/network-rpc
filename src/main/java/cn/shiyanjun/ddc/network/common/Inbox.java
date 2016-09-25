package cn.shiyanjun.ddc.network.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.shiyanjun.ddc.api.Context;

public class Inbox extends MessageBox<InboxMessage> {

	private static final Log LOG = LogFactory.getLog(Inbox.class);
	
	public Inbox(Context context, MessageDispatcher dispatcher) {
		super(context, dispatcher);
		final Thread receiver = new Receiver();
		receiver.start();
	}
	
	private class Receiver extends Thread {
		
		@Override
		public void run() {
			while(true) {
				InboxMessage message = null;
				try {
					message = messageBox.take();
					if(message != null) {
						dispatcher.dispatch(message);
					}
				} catch (Exception e) {
					LOG.warn("Fail to dispatch message: " + message, e);
				}
			}
		}
	}
}
