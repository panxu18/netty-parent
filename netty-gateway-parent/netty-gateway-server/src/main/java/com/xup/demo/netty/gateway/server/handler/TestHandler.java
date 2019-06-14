package com.xup.demo.netty.gateway.server.handler;

import com.xup.demo.netty.gateway.session.Session;
import com.xup.demo.netty.gateway.session.SessionManager;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TestHandler extends ChannelInboundHandlerAdapter {
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String remoteAddress = ctx.channel().remoteAddress().toString();
		Session session = new Session(remoteAddress, ctx.channel());
		SessionManager.saveSession(session);
		System.out.println("Remote client connect : " + remoteAddress);
		ctx.writeAndFlush("remote requst has recieced");
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		SessionManager.closeSession(ctx.channel().remoteAddress().toString());
	}

}
