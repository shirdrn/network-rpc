package cn.shiyanjun.ddc.network;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Throwables;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.network.api.MessageListener;
import cn.shiyanjun.ddc.network.common.NettyRpcEndpoint;
import cn.shiyanjun.ddc.network.common.RpcMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty RPC endpoint implementation for <code>server</code> side. User needs 
 * a {@link MessageListener} implementation to handle {@link RpcMessage}
 * transporting between server and client.
 * 
 * @author yanjun
 */
public class NettyRpcServer extends NettyRpcEndpoint {
	
	private static final Log LOG = LogFactory.getLog(NettyRpcServer.class);
	private final EventLoopGroup bossGroup;
	private final ServerBootstrap b = new ServerBootstrap();
	
	public NettyRpcServer(Context context) {
		super(context);
		bossGroup = super.newEventLoopGroup(1);
	}

	@Override
	public void start() {
		try {
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class) 
				.childHandler(super.newChannelInitializer())
				.option(ChannelOption.SO_BACKLOG, 128) 
				.childOption(ChannelOption.SO_KEEPALIVE, true);

			// Bind and start to accept incoming connections.
			bindOrConnectChannelFuture = b.bind(super.getSocketAddress().getPort()).sync(); 
			LOG.info("Netty server started!");
		} catch (Exception e) {
			LOG.error("Fail to start Netty RPC server: ", e);
			Throwables.propagate(e);
		}
	}
	
	@Override
	public void stop() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();		
	}
	
}
