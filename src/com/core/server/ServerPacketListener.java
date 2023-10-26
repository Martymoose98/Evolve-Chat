package com.core.server;

import com.core.shared.BroadcastPacket;
import com.core.shared.ConnectPacket;
import com.core.shared.DisconnectPacket;
import com.core.shared.HandshakePacket;
import com.core.shared.HeartbeatPacket;
import com.core.shared.PacketListener;
import com.core.shared.UnknownPacket;

public class ServerPacketListener extends PacketListener
{
	private Server server;
	
	protected ServerPacketListener(Server server, String name)
	{
		super(server.connect, server.isRunning, name);
		this.server = server;
	}

	@Override
	protected void process(UnknownPacket packet)
	{
		if (packet == null)
			return;

		if (this.server.isRaw.get())
			System.out.println(packet);

		switch (packet.getType())
		{
		case UnknownPacket.HANDSHAKE:
			this.server.handshake(new HandshakePacket(packet, this.server.getProtocol()));
			break;
		case UnknownPacket.CONNECT:
			this.server.connect(new ConnectPacket(packet));
			break;
		case UnknownPacket.DISCONNECT:
			this.server.disconnect(new DisconnectPacket(packet));
			break;
		case UnknownPacket.MESSAGE_ALL:
			this.server.broadcast(new BroadcastPacket(packet));
			break;
		case UnknownPacket.MESSAGE_PRIVATE:
			break;
		case UnknownPacket.QUERY_RESPONSE:
			this.server.markClientActive(new HeartbeatPacket(packet).getId());
			break;
		case UnknownPacket.INVALID:
			System.out.println("Could not process packet. Invalid packet type received.");
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
