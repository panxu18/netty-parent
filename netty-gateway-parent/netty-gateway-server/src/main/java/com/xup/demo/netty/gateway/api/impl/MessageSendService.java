package com.xup.demo.netty.gateway.api.impl;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;

import com.xup.demo.netty.gateway.session.Session;
import com.xup.demo.netty.gateway.session.SessionManager;

import com.xup.demo.netty.gateway.api.MessageSend;

@Service(version = "1.0.0")
public class MessageSendService implements MessageSend{

	@Value("${dubbo.application.name}")
    private String serviceName;
	
	@Override
	public void send(String sessionId, Object msg) {
		System.out.println("remote send command : " + msg);
		Session session = SessionManager.getSession(sessionId);
		session.send(msg);
	}

}
