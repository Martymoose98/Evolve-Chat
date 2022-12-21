package com.core.shared;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class HandshakePacket extends UnknownPacket implements Serializable
{
	public enum Protocol
	{
		UNKNOWN((byte) 0), 
		UDP((byte) 1), 
		TCP((byte) 2);
	 
		protected final byte value;

		Protocol(byte type) { this.value = type; }
	}
	//Protocol proto;
	
	public HandshakePacket(UnknownConnection connect)
	{
		super(connect, ByteBuffer.allocate(Byte.BYTES + 2 * Integer.BYTES)
				.putInt(HANDSHAKE)
				.put(Protocol.UNKNOWN.value)
				.putInt(TERMINATION_SEQ).array());
	}
	
	public HandshakePacket(UnknownPacket packet, Protocol protocol)
	{
		super(packet.address, packet.port, ByteBuffer.allocate(Byte.BYTES + 2 * Integer.BYTES)
				.putInt(HANDSHAKE_RESPONSE)
				.put(protocol.value)
				.putInt(TERMINATION_SEQ).array());
	}
	
	public byte get()
	{
		return data[4];
	}
}
