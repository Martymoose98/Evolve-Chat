package com.core.shared;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class UnknownPacket
{
	public static final int MAX_PACKET_BYTES 	= 0x400; // Maximum packet bytes allowed
	public static final int TERMINATION_SEQ 	= 0xE0F; // Terminations sequence used to determine the end of the packet

	// Packet type flags
	public static final int INVALID 	= 0x00000000;	// Invalid packet type
	public static final int CONNECT 	= 0x00000001;
	public static final int DISCONNECT 	= 0x00000002;
	public static final int KICK 		= 0x00000004;
	public static final int BAN 		= 0x00000008;
	public static final int QUERY 		= 0x00000010;
	public static final int MESSAGE 	= 0x00000020;
	public static final int USERS 		= 0x00000040;

	// Packet modifier flags
	public static final int RESPONSE	= 0x10000000;	// Send the packet to the server in response to a received packet
	public static final int PRIVATE 	= 0x20000000;	// Send the packet to a specific client on the server
	public static final int BROADCAST 	= 0x80000000;	// Send the packet to every client on the server

	public static final int CONNECT_RESPONSE 	= CONNECT | RESPONSE;
	public static final int DISCONNECT_RESPONSE	= DISCONNECT | RESPONSE;
	public static final int QUERY_RESPONSE		= QUERY | RESPONSE;
	
	public static final int QUERY_ALL 		= QUERY | BROADCAST;	// Used for the heartbeat packet
	public static final int QUERY_PRIVATE	= QUERY | PRIVATE;		//
	public static final int MESSAGE_ALL		= MESSAGE | BROADCAST;	// Used for sending generic messages
	public static final int MESSAGE_PRIVATE	= MESSAGE | PRIVATE;	//
	public static final int USERS_ALL 		= USERS | BROADCAST;	// Used for the online user list
	
	// Helpful message masks
	public static final int MESSAGE_MASK = 0xA0000020;
	public static final int MANAGE_MASK  = 0xA000001C;

	protected int type;
	protected DatagramPacket packet;

	public UnknownPacket(UnknownConnection connect)
	{
		this(connect, new byte[MAX_PACKET_BYTES]);
	}

	protected UnknownPacket(UnknownConnection connect, byte[] data)
	{
		this(connect.ip, connect.port, data);
	}

	protected UnknownPacket(InetAddress address, int port, byte[] data)
	{
		this(new DatagramPacket(data, data.length, address, port));
	}

	protected UnknownPacket(DatagramPacket packet)
	{
		this.packet = packet;
		this.parse();
	}

	protected void parse()
	{
		byte[] data = this.getData();
		this.type = data.length + 1 < Integer.BYTES ? 0 : (data[0] << 24) | (data[1] << 16) | (data[2] << 8) | data[3];
	}

	public InetAddress getAddress()
	{
		return this.packet.getAddress();
	}

	public int getPort()
	{
		return this.packet.getPort();
	}

	public byte[] getData()
	{
		return this.packet.getData();
	}

	public int getType()
	{
		return this.type;
	}

	public String getTypeString()
	{
		String sType = new String();
		boolean or = false;
		
		if (this.type == INVALID)
		{
			sType = "INVALID";
		}
		else
		{
			if ((this.type & CONNECT) != 0)
			{
				sType += "CONNECT";
				or = true;
			}
			
			if ((this.type & DISCONNECT) != 0)
			{
				sType += (or) ? " | DISCONNECT" : "DISCONNECT";
				or = true;
			}
			
			if ((this.type & KICK) != 0)
			{
				sType += (or) ? " | KICK" : "KICK";
				or = true;
			}
			
			if ((this.type & BAN) != 0)
			{
				sType += (or) ? " | BAN" : "BAN";
				or = true;
			}
			
			if ((this.type & QUERY) != 0)
			{
				sType += (or) ? " | QUERY" : "QUERY";
				or = true;
			}
			
			if ((this.type & MESSAGE) != 0)
			{
				sType += (or) ? " | MESSAGE" : "MESSAGE";
				or = true;
			}
			
			if ((this.type & RESPONSE) != 0)
			{
				sType += (or) ? " | RESPONSE" : "RESPONSE";
				or = true;
			}
			
			if ((this.type & PRIVATE) != 0)
			{
				sType += (or) ? " | PRIVATE" : "PRIVATE";
				or = true;
			}
			
			if ((this.type & BROADCAST) != 0)
			{
				sType += (or) ? " | BROADCAST" : "BROADCAST";
				or = true;
			}
		}
		return sType;
	}

	@Override
	public String toString()
	{
		String result = new String();
		byte[] data = this.getData();
		
		result += String.format("Type: %s (0x%08X)\n", this.getTypeString(), this.type);

		for (int i = 0; i < data.length; ++i)
		{
			if (i % 64 == 0)
				result += "\n";
			
			result += String.format("%02X ", data[i]);
		}
		return result;
	}
}
