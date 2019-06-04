
import com.xup.demo.netty.nettymessage.common.core.message.Header;
import com.xup.demo.netty.nettymessage.common.core.message.NettyMessage;
import com.xup.demo.netty.nettymessage.common.core.message.NettyMessageDecoder;
import com.xup.demo.netty.nettymessage.common.core.message.NettyMessageEncoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.xup.demo.netty.nettymessage.common.core.message.MessageType;

public class Test {

	public static void main(String[] args) throws Exception {
		NettyMessage message1 = new NettyMessage();
		Header header = new Header();
        header.setType(MessageType.HEARTBEAT_REQ.value());
        message1.setHeader(header);
		
		NettyMessageEncoder encoder = new NettyMessageEncoder();
		NettyMessageDecoder decoder = new NettyMessageDecoder(1024*1024, 4, 4);
		
		System.out.println(message1);
		ByteBuf buf1 = Unpooled.buffer();
		buf1 = encoder.encodeTest(message1);
		System.out.println(buf1.readableBytes());
//		System.out.println(buf1.readInt() + buf1.readInt());
		NettyMessage message2 = (NettyMessage) decoder.decodeTest(buf1);
		System.out.println(message2);
//		NettyMessage message3 = (NettyMessage) decoder.decodeTest(encoder.encodeTest(message2));
//		System.out.println(message3);
		

	}

}
