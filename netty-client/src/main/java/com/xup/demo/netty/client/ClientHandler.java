package com.xup.demo.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class ClientHandler extends SimpleChannelInboundHandler<Object>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		ctx.channel().attr(AttributeKey.valueOf("Attribute_key")).set(msg);
		ctx.close();
	}

}
