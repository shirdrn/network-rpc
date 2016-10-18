package cn.shiyanjun.platform.network.common;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;

import cn.shiyanjun.platform.api.Context;
import cn.shiyanjun.platform.api.utils.Pair;
import cn.shiyanjun.platform.api.utils.ReflectionUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public abstract class NettyRpcEndpoint extends AbstractEndpoint<RpcMessage> {

	private static final Log LOG = LogFactory.getLog(NettyRpcEndpoint.class);
	private final List<Pair<Class<? extends ChannelHandler>, Object[]>> registeredchannelHandlerClasses = Lists.newArrayList();
	protected EventLoopGroup workerGroup;
	protected ChannelFuture bindOrConnectChannelFuture;
	
	public NettyRpcEndpoint(Context context) {
		super(context);
	}
	
	@Override
	public void start() {
		workerGroup = newEventLoopGroup(1);		
	}
	
	@Override
	public void stop() {
		try {
			workerGroup.shutdownGracefully().sync();
		} catch (InterruptedException e) {
			LOG.warn("Catch exception when worker group being shutdown:", e);
		}
		
	}
	
	protected EventLoopGroup newEventLoopGroup(int nThreads) {
		return new NioEventLoopGroup(nThreads);
	}
	
	protected ChannelHandler newChannelInitializer() {
		return new ChannelInitializer<Channel>() { 
			
			@Override
			public void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new ObjectDecoder(Class::forName));
				for(Pair<Class<? extends ChannelHandler>, Object[]> clazz : registeredchannelHandlerClasses) {
					ChannelHandler handler = ReflectionUtils.newInstance(clazz.getKey(), ChannelHandler.class, clazz.getValue());
					ch.pipeline().addLast(handler);
					LOG.info("Channel handler pipelined: handler=" + handler);
				}
				ch.pipeline().addLast(new ObjectEncoder());
			}
		};
	}
	
	public void addChannelHandlerClass(Pair<Class<? extends ChannelHandler>, Object[]> clazzWithParameters) {
		registeredchannelHandlerClasses.add(clazzWithParameters);
		LOG.info("Channel handler added: clazz=" + clazzWithParameters.getKey().getName());
	}
	
	public void await() throws InterruptedException {
		bindOrConnectChannelFuture.channel().closeFuture().sync();
	}
	
	/**
	 * Create a client/server side Netty RPC endpoint.
	 * @param endpointClass
	 * @param context
	 * @param rpcMessageHandler
	 * @return
	 */
	public static NettyRpcEndpoint newEndpoint(
			Context context, 
			Class<? extends NettyRpcEndpoint> endpointClass, 
			List<Pair<Class<? extends ChannelHandler>, Object[]>> channelHandlerClasses) {
		final NettyRpcEndpoint endpoint = ReflectionUtils.newInstance(endpointClass, NettyRpcEndpoint.class, context);
		// configure Netty endpoint
		for(Pair<Class<? extends ChannelHandler>, Object[]> clazz : channelHandlerClasses) {
			endpoint.addChannelHandlerClass(clazz);
		}
		return endpoint;
	}

}
