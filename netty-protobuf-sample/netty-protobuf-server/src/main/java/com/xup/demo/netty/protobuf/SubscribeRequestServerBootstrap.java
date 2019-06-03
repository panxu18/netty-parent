package com.xup.demo.netty.protobuf;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.xup.demo.netty.protobuf.server.SubscribeRequestProtobufServer;

import io.netty.channel.ChannelFuture;

@SpringBootApplication
public class SubscribeRequestServerBootstrap implements CommandLineRunner{
	
	@Value("${netty.url}")
	private String url;
	
	@Value("${netty.port}")
	private int port;
	
	@Autowired
	private SubscribeRequestProtobufServer server;
	
	@Override
	public void run(String... args) throws Exception {
		InetSocketAddress address = new InetSocketAddress(url, port);
		ChannelFuture f = server.run(address);
		Runtime.getRuntime().addShutdownHook(new Thread(){
			 @Override
	            public void run() {
				 server.destroy();
	            }
		});
		f.channel().closeFuture().sync();
	}

	public static void main(String[] args) {
		SpringApplication.run(SubscribeRequestServerBootstrap.class, args);
	}

}
