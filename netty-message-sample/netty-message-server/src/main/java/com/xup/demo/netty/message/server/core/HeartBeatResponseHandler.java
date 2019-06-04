package com.xup.demo.netty.message.server.core;

import com.xup.demo.netty.nettymessage.common.core.message.Header;
import com.xup.demo.netty.nettymessage.common.core.message.NettyMessage;
import com.xup.demo.netty.nettymessage.common.core.message.MessageType;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class HeartBeatResponseHandler extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		NettyMessage message = (NettyMessage) msg;
		if (message.getHeader() != null
				&& message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
			System.out.println("Receive client heart beat message : ---> "+ message);
			NettyMessage heartBeat = buildHeatBeat();
			System.out.println("Send heart beat response message to client : ---> "+ heartBeat);
			ctx.writeAndFlush(heartBeat);
		} else {
			ctx.fireChannelRead(msg);
		}

	}
	
	 private NettyMessage buildHeatBeat() {
	        NettyMessage message = new NettyMessage();
	        Header header = new Header();
	        header.setType(MessageType.HEARTBEAT_RESP.value());
	        message.setHeader(header);
	        return message;
	    }
}
