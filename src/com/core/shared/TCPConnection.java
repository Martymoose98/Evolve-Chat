package com.core.shared;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPConnection extends UnknownConnection
{
	private Socket socket;

	public TCPConnection(int port)
	{
		super(new InetSocketAddress(port));
	}

	public TCPConnection(String address, int port)
	{
		super(address, port);
		
		try
		{
			this.socket = new Socket(address, port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isValid()
	{
		return this.socket != null && this.ip != null && !this.socket.isClosed();
	}

	@Override
	public boolean send(UnknownPacket packet)
	{
		return false;
	}

	@Override
	public UnknownPacket recv()
	{
		return null;
	}

	@Override
	public void close()
	{
		try
		{
			this.socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
