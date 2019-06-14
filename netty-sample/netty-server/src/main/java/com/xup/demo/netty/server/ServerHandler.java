package com.xup.demo.netty.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends ChannelHandlerAdapter {
    @Override
	public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("server receive message :"+ o);
        channelHandlerContext.channel().writeAndFlush("yes server already accept your message" + o);
        channelHandlerContext.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive>>>>>>>>");
    }
}
