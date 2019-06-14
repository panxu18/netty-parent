package com.xup.demo.netty.gateway.dubbo;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.xup.demo.netty.gateway.api.MessageSend;

@EnableAutoConfiguration
public class ConsumerBootStrap {

	    @Reference(version = "1.0.0")
	    private MessageSend messageSendService;

	    public static void main(String[] args) {
	        SpringApplication.run(ConsumerBootStrap.class).close();
	    }

	    @Bean
	    public ApplicationRunner runner() {
	        return args -> messageSendService.send("/127.0.0.1:60414", "Test send message to client");
	    }
}