package cn.shiyanjun.ddc.network;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Throwables;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.network.api.MessageListener;
import cn.shiyanjun.ddc.network.common.NettyRpcEndpoint;
import cn.shiyanjun.ddc.network.common.RpcMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Netty RPC endpoint implementation for <code>client</code> side. User needs 
 * a {@link MessageListener} implementation to handle {@link RpcMessage}
 * transporting between server and client.
 * 
 * @author yanjun
 */
public class NettyRpcClient extends NettyRpcEndpoint {

	private static final Log LOG = LogFactory.getLog(NettyRpcClient.class);
	private Bootstrap b;
	
	public NettyRpcClient(Context context) {
		super(context);
	}

	@Override
	public void start() {
		super.start();
		try {
			b = new Bootstrap();
			b.group(workerGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.handler(super.newChannelInitializer());

			// Connect to the server
			bindOrConnectChannelFuture = b.connect(
					super.getSocketAddress().getHostName(), 
					super.getSocketAddress().getPort()
				).sync(); 
			LOG.info("Netty client started!");
		} catch (Exception e) {
			LOG.warn("Fail to start Netty RPC client:", e);
			Throwables.propagate(e);
		}
	}
	
	@Override
	public void stop() {
		workerGroup.shutdownGracefully();
	}
	
}
