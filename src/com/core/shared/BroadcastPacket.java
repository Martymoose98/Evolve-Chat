package com.core.shared;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class BroadcastPacket extends UnknownPacket
{
	public BroadcastPacket(UnknownConnection connect, long id, String message)
	{
		super(connect, ByteBuffer.allocate(Integer.BYTES * 2 + Long.BYTES + message.length())
				.putInt(MESSAGE_ALL)
				.putLong(id)
				.put(message.getBytes())
				.putInt(3599).array());
	}

	public BroadcastPacket(InetAddress address, int port, long id, String message)
	{
		super(address, port, ByteBuffer.allocate(Integer.BYTES * 2 + Long.BYTES + message.length())
				.putInt(MESSAGE_ALL)
				.putLong(id)
				.put(message.getBytes())
				.putInt(3599).array());
	}

	public BroadcastPacket(UnknownPacket packet)
	{
		super(packet.packet);
	}

	public long getId()
	{
		byte[] data = this.getData();
		return ((long) data[4] & 0xFFL) << 56 | ((long) data[5] & 0xFFL) << 48 | ((long) data[6] & 0xFFL) << 40 | ((long) data[7] & 0xFFL) << 32 |
				((long) data[8] & 0xFFL) << 24 | ((long) data[9] & 0xFFL) << 16 | ((long) data[10] & 0xFFL) << 8 | (long) data[11] & 0xFFL;
	}

	public String getMessage()
	{
		byte[] data = this.getData();
		int offset = Integer.BYTES + Long.BYTES;
		
		for (int i = offset - 1; i < data.length; ++i)
		{
			int cur = (data[i] << 24) | (data[i + 1] << 16) | (data[i + 2] << 8) | data[i + 3];
			
			if (cur == TERMINATION_SEQ)
				return new String(data, offset, i - offset);
		}
		return null;
	}
}
