package com.core.shared;

import java.io.Serializable;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class DisconnectPacket extends UnknownPacket implements Serializable
{
	public static final int REASON_USER_REQUESTED = 0;
	public static final int REASON_SERVER_REQUESTED = 1;
	public static final int REASON_TIMEOUT = 2;

//	public enum Reason
//	{
//		UNKNOWN(0), 
//		USER_REQUESTED(1),
//		REASON_SERVER_REQUESTED(2),
//		REASON_TIMEOUT(3);
//
//		protected final int value;
//		
//		Reason(int value) { this.value = value; }
//	}
//	
	public DisconnectPacket(UnknownConnection connect, long id, int reason)
	{
		super(connect, ByteBuffer.allocate(Integer.BYTES * 3 + Long.BYTES)
				.putInt(DISCONNECT)
				.putLong(id)
				.putInt(reason)
				.putInt(TERMINATION_SEQ).array());
	}

	public DisconnectPacket(InetAddress address, int port, long id, int reason)
	{
		super(address, port, ByteBuffer.allocate(Integer.BYTES * 3 + Long.BYTES)
				.putInt(DISCONNECT)
				.putLong(id)
				.putInt(reason)
				.putInt(TERMINATION_SEQ).array());
	}

	public DisconnectPacket(UnknownPacket packet)
	{
		super(packet.address, packet.port, packet.data);
	}

	public long getId()
	{
		byte[] data = this.getData();
		return ((long) data[4] & 0xFFL) << 56 | ((long) data[5] & 0xFFL) << 48 | ((long) data[6] & 0xFFL) << 40 | ((long) data[7] & 0xFFL) << 32 | 
				((long) data[8] & 0xFFL) << 24 | ((long) data[9] & 0xFFL) << 16 | ((long) data[10] & 0xFFL) << 8 | (long) data[11] & 0xFFL;
	}

	public int getReason()
	{
		byte[] data = this.getData();
		return (data[12] << 24) | (data[13] << 16) | (data[14] << 8) | data[15];
	}
}
