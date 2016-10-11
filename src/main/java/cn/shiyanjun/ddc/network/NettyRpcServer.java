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
	private EventLoopGroup bossGroup;
	private ServerBootstrap b;
	
	public NettyRpcServer(Context context) {
		super(context);
	}

	@Override
	public void start() {
		super.start();
		try {
			b = new ServerBootstrap();
			bossGroup = super.newEventLoopGroup(1);
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class) 
				.childHandler(super.newChannelInitializer())
				.option(ChannelOption.SO_BACKLOG, 128) 
				.childOption(ChannelOption.SO_KEEPALIVE, true);

			// Bind & start to accept incoming connections
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
