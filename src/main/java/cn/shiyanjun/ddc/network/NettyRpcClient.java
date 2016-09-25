package cn.shiyanjun.ddc.network;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.api.LifecycleAware;
import cn.shiyanjun.ddc.api.network.MessageListener;
import cn.shiyanjun.ddc.network.common.NettyRpcEndpoint;
import cn.shiyanjun.ddc.network.common.RpcMessage;
import cn.shiyanjun.ddc.network.common.RpcMessageDecoder;
import cn.shiyanjun.ddc.network.common.RpcMessageEncoder;
import cn.shiyanjun.ddc.network.common.RpcMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
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
	private final  Bootstrap b = new Bootstrap();
	
	private NettyRpcClient(Context context) {
		super(context);
	}

	@Override
	public void start() {
		try {
			b.group(workerGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.handler(super.newChannelInitializer());

			// Connect to the server
			ChannelFuture f = b.connect(
					super.getSocketAddress().getHostName(), 
					super.getSocketAddress().getPort()
				).sync(); 
			LOG.info("Netty client started!");
			
			// Wait until the connection is closed.
            f.channel().closeFuture().sync();
		} catch (Exception e) {
			LOG.warn("Fail to start Netty RPC client:", e);
		}
	}
	
	@Override
	public void stop() {
		workerGroup.shutdownGracefully();
	}
	
	/**
	 * Create a client-side Netty RPC endpoint.
	 *  
	 * @param context
	 * @return
	 */
	public static LifecycleAware newClient(Context context, RpcMessageHandler rpcMessageHandler) {
		final LifecycleAware client = new NettyRpcClient(context);
		// configure Netty endpoint
		NettyRpcEndpoint endpoint = (NettyRpcEndpoint) client;
		endpoint.addChannelHandlers(
				new RpcMessageDecoder(), 
				rpcMessageHandler, 
				new RpcMessageEncoder());
		return client;
	}

}
