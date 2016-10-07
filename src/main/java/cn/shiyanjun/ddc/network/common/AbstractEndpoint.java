package cn.shiyanjun.ddc.network.common;

import java.net.InetSocketAddress;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.api.common.AbstractComponent;
import cn.shiyanjun.ddc.network.api.RpcEndpoint;
import cn.shiyanjun.ddc.network.constants.RpcConfigKeys;

public abstract class AbstractEndpoint<M> extends AbstractComponent implements RpcEndpoint {

	private final InetSocketAddress socketAddress;
	
	public AbstractEndpoint(Context context) {
		super(context);
		final String host = context.get(RpcConfigKeys.NETWORK_RPC_HOST);
		final int port = context.getInt(RpcConfigKeys.NETWORK_RPC_PORT, 8080);
		this.socketAddress = new InetSocketAddress(host, port);
	}

	@Override
	public InetSocketAddress getSocketAddress() {
		return socketAddress;
	}

}
