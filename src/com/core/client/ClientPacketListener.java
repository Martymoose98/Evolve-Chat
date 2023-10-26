package com.core.client;

import com.core.shared.BroadcastPacket;
import com.core.shared.ConnectResponsePacket;
import com.core.shared.DisconnectPacket;
import com.core.shared.HeartbeatPacket;
import com.core.shared.PacketListener;
import com.core.shared.UnknownPacket;
import com.core.shared.UserListPacket;

public class ClientPacketListener extends PacketListener
{
	private Client client;

	public ClientPacketListener(Client client, String name)
	{
		super(client.getConnection(), client.isRunning, name);
		this.client = client;
	}

	@Override
	public void process(UnknownPacket packet)
	{
		switch (packet.getType())
		{
		case UnknownPacket.CONNECT_RESPONSE:
			ConnectResponsePacket response = new ConnectResponsePacket(packet);
			this.client.connectionEstablished(response);
			break;
		case UnknownPacket.DISCONNECT:
			DisconnectPacket disconnect = new DisconnectPacket(packet);
			this.client.connectionTerminated(disconnect);
			break;
		case UnknownPacket.MESSAGE_ALL:
			BroadcastPacket broadcast = new BroadcastPacket(packet);
			this.client.broadcast(broadcast);
			break;
		case UnknownPacket.QUERY_ALL:
			this.client.send(new HeartbeatPacket(this.connect, this.client.getId()));
			break;
		case UnknownPacket.USERS_ALL:
			this.client.updateOnlineUsers(new UserListPacket(packet));
			break;
		default:
			this.client.unknownPacketRecieved(packet);
			break;
		}
	}

	@Override
	public void prePacketProcessed(UnknownPacket packet)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postPacketProcessed(UnknownPacket packet)
	{
		// TODO Auto-generated method stub
		
	}
}
