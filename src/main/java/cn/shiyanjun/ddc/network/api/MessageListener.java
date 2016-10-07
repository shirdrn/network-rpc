package cn.shiyanjun.ddc.network.api;

import cn.shiyanjun.ddc.api.LifecycleAware;

/**
 * In-bound messages require to be handled by the 
 * actually business logic. For example, whether heartbeat messages
 * are reached, we should decide whether worker node is alive, if not
 * then the lost node should be removed from the memory of the Master 
 * node, and the lost node is not able to be assigned to execute tasks.
 * 
 * @author yanjun
 */
public interface MessageListener<M> extends LifecycleAware {

	void handle(M message);
}
