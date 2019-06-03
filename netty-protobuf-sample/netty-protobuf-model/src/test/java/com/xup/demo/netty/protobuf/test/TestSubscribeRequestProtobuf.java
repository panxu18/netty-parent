package com.xup.demo.netty.protobuf.test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xup.demo.netty.protobuf.model.SubscribeRequestProto;

public class TestSubscribeRequestProtobuf {
	
	private static byte[] encode(SubscribeRequestProto.SubscribeRequest req){
		return req.toByteArray();
	}
	
	private static SubscribeRequestProto.SubscribeRequest decode(byte[] body) 
			throws InvalidProtocolBufferException{
		return SubscribeRequestProto.SubscribeRequest.parseFrom(body);
	}
	
	private static SubscribeRequestProto.SubscribeRequest createSubscribeRequest(){
		SubscribeRequestProto.SubscribeRequest.Builder builder = SubscribeRequestProto.
				SubscribeRequest.newBuilder();
		builder.setSubReqId(1);
		builder.setUserName("xupan");
		builder.setAddress("武汉市武汉工程大学流芳校区");
		builder.setProductName("Netty 手册");
		return builder.build();
		
	}
	
	public static void main(String[] args) throws InvalidProtocolBufferException{
		SubscribeRequestProto.SubscribeRequest req = createSubscribeRequest();
		System.out.println("Before encode : " + req.toString());
		SubscribeRequestProto.SubscribeRequest req2 = decode(encode(req));
		System.out.println("After decode : " + req2.toString());
		assert req2.equals(req);
	}
}
