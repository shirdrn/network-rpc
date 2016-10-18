package cn.shiyanjun.platform.network.api;

import java.net.InetSocketAddress;

import cn.shiyanjun.platform.api.LifecycleAware;

/**
 * RPC endpoint for Server or Client.
 * 
 * @author yanjun
 */
public interface RpcEndpoint extends LifecycleAware {

	InetSocketAddress getSocketAddress();
}
