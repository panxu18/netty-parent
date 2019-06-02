package com.xup.demo.netty.client;


import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

public class NettyClient {

	private static Logger logger = LogManager.getLogger(NettyClient.class);

	private static Bootstrap b;
	private static ChannelFuture f;
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

	private static void init () {
		try {
			logger.info("init...");
			b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel socketChannel) {
					// 解码编码
					socketChannel.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
					socketChannel.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));

					socketChannel.pipeline().addLast(new ClientHandler());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object startAndWrite(InetSocketAddress address, Object send) throws InterruptedException {
		init();
		f = b.connect(address).sync();
		// 传数据给服务端
		f.channel().writeAndFlush(send);
		f.channel().closeFuture().sync();
		return f.channel().attr(AttributeKey.valueOf("Attribute_key")).get();
	}

	public static void main(String[] args) {
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", 7000);
		String message = "hello";
		try {
			Object result = NettyClient.startAndWrite(address, message);
			logger.info("....result:" + result);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			f.channel().close();
			workerGroup.shutdownGracefully();
			logger.info("Closed client!");
		}
	}

}
