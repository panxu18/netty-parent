package com.xup.demo.netty.gateway.server;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.xup.demo.netty.gateway.server.handler.TestHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

@Component
public class Server {
	@Value(value = "${gateway.server.url:127.0.0.1}")
	private String url;
	
	@Value(value = "${gateway.server.port:8160}")
	private int port;
	
	@Value(value = "${gateway.server.bossGroupNums:0}")
	private int bossGroupNums;
	
	@Value(value = "${gateway.server.workerGroupNums:0}")
	private int workerGroupNums;
	
	
	private final EventLoopGroup bossGroup = new NioEventLoopGroup(bossGroupNums);
	private final EventLoopGroup workerGroup = new NioEventLoopGroup(workerGroupNums);
	private ChannelFuture f;
	private Channel channel;
	
	public ChannelFuture start() throws InterruptedException{
		try{
			
			ServerBootstrap bootStrap = new ServerBootstrap();
			bootStrap.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 100)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {
				
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
					ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
					ch.pipeline().addLast("TestHandler", new TestHandler());
				}
			});
			
			f = bootStrap.bind(new InetSocketAddress(url, port)).sync();
			channel = f.channel();
			return f;
			
			
		} catch (InterruptedException e){
			e.printStackTrace();
			throw e;
		} finally{
			if (f != null && f.isSuccess()) {
				System.out.println("Netty server start ok : " + url + " : " + port);
            } else {
            	System.out.println("Netty server start up Error!");
            }
		}
		
	}
	
	public void stop(){
		if(channel != null) { channel.close();}
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
	

}
