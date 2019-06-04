package com.xup.demo.netty.message.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.xup.demo.netty.message.client.core.HeartBeatRequestHandler;
import com.xup.demo.netty.message.client.core.LoginAuthRequestHandler;
import com.xup.demo.netty.nettymessage.common.core.message.NettyMessageDecoder;
import com.xup.demo.netty.nettymessage.common.core.message.NettyMessageEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NettyMessageClient {
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	EventLoopGroup group = new NioEventLoopGroup();

	public void connect(InetSocketAddress remoteAddress, InetSocketAddress localAddress) throws Exception {
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("NettyMessageDecoder", new NettyMessageDecoder(1024*1024, 4, 4));
					ch.pipeline().addLast("MessageEncode", new NettyMessageEncoder());
					ch.pipeline().addLast("ReadTimeoutHandler", new ReadTimeoutHandler(50));
					ch.pipeline().addLast("LoginAuthRequestHandler", new LoginAuthRequestHandler());
					ch.pipeline().addLast("HeartBeatRequestHandler", new HeartBeatRequestHandler());
				}
			});

			ChannelFuture f = b.connect(remoteAddress, localAddress).sync();
			f.channel().closeFuture().sync();
		} finally {
			executor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						connect(remoteAddress, localAddress);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 5000, TimeUnit.MILLISECONDS);
		}
	}


	public static void main(String[] args) throws Exception {
		new NettyMessageClient().connect(new InetSocketAddress("127.0.0.1", 7000), 
				new InetSocketAddress("127.0.0.1", 8000));
	}

}
