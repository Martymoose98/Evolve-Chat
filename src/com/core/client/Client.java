package com.core.client;

import com.core.server.UniqueIdentifier;
import com.core.shared.TCPConnection;
import com.core.shared.UDPConnection;
import com.core.shared.UnknownConnection;
import com.core.shared.UnknownPacket;
import java.util.concurrent.atomic.AtomicLong;

public class Client
{
	private UnknownConnection connect;
	private Thread send;
	private String name;
	private AtomicLong id;

	public Client(String name)
	{
		this.name = name;
		this.id = new AtomicLong(UniqueIdentifier.INVALID_ID);
	}

	//TODO: The decision of use of TCP or UDP should be decided automatically through a
	// "handshake" with the server or through the login screen in the form of a check-box
	public boolean openConnection(String address, int port, boolean useUDP)
	{
		this.connect = useUDP ? new UDPConnection(address, port) : new TCPConnection(address, port);
		return this.connect.isValid();
	}

	public void send(final UnknownPacket packet)
	{
		this.send = new Thread("Send")
		{
			@Override
			public void run()
			{
				connect.send(packet);
			}
		};
		this.send.start();
	}

	public UnknownPacket receive()
	{
		return this.connect.recv();
	}

	//FIXME: Does this actually need to be in a new thread?
	public void close()
	{
		new Thread("Close")
		{
			@Override
			public void run()
			{
				connect.close();
			}
		}.start();
	}

	public final String getName()
	{
		return this.name;
	}

	public final void setName(String name)
	{
		this.name = name;
	}

	public final UnknownConnection getConnection()
	{
		return this.connect;
	}

	public final boolean isUsingUDP()
	{
		return this.connect instanceof UDPConnection;
	}

	public long getId()
	{
		return this.id.get();
	}

	public void setId(long id)
	{
		this.id.set(id);
	}
}
