package cn.shiyanjun.ddc.network.api;

import java.net.InetSocketAddress;

import cn.shiyanjun.ddc.api.LifecycleAware;

/**
 * RPC endpoint for Server or Client.
 * 
 * @author yanjun
 */
public interface RpcEndpoint extends LifecycleAware {

	InetSocketAddress getSocketAddress();
}
