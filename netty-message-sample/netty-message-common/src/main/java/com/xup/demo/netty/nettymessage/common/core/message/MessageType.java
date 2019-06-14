package com.xup.demo.netty.nettymessage.common.core.message;

public enum MessageType {
	LOGIN_REQ((byte)3),
	LOGIN_RESP((byte)4),
	HEARTBEAT_REQ((byte)5),
	HEARTBEAT_RESP((byte)6),
	MESSAGE_REQ((byte)7),
	MESSAGE_RESP((byte)8);
	
	public byte value;

    MessageType(byte v){
        this.value = v;
    }

    public byte value(){
        return value;
    }
}
