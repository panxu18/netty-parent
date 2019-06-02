package com.xup.demo.netty.serialize;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xup.demo.netty.serialize.server.SubscribeRequestServer;

import io.netty.channel.ChannelFuture;

@SpringBootApplication
public class SubscribRequestServerBootStrap implements CommandLineRunner{
	
	@Value("${netty.url}")
	private String url;
	
	@Value("${netty.port}")
	private int port;
	
	@Autowired
	private SubscribeRequestServer subReqServer;
	

	@Override
	public void run(String... args) throws Exception {
		InetSocketAddress address = new InetSocketAddress(url, port);
		ChannelFuture future = subReqServer.run(address);
		Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
            	subReqServer.destroy();
            }
        });
		future.channel().closeFuture().syncUninterruptibly();
	}
	
	
	public static void main(String[] args){
		SpringApplication.run(SubscribRequestServerBootStrap.class, args);
	}

}
