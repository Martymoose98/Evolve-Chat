package com.core.shared;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ConnectResponsePacket extends UnknownPacket
{
	public ConnectResponsePacket(InetAddress address, int port, long id)
	{
		super(address, port, ByteBuffer.allocate(Integer.BYTES * 2 + Long.BYTES)
				.putInt(CONNECT_RESPONSE)
				.putLong(id)
				.putInt(TERMINATION_SEQ).array());
	}

	public ConnectResponsePacket(UnknownPacket packet)
	{
		super(packet.packet);
	}

	public long getId()
	{
		byte[] data = this.getData();
		return ((long) data[4] & 0xFFL) << 56 | ((long) data[5] & 0xFFL) << 48 | ((long) data[6] & 0xFFL) << 40 | ((long) data[7] & 0xFFL) << 32 |
				((long) data[8] & 0xFFL) << 24 | ((long) data[9] & 0xFFL) << 16 | ((long) data[10] & 0xFFL) << 8 | (long) data[11] & 0xFFL;
	}
}
