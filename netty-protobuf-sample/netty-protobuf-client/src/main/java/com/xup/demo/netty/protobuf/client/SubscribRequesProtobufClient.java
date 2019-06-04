package com.xup.demo.netty.protobuf.client;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xup.demo.netty.protobuf.model.SubscribeRequestProto;
import com.xup.demo.netty.protobuf.model.SubscribeResponseProto;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class SubscribRequesProtobufClient {
	private static Logger logger = LogManager.getLogger(SubscribRequesProtobufClient.class);

	Bootstrap b;
	EventLoopGroup workerGroup;

	public SubscribRequesProtobufClient(InetSocketAddress address){
		init(address);
	}

	private void init(InetSocketAddress address){
		logger.info("init...");
		try {
			b = new Bootstrap();
			workerGroup = new NioEventLoopGroup();
			b.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
					ch.pipeline().addLast(new ProtobufDecoder(SubscribeResponseProto.SubscribeResponse
							.getDefaultInstance()));
					ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
					ch.pipeline().addLast(new ProtobufEncoder());
					ch.pipeline().addLast(new SubscribeRequestHandler());

				}

			});
			ChannelFuture f = b.connect(address).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			workerGroup.shutdownGracefully();
		}
	}

	private class SubscribeRequestHandler extends ChannelHandlerAdapter{

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			logger.info("Receive server response: [" + msg + "]");

		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			for(int i = 0; i < 10; i++){
				ctx.write(subscribeRequest(i));
			}
			ctx.flush();
		}

		private SubscribeRequestProto.SubscribeRequest subscribeRequest(int subReqID){
			SubscribeRequestProto.SubscribeRequest.Builder build = SubscribeRequestProto.SubscribeRequest
					.newBuilder();
			build.setSubReqId(subReqID);
			build.setUserName("xupan");
			build.setProductName("Netty in Action");
			build.setAddress("wuhan institutes");
			return build.build();
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			ctx.flush();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			logger.error("Channel error : " + cause);
			ctx.close();
		}

	}

	public static void main(String[] args) {
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", 7000);
		SubscribRequesProtobufClient subReqClient = new SubscribRequesProtobufClient(address);
	}

}
