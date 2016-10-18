package cn.shiyanjun.platform.network.api;

/**
 * Protocol for sending messages with type <code>REQ</code>.
 *
 * @param <REQ>
 * @author yanjun
 */
public interface RpcAskService<M> {

	void ask(M request);

	void askWithRetry(M request, int timeoutMillis);
    
    void send(M request);

    void sendWithRetry(M request, int timeoutMillis);
    
    void reply(M request);

    void replyWithRetry(M request, int timeoutMillis);
}
