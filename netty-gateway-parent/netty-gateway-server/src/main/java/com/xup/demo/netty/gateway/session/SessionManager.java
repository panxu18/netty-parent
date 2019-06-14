package com.xup.demo.netty.gateway.session;

import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

public class SessionManager {
	private static final ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();
	
	public static void saveSession(String sessionId, Channel channel){
		Session session = new Session(sessionId, channel);
		saveSession(session);
	}
	
	public static void saveSession(Session session){
		if(sessionMap.get(session.getSessionId()) != null)
			closeSession(session);
		sessionMap.put(session.getSessionId(), session);
	}

	public static void closeSession(Session session) {
		session.close();
		sessionMap.remove(session.getSessionId());
	}
	
	public static void closeSession(String sessionId) {
		closeSession(getSession(sessionId));
	}
	
	public static Session getSession(String sessionId){
		return sessionMap.get(sessionId);
	}
	
	
}
