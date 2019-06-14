package com.xup.demo.netty.gateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xup.demo.netty.gateway.server.Server;

import io.netty.channel.ChannelFuture;

@EnableDubbo
@SpringBootApplication
public class ServerApp implements CommandLineRunner{
	
	@Autowired
	private Server server;

	public static void main(String[] args) {
		SpringApplication.run(ServerApp.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ChannelFuture f = server.start();
		f.channel().closeFuture().sync();
		Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
            	server.stop();
            }
        });
	}

}
