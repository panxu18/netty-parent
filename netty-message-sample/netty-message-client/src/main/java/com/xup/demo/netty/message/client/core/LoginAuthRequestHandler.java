package com.xup.demo.netty.message.client.core;

import com.xup.demo.netty.nettymessage.common.core.message.Header;
import com.xup.demo.netty.nettymessage.common.core.message.NettyMessage;
import com.xup.demo.netty.nettymessage.common.core.message.MessageType;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class LoginAuthRequestHandler extends ChannelHandlerAdapter{
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		NettyMessage message = buildLoginRequest();
		ctx.writeAndFlush(message);
		System.out.println("Atempt to logging :" + message);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		NettyMessage message = (NettyMessage) msg;
		if (message.getHeader() != null &&
				message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
			byte loginResult = (byte) message.getBody();
			if(loginResult != (byte) 0){
				ctx.close();
			}else{
				System.out.println("Login is ok :" + message);
				ctx.fireChannelRead(msg);
			}
		} else
			ctx.fireChannelRead(msg);
	}
	
	private NettyMessage buildLoginRequest() {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.LOGIN_REQ.value());
		message.setHeader(header);
        return message;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

}
