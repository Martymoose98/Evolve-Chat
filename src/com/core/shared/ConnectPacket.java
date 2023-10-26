package com.core.shared;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class ConnectPacket extends UnknownPacket implements Serializable
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
		super(packet.address, packet.port, packet.data);
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
