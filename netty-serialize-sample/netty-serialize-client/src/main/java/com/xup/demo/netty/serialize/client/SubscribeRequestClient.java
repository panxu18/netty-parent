package com.xup.demo.netty.serialize.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xup.demo.netty.serialize.model.SubscribeRequest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class SubscribeRequestClient {

	private static Logger logger = LogManager.getLogger(SubscribeRequestClient.class);

	Bootstrap b = new Bootstrap();
	EventLoopGroup workerGroup = new NioEventLoopGroup();

	public SubscribeRequestClient() {
		init();
	}

	private void init() {
		logger.info("init...");
		try {
			b = new Bootstrap();
			workerGroup = new NioEventLoopGroup();

			b.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass()
							.getClassLoader())));
					ch.pipeline().addLast(new ObjectEncoder());
					ch.pipeline().addLast(new SubscribeRequestHandler());
				}
			});
			ChannelFuture f = b.connect("127.0.0.1", 7000).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			workerGroup.shutdownGracefully();
		}


	}

	private class SubscribeRequestHandler extends ChannelHandlerAdapter{

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			logger.info("Receive server response: [" + msg + "]");

		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			ctx.flush();
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			for(int i = 0; i < 10; i++){
				ctx.write(subscribeRequest(i));
			}
			ctx.flush();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			ctx.close();
		}

		private SubscribeRequest subscribeRequest(int i) {
			SubscribeRequest req = new SubscribeRequest();
			req.setSubReqID(i);
			req.setAddress("武汉市工程大学流芳校区");
			req.setPhoneNumber("13400000000");
			req.setProductName("Netty 手册");
			req.setUserName("xupan");
			return req;
		}

	}

	public static void main(String[] args) {
		SubscribeRequestClient subReqClient = new SubscribeRequestClient();
	}
}
