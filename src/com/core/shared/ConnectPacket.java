package com.core.shared;

import java.nio.ByteBuffer;

public class ConnectPacket extends UnknownPacket
{
	public ConnectPacket(UnknownConnection connect, String name)
	{
		super(connect, ByteBuffer.allocate(Integer.BYTES * 2 + name.length())
				.putInt(CONNECT)
				.put(name.getBytes())
				.putInt(TERMINATION_SEQ).array());
	}

	public ConnectPacket(UnknownPacket packet)
	{
		super(packet.packet);
	}

	public String getName()
	{
		byte[] data = this.getData();
		int offset = Integer.BYTES;
	
		for (int i = offset - 1; i < data.length; ++i)
		{
			int cur = (data[i] << 24) | (data[i + 1] << 16) | (data[i + 2] << 8) | data[i + 3];
			
			if (cur == TERMINATION_SEQ)
				return new String(data, offset, i - offset);
		}
		return null;
	}
}
