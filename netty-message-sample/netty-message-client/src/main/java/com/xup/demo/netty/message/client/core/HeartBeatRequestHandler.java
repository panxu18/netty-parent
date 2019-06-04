package com.xup.demo.netty.message.client.core;

import java.util.concurrent.TimeUnit;

import com.xup.demo.netty.nettymessage.common.core.message.Header;
import com.xup.demo.netty.nettymessage.common.core.message.MessageType;
import com.xup.demo.netty.nettymessage.common.core.message.NettyMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;

public class HeartBeatRequestHandler extends ChannelHandlerAdapter{

	private volatile ScheduledFuture heartBeat;
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		NettyMessage message = (NettyMessage) msg;
		if(message.getHeader() != null
				&& message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
			heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatRequestHandler.HeartBeatTask(ctx),
					0, 5000, TimeUnit.MILLISECONDS);
		} else if(message.getHeader() != null
				&& message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
			System.out.println("Client receive server heart beat message : ---> "+ message);
		} else
			ctx.fireChannelRead(msg);
	}
	
	private class HeartBeatTask implements Runnable{
		private final ChannelHandlerContext ctx;

		public HeartBeatTask(ChannelHandlerContext ctx) {
			super();
			this.ctx = ctx;
		}

		@Override
		public void run() {
			NettyMessage heatBeat = buildHeatBeat();
			System.out.println("Client send heart beat messsage to server : ---> "+ heatBeat);
			 ctx.writeAndFlush(heatBeat);
		}
		
		private NettyMessage buildHeatBeat() {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.HEARTBEAT_REQ.value());
            message.setHeader(header);
            return message;
        }
		
		
	}
	
	

}
