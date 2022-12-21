package com.core.shared;

import java.io.Serializable;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class UserListPacket extends UnknownPacket implements Serializable
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
		super(packet.address, packet.port, packet.data);
	}

	public String[] getUsers()
	{
		byte[] data = this.getData();
		int off = ((data[4] & 0xFF) << 24) | ((data[4 + 1] & 0xFF) << 16) | ((data[4 + 2] & 0xFF) << 8) | (data[4 + 3] & 0xFF);
		int start = Integer.BYTES * 2 + Long.BYTES * off;
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
	
//	public String[] getUsers()
//	{
//		byte[] data = this.getData();
//		int start = Integer.BYTES;
//		int size = 0;
//		String[] users = null;
//		
//		for (int i = start; i < data.length; ++i)
//		{
//			int cur = (data[i] << 24) | (data[i + 1] << 16) | (data[i + 2] << 8) | data[i + 3];
//
//			if (cur == TERMINATION_SEQ)
//				break;
//
//			if (data[i] == 0)
//				++size;
//		}
//		
//		if (size > 0)
//		{
//			users = new String[size];
//			users[0] = new String();
//			
//			for (int i = start, k = 0; i < data.length && k < users.length; ++i)
//			{
//				if (data[i] == 0)
//				{
//					if (++k < users.length)
//						users[k] = new String();
//				}
//				else
//				{
//					users[k] += (char) data[i];
//				}
//			}
//		}
//		return users;
//	}
}
