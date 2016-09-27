package cn.shiyanjun.ddc.network.common;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;

import cn.shiyanjun.ddc.api.Context;
import cn.shiyanjun.ddc.api.common.AbstractEndpoint;
import cn.shiyanjun.ddc.api.utils.ReflectionUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public abstract class NettyRpcEndpoint extends AbstractEndpoint<RpcMessage> {

	private static final Log LOG = LogFactory.getLog(NettyRpcEndpoint.class);
	private final List<ChannelHandler> channelHandlers = Lists.newArrayList();
	protected final EventLoopGroup workerGroup;
	
	public NettyRpcEndpoint(Context context) {
		super(context);
		this.workerGroup = newEventLoopGroup(1);
	}
	
	protected EventLoopGroup newEventLoopGroup(int nThreads) {
		return new NioEventLoopGroup(nThreads);
	}
	
	protected ChannelHandler newChannelInitializer() {
		return new ChannelInitializer<Channel>() { 
			@Override
			public void initChannel(Channel ch) throws Exception {
				for(final ChannelHandler handler : channelHandlers) {
					ch.pipeline().addLast(handler);
				}
			}
		};
	}
	
	public void addChannelHandlers(ChannelHandler... handlers) {
		for(ChannelHandler h : handlers) {
			channelHandlers.add(h);
			LOG.info("Channel handler added: " + h.getClass().getName());
		}
	}
	
	@Override
	public void stop() {
		try {
			workerGroup.shutdownGracefully().sync();
		} catch (InterruptedException e) {
			LOG.warn("Catch exception when worker group being shutdown:", e);
		}
		
	}
	
	/**
	 * Create a client/server side Netty RPC endpoint.
	 * @param endpointClass
	 * @param context
	 * @param rpcMessageHandler
	 * @return
	 */
	public static NettyRpcEndpoint newEndpoint(Class<? extends NettyRpcEndpoint> endpointClass, Context context, RpcMessageHandler rpcMessageHandler) {
		final NettyRpcEndpoint endpoint = ReflectionUtils.newInstance(endpointClass, NettyRpcEndpoint.class, context);
		// configure Netty endpoint
		endpoint.addChannelHandlers(
				new RpcMessageDecoder(), 
				rpcMessageHandler, 
				new RpcMessageEncoder());
		return endpoint;
	}

}
