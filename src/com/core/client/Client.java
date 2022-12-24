package com.core.client;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.core.server.UniqueIdentifier;
import com.core.shared.BroadcastPacket;
import com.core.shared.ConnectPacket;
import com.core.shared.ConnectResponsePacket;
import com.core.shared.DisconnectPacket;
import com.core.shared.HandshakeConnection;
import com.core.shared.PacketDispatcher;
import com.core.shared.UDPConnection;
import com.core.shared.UnknownConnection;
import com.core.shared.UnknownPacket;
import com.core.shared.UserListPacket;

public class Client
{
	private UnknownConnection connect;
	private ClientPacketListener incomingPackets;
	private PacketDispatcher outgoingPackets;
	private ClientInterface iface;
	private OnlineUserList<String> users;
	private String name;
	private AtomicLong id;
	protected AtomicBoolean isRunning;
	
	public Client(ClientInterface iface, String name)
	{
		this.iface = iface;
		this.users = new OnlineUserList<String>();
		this.name = name;
		this.id = new AtomicLong(UniqueIdentifier.INVALID_ID);
		this.isRunning = new AtomicBoolean(true);
	}

	public boolean openConnection(String address, int port)
	{
		HandshakeConnection handshake = new HandshakeConnection(address, port);
		this.connect = handshake.establish();
		handshake.close();
		this.incomingPackets = new ClientPacketListener(this, "Incoming Packets");
		this.outgoingPackets = new PacketDispatcher(this.connect, this.isRunning, "Outgoing Packets");
		this.send(new ConnectPacket(this.connect, name));
		return this.connect.isValid();
	}

	public void send(UnknownPacket packet)
	{
		this.outgoingPackets.send(packet);
	}

	//FIXME: Does this actually need to be in a new thread?
	// This is not how you properly close the connection now!
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
	
	public void connectionEstablished(ConnectResponsePacket packet)
	{
		this.setId(packet.getId());
		this.iface.consolefln(Color.BLUE, "Successfully connected to server! IP: %s ID: %d\nWelcome, %s", this.connect, this.getId(), this.name);
	}
	
	public void connectionTerminated(DisconnectPacket packet)
	{
		this.close();
		this.iface.consolefln(Color.BLUE, "Successfully disconnected from server! Reason: %d IP: %s ID: %d", packet.getReason(), this.connect, this.getId());
	}
	
	public void broadcast(BroadcastPacket packet)
	{
		// FIXME: fix this this is just a temp hack
		this.iface.append(Color.GREEN, Color.BLACK, packet.getMessage() + "\n");
	}
	
	public void updateOnlineUsers(UserListPacket packet)
	{
		this.users.update(packet.getUsers());
	}
	
	public void unknownPacketRecieved(UnknownPacket packet)
	{
		this.iface.consolefln(Color.RED, "[%s] Strange packet received:\n%s\n", this.name, this.connect);
	}
	
	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public UnknownConnection getConnection()
	{
		return this.connect;
	}

	public boolean isUsingUDP()
	{
		return this.connect instanceof UDPConnection;
	}

	public long getId()
	{
		return this.id.get();
	}

	private void setId(long id)
	{
		this.id.set(id);
	}
	
	public void getRunning()
	{
		this.isRunning.get();
	}
	
	public void setRunning(boolean isRunning)
	{
		this.isRunning.set(isRunning);
	}

	public OnlineUserList<String> getOnlineUsers()
	{
		return this.users;
	}
}
