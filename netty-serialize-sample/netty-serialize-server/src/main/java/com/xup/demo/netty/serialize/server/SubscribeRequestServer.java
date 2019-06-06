package com.xup.demo.netty.serialize.server;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.xup.demo.netty.serialize.model.SubscribeRequest;
import com.xup.demo.netty.serialize.model.SubscribeResponse;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


@Component
public class SubscribeRequestServer {
	private static Logger logger = LogManager.getLogger(SubscribeRequestServer.class);

	private final EventLoopGroup bossGroup = new NioEventLoopGroup();
	private final EventLoopGroup workerGroup = new NioEventLoopGroup();
	
	private Channel channel;

	/**
	 * start service
	 */
	public ChannelFuture run(InetSocketAddress address){
		ChannelFuture f = null;
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					.addLast(new ObjectDecoder(1024 * 1024,
							ClassResolvers.weakCachingConcurrentResolver(this.getClass()
									.getClassLoader())));
					ch.pipeline().addLast(new ObjectEncoder());
					ch.pipeline().addLast(new SubscibeRequestHandler());

				}

			})
			.option(ChannelOption.SO_BACKLOG, 128);
			
			f = b.bind(address).syncUninterruptibly();
			channel = f.channel();

		} catch (Exception e){
			 logger.error("Netty start error:", e);
		} finally {
			 if (f != null && f.isSuccess()) {
	                logger.info("Netty server listening " + address.getHostName() + " on port " + address.getPort() + " and ready for connections...");
	            } else {
	            	destroy();
	                logger.error("Netty server start up Error!");
	            }
		}
		return f;
	}
	
	public void destroy(){
		logger.info("Shutdown Netty Server...");
		if(channel != null) { channel.close();}
		workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        logger.info("Shutdown Netty Server Success!");
	}

	private class SubscibeRequestHandler extends ChannelHandlerAdapter{

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			SubscribeRequest req = (SubscribeRequest) msg;
			if("xupan".equalsIgnoreCase(req.getUserName())){
				logger.info("Service accept client subscribe req : [" + 
						req.toString() + "]");
				ctx.writeAndFlush(response(req.getSubReqID()));

			}

		}

		private SubscribeResponse response(int subReqID) {
			SubscribeResponse resp = new SubscribeResponse();
			resp.setSubReqID(subReqID);
			resp.setRespCode(0);
			resp.setDesc("Order subscribe successly!");
			
			return resp;
		}


	}

}
