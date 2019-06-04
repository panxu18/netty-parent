package com.xup.demo.netty.message.server;

import java.io.IOException;

import com.xup.demo.netty.message.server.core.HeartBeatResponseHandler;
import com.xup.demo.netty.message.server.core.LoginAuthResponseHandler;
import com.xup.demo.netty.nettymessage.common.core.message.NettyMessageDecoder;
import com.xup.demo.netty.nettymessage.common.core.message.NettyMessageEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NettyMessageServer {
	public void bind() throws Exception {
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)throws IOException{
                    	ch.pipeline().addLast("NettyMessageDecoder", new NettyMessageDecoder(1024*1024, 4, 4));
    					ch.pipeline().addLast("MessageEncode", new NettyMessageEncoder());
    					ch.pipeline().addLast("ReadTimeoutHandler", new ReadTimeoutHandler(50));
    					ch.pipeline().addLast("LoginAuthRequestHandler", new LoginAuthResponseHandler());
    					ch.pipeline().addLast("HeartBeatRequestHandler", new HeartBeatResponseHandler());
                    }
                });

        // 绑定端口，同步等待成功
        b.bind("127.0.0.1", 7000).sync()
        .channel().closeFuture().sync();
        System.out.println("Netty server start ok : " + ("127.0.0.1" + " : " + 7000));
    }

    public static void main(String[] args) throws Exception {
        new NettyMessageServer().bind();
    }
}
