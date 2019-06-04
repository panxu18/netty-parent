package com.xup.demo.netty.protobuf.server;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.xup.demo.netty.protobuf.model.SubscribeRequestProto;
import com.xup.demo.netty.protobuf.model.SubscribeResponseProto;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;

@Component
public class SubscribeRequestProtobufServer {
	private static Logger logger = LogManager.getLogger(SubscribeRequestProtobufServer.class);
	private ChannelFuture f;
	private EventLoopGroup boosGroup = new NioEventLoopGroup();
	private EventLoopGroup workerGroup = new NioEventLoopGroup();
	private ServerBootstrap b = new ServerBootstrap();
	private Channel channel;

	public ChannelFuture run(InetSocketAddress address){
		boosGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		b = new ServerBootstrap();
		try {
			b.group(boosGroup, workerGroup).channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler())
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
					ch.pipeline().addLast(new ProtobufDecoder(
							SubscribeRequestProto.SubscribeRequest.getDefaultInstance()));
					ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
					ch.pipeline().addLast(new ProtobufEncoder());
					ch.pipeline().addLast(new SubscribeRequestHandler());
				}
			});

			f = b.bind(address).sync();
			channel = f.channel();
		} catch (InterruptedException e) {
			logger.error("Netty start error:", e);
		} finally{
			if (f!=null && f.isSuccess()){
				logger.info("Netty server listening " + address.getHostName() + " on port " + address.getPort() + " and ready for connections...");
			} else{
				destroy();
				logger.error("Netty server start up Error!");
			}
		}
		return f;
	}

	public void destroy(){
		logger.info("Shutdown Netty Server...");
		if(channel != null) { channel.close();}
		workerGroup.shutdownGracefully();
		boosGroup.shutdownGracefully();
		logger.info("Shutdown Netty Server Success!");
	}

	private class SubscribeRequestHandler extends ChannelHandlerAdapter{

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			SubscribeRequestProto.SubscribeRequest req = (SubscribeRequestProto.SubscribeRequest) msg;
			if("xupan".equalsIgnoreCase(req.getUserName())){
				logger.info("Service accept client subscribe req : [" + 
						req.toString() + "]");
				ctx.writeAndFlush(response(req.getSubReqId()));
			}

		}

		private SubscribeResponseProto.SubscribeResponse response(int subReqID) {
			SubscribeResponseProto.SubscribeResponse.Builder build = SubscribeResponseProto.SubscribeResponse
					.newBuilder();
			build.setSubReqId(subReqID);
			build.setRespCode(0);
			build.setDesc("Order Successlly!");
			return build.build();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			logger.error("channel error:", cause);
			ctx.close();
		}

	}


}


