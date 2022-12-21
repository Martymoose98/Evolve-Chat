package com.core.shared;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPConnection extends UnknownConnection
{
	protected Socket socket;
	protected ServerSocket ssocket;
	protected ObjectOutputStream sendStream;
	protected ObjectInputStream recvStream;


	public TCPConnection(int port)
	{
		super("127.0.0.1", port);
		createSocket(port);
	}

	public TCPConnection(String address, int port)
	{
		super(address, port);
		createSocket(address, port);
	}

	private void createSocket(int port)
	{
		try
		{
			this.ssocket = new ServerSocket(port);
			this.ssocket.setReuseAddress(true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void createSocket(String address, int port)
	{
		try
		{
			this.socket = new Socket(address, port);
			this.sendStream = new ObjectOutputStream(this.socket.getOutputStream());
			this.recvStream = new ObjectInputStream(new BufferedInputStream(this.socket.getInputStream()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isValid()
	{
		return ((this.ssocket != null && !this.ssocket.isClosed()) || (this.socket != null && !this.socket.isClosed())) && this.ip != null;
	}

	@Override
	public boolean send(UnknownPacket packet)
	{
		if (!this.isValid())
			return false;

		try
		{	
			this.sendStream.writeObject(packet);
			this.sendStream.flush();
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
		UnknownPacket packet = null;

		try
		{
			if (this.socket == null)
			{
				this.socket = this.ssocket.accept();
				this.sendStream = new ObjectOutputStream(this.socket.getOutputStream());
				this.recvStream =  new ObjectInputStream(new BufferedInputStream(this.socket.getInputStream()));
			}

			//if (this.recvStream.available() > 0)
			{
				Object obj = this.recvStream.readObject();
				
				if (obj instanceof UnknownPacket)
					packet = (UnknownPacket) obj;
			}
		}
		catch (ClassNotFoundException | IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return packet;
	}

	@Override
	public void close()
	{
		try
		{
			
			this.sendStream.flush();
			
			if (ssocket != null)
				this.ssocket.close();
			
			this.socket.close();
			this.sendStream.close();
			this.recvStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
