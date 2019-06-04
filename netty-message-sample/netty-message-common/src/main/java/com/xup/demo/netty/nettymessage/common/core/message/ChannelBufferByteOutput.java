package com.xup.demo.netty.nettymessage.common.core.message;

import java.io.IOException;

import org.jboss.marshalling.ByteOutput;

import io.netty.buffer.ByteBuf;

public class ChannelBufferByteOutput implements ByteOutput{
	private final ByteBuf buffer;

	/**
     * Create a new instance which use the given {@link ByteBuf}
     */
    public ChannelBufferByteOutput(ByteBuf buffer) {
        this.buffer = buffer;
    }

	@Override
	public void close() throws IOException {

	}

	@Override
	public void flush() throws IOException {

	}

	@Override
	public void write(int b) throws IOException {
		buffer.writeByte(b);
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		buffer.writeBytes(bytes);
	}

	@Override
	public void write(byte[] bytes, int srcIndex, int length) throws IOException {
		buffer.writeBytes(bytes, srcIndex, length);
	}

	/**
     * Return the {@link ByteBuf} which contains the written content
     */
    ByteBuf getBuffer() {
        return buffer;
    }

}
