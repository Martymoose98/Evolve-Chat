package com.core.shared;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public abstract class UnknownConnection
{
	protected InetAddress ip;
	protected int port;

	public UnknownConnection(String address, int port)
	{
		this(new InetSocketAddress(address, port));
	}

	protected UnknownConnection(InetSocketAddress address)
	{
		this.port = address.getPort();
		this.ip = address.getAddress();
	}

	public abstract boolean isValid();

	public abstract boolean send(UnknownPacket packet);

	public abstract UnknownPacket recv();

	public abstract void close();

	public String toString()
	{
		return this.ip.getHostAddress() + ":" + this.port;
	}

	public String getAddress()
	{
		return this.ip.getHostAddress();
	}

	public int getPort()
	{
		return this.port;
	}
}
