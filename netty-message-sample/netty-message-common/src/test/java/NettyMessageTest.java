
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.xup.demo.netty.nettymessage.common.core.message.Header;
import com.xup.demo.netty.nettymessage.common.core.message.MessageType;
import com.xup.demo.netty.nettymessage.common.core.message.NettyMessage;
import com.xup.demo.netty.nettymessage.common.core.message.NettyMessageDecoder;
import com.xup.demo.netty.nettymessage.common.core.message.NettyMessageEncoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

public class NettyMessageTest {

	@Test
	public void testNettyMessageEncodeAndDecode() throws IOException{
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.HEARTBEAT_REQ.value());
		message.setHeader(header);

		EmbeddedChannel channel = new EmbeddedChannel(new NettyMessageEncoder(),
				new NettyMessageDecoder(1024*1024,4,4));
		//写入出栈处理器，测试encoder
		boolean writeOutbound = channel.writeOutbound(message);
		assertTrue(writeOutbound);
		//取出编码后的消息
		ByteBuf readOutbound =  channel.readOutbound();
		//写入入栈处理器，测试decoder
		boolean writeInbound = channel.writeInbound(readOutbound);
		assertTrue(writeInbound);
		//取出解码后的消息
		NettyMessage readInbound =  channel.readInbound();
		System.out.println(message);
		System.out.println(readInbound);
	}



}
