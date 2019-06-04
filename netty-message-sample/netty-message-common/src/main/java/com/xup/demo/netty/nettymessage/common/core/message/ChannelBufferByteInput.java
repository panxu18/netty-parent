package com.xup.demo.netty.nettymessage.common.core.message;

import java.io.IOException;

import org.jboss.marshalling.ByteInput;

import io.netty.buffer.ByteBuf;

public class ChannelBufferByteInput implements ByteInput{
	private final ByteBuf buffer;

    public ChannelBufferByteInput(ByteBuf buffer) {
        this.buffer = buffer;
    }

	@Override
	public void close() throws IOException {

	}

	@Override
	public int available() throws IOException {
		return buffer.readableBytes();
	}

	@Override
	public int read() throws IOException {
		if (buffer.isReadable()) {
            return buffer.readByte() & 0xff;
        }
        return -1;
	}

	@Override
	public int read(byte[] array) throws IOException {
		return read(array, 0, array.length);
	}

	@Override
	public int read(byte[] dst, int dstIndex, int length) throws IOException {
		int available = available();
        if (available == 0) {
            return -1;
        }

        length = Math.min(available, length);
        buffer.readBytes(dst, dstIndex, length);
        return length;
	}

	@Override
	public long skip(long bytes) throws IOException {
		int readable = buffer.readableBytes();
        if (readable < bytes) {
            bytes = readable;
        }
        buffer.readerIndex((int) (buffer.readerIndex() + bytes));
        return bytes;
	}
}
