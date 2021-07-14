package com.core.shared;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UDPConnection extends UnknownConnection
{
	protected DatagramSocket socket;

	public UDPConnection(int port)
	{
		super(new InetSocketAddress("127.0.0.1", port));
		
		try
		{
			this.socket = new DatagramSocket(port);
			this.socket.setReuseAddress(true);
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
	}

	public UDPConnection(String address, int port)
	{
		super(address, port);
		
		try
		{
			this.socket = new DatagramSocket();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isValid()
	{
		return this.ip != null && this.socket != null && !this.socket.isClosed();
	}

	@Override
	public boolean send(UnknownPacket packet)
	{
		if (!this.isValid())
			return false;
		
		try
		{
			this.socket.send(packet.packet);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public UnknownPacket recv()
	{
		UnknownPacket unknown = null;
		
		if (this.isValid())
		{
			unknown = new UnknownPacket(this);
			try
			{
				this.socket.receive(unknown.packet);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
			unknown.parse();
		}
		return unknown;
	}

	@Override
	public void close()
	{
		this.send(new UnknownPacket(this));
		
		synchronized (this.socket)
		{
			this.socket.close();
		}
	}

	public DatagramSocket getSocket()
	{
		return this.socket;
	}
}
