package com.xup.demo.netty.gateway.session;

import java.util.HashSet;
import java.util.Set;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;

public final class Session {
	private final String sessionId;
	private final Channel channel;
	private Set<SessionCloseListener> closeListeners;
	
	public Session(String sessionId, Channel channel) {
		this.sessionId = sessionId;
		this.channel = channel;
		this.closeListeners = new HashSet<>();
	}

	ChannelFuture write(Object msg){
		return channel.write(msg);
	}
	
	ChannelFuture write(Object msg, ChannelPromise promise){
		return channel.write(msg, promise);
	}
	
	ChannelFuture close(){
		callListener();
		return channel.close();
	}
	
	ChannelFuture close(ChannelPromise promise){
		callListener();
		return channel.close(promise);
	}

	public final String getSessionId() {
		return sessionId;
	}

	public void addListener(SessionCloseListener listener){
		closeListeners.add(listener);
	}
	
	public void removeListener(SessionCloseListener listener){
		closeListeners.remove(listener);
	}
	
	private void callListener(){
		closeListeners.forEach(SessionCloseListener::close);
	}

	public ChannelFuture send(Object msg) {
		return channel.writeAndFlush(msg);
	}
	
	public ChannelFuture send(Object msg, ChannelPromise promise) {
		return channel.writeAndFlush(msg, promise);
	}
	
	
}
