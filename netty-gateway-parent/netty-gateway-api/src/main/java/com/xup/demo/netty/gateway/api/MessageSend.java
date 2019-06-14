package com.xup.demo.netty.gateway.api;

public interface MessageSend {
	void send(String sessionId, Object msg);
}
