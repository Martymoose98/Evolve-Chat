package com.core.shared;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.core.shared.HandshakePacket.Protocol;

//TODO: think about switching to SocketChannel
public class HandshakeConnection extends TCPConnection
{
	public HandshakeConnection(int port)
	{
		super(port + 1);
	}

	public HandshakeConnection(String address, int port)
	{
		super(address, port + 1);
	}

	public UnknownConnection establish()
	{
		UnknownConnection connection = null;

		this.send(new HandshakePacket(this));
		HandshakePacket response = recv();

		if (Protocol.UDP.value == response.get())
		{
			connection = new UDPConnection(this.ip.getHostAddress(), this.port - 1);
		}
		else if (Protocol.TCP.value == response.get())
		{
			connection = new TCPConnection(this.ip.getHostAddress(), this.port - 1);
		}

		return connection;
	}

	/**
	 *
	 */
	@Override
	public HandshakePacket recv()
	{
		HandshakePacket packet = null; // super.recv();
		try
		{
			if (this.socket == null)
			{
				this.socket = this.ssocket.accept();
				this.sendStream = new ObjectOutputStream(this.socket.getOutputStream());
				this.recvStream = new ObjectInputStream(new BufferedInputStream(this.socket.getInputStream()));
			}

			// SocketChannel sc = SocketChannel.open(this.socket.getRemoteSocketAddress());
			// sc.register(SelectorProvider.provider().openSelector(), 0);
			// this.socket.getChannel().register(null, avail);

			packet = (HandshakePacket) this.recvStream.readObject();

		}
		catch (ClassNotFoundException | IOException e)
		{
			reset();
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return packet;
	}

	public void reset()
	{
		try
		{
			this.sendStream.flush();
			this.socket.close();
			this.socket = null;
			this.sendStream.close();
			this.recvStream.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
