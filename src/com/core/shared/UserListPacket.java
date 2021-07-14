package com.core.shared;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class UserListPacket extends UnknownPacket
{
	public UserListPacket(InetAddress address, int port, byte[] users)
	{
		super(address, port, ByteBuffer.allocate(Integer.BYTES * 2 + users.length)
				.putInt(USERS_ALL)
				.put(users)
				.putInt(TERMINATION_SEQ).array());
	}

	public UserListPacket(UnknownPacket packet)
	{
		super(packet.packet);
	}

	public String[] getUsers()
	{
		byte[] data = this.getData();
		int start = Integer.BYTES;
		int size = 0;
		String[] users = null;
		
		for (int i = start; i < data.length; ++i)
		{
			int cur = (data[i] << 24) | (data[i + 1] << 16) | (data[i + 2] << 8) | data[i + 3];

			if (cur == TERMINATION_SEQ)
				break;

			if (data[i] == 0)
				++size;
		}
		
		if (size > 0)
		{
			users = new String[size];
			users[0] = new String();
			
			for (int i = start, k = 0; i < data.length && k < users.length; ++i)
			{
				if (data[i] == 0)
				{
					if (++k < users.length)
						users[k] = new String();
				}
				else
				{
					users[k] += (char) data[i];
				}
			}
		}
		return users;
	}
}
