package com.core.server;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import com.core.shared.BroadcastPacket;
import com.core.shared.ConnectPacket;
import com.core.shared.ConnectResponsePacket;
import com.core.shared.DisconnectPacket;
import com.core.shared.HandshakeConnection;
import com.core.shared.HandshakePacket;
import com.core.shared.HandshakePacket.Protocol;
import com.core.shared.HeartbeatPacket;
import com.core.shared.PacketDispatcher;
import com.core.shared.TCPConnection;
import com.core.shared.UDPConnection;
import com.core.shared.UnknownConnection;
import com.core.shared.UnknownPacket;
import com.core.shared.UserListPacket;

public class Server implements Runnable
{
	private List<ServerClient> clients;
	protected UnknownConnection connect;
	private HandshakeConnection handshake;
	private Thread server;
	private Thread manage;	
	private Thread receiveHandshake;
	@SuppressWarnings("unused")
	private ServerPacketListener incomingPackets;
	private PacketDispatcher outgoingPackets;
	private Scanner scanner;
	protected AtomicBoolean isRunning;
	protected AtomicBoolean isRaw;

	public Server(int port, boolean useUDP)
	{
		this.scanner = new Scanner(System.in);
		this.clients = Collections.synchronizedList(new ArrayList<ServerClient>());
		this.isRaw = new AtomicBoolean(false);
		this.isRunning = new AtomicBoolean(false);
		this.handshake = new HandshakeConnection(port);	
		this.connect = useUDP ? new UDPConnection(port) : new TCPConnection(port);
		
		if (this.connect.isValid())
		{
			this.server = new Thread(this, "Server");
			this.server.start();
		}
	}

	@Override
	public void run()
	{
		this.isRunning.set(true);

		System.out.println("[SERVER]: " + LocalDateTime.now() + " Server initalized on port " + this.connect.getPort());

		this.heartbeat();
		this.receiveHandshake();
		this.incomingPackets = new ServerPacketListener(this, "Incoming Packets");
		this.outgoingPackets = new PacketDispatcher(this.connect, this.isRunning, "Outgoing Packets");
		
		while (this.isRunning.get())
		{
			String text = this.scanner.nextLine();

			if (text.startsWith("$"))
			{
				if (text.equals("$raw"))
				{
					this.toggleRawMode();
				}
				else if (text.equals("$queryclients"))
				{
					this.queryClients();
				}
				else if (text.equals("$stop"))
				{
					this.stop();
				}
				else if (text.equals("$help"))
				{
					this.printHelp();
				}
				else if (text.startsWith("$kick"))
				{
					String name = text.split(" ")[1];
					int id = -1;
					boolean isId = true;

					try
					{
						id = Integer.parseInt(name);
					}
					catch (NumberFormatException e)
					{
						isId = false;
					}

					ServerClient client = isId ? this.getClientById(id) : this.getClientByName(name);

					if (client == null)
						System.out.println(isId ? "Client with id: " + id + " does not exist! Check client id." : "Client with name: " + name + " does not exist! Check client name.");
					else
						this.disconnect(client, new DisconnectPacket(client.address, client.port, client.id, DisconnectPacket.REASON_SERVER_REQUESTED));
				}
				else
				{
					System.out.println("Unknown command.");
					this.printHelp();
				}
			}
			this.broadcast(new BroadcastPacket(this.connect, -1L, String.format("Server: %s", text)));
		}
	}

	private void stop()
	{
		synchronized (this.clients)
		{
			for (ServerClient client : this.clients)
				this.outgoingPackets.send(new DisconnectPacket(client.address, client.port, client.id, DisconnectPacket.REASON_SERVER_REQUESTED));
		}
		this.isRunning.set(false);
		this.close();
	}

	public void close()
	{
		new Thread("Close")
		{
			@Override
			public void run()
			{
				connect.close();
				scanner.close();
			}
		}.start();
	}

	private void heartbeat()
	{
		this.manage = new Thread("Manage")
		{
			@Override
			public void run()
			{
				while (isRunning.get())
				{
					byte[] names = queryClientNames();

					synchronized (clients)
					{
						for (ServerClient client : clients)
						{
							outgoingPackets.send(new UserListPacket(client.address, client.port, names));
							client.ping(outgoingPackets);
						}
					}

					try
					{
						Thread.sleep(2000L);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		};
		this.manage.start();
	}

	private void receiveHandshake()
	{
		this.receiveHandshake = new Thread("Inbound Handshake Packets")
		{
			@Override
			public void run()
			{
				while (isRunning.get())
				{
					process(handshake.recv());
					
					try
					{
						Thread.sleep(4000L);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		};
		this.receiveHandshake.start();
	}

	private void process(UnknownPacket packet)
	{
		if (packet == null)
			return;

		if (this.isRaw.get())
			System.out.println(packet);

		switch (packet.getType())
		{
		case UnknownPacket.HANDSHAKE:
			this.handshake(new HandshakePacket(packet, this.getProtocol()));
			break;
		case UnknownPacket.CONNECT:
			this.connect(new ConnectPacket(packet));
			break;
		case UnknownPacket.DISCONNECT:
			this.disconnect(new DisconnectPacket(packet));
			break;
		case UnknownPacket.MESSAGE_ALL:
			this.broadcast(new BroadcastPacket(packet));
			break;
		case UnknownPacket.MESSAGE_PRIVATE:
			break;
		case UnknownPacket.QUERY_RESPONSE:
			this.markClientActive(new HeartbeatPacket(packet).getId());
			break;
		case UnknownPacket.INVALID:
			System.out.println("Could not process packet. Invalid packet type received.");
			break;
		}
	}
	
	protected void handshake(HandshakePacket packet)
	{
		this.handshake.send(packet);
	}
	
	protected void connect(ConnectPacket packet)
	{
		ServerClient client = new ServerClient(packet.getName(), packet.getAddress(), packet.getPort(), UniqueIdentifier.getIndentifier());
		this.clients.add(client);
		this.outgoingPackets.send(new ConnectResponsePacket(client.address, client.port, client.id));
		System.out.println(client.name + " connected from " + client.address.getHostAddress() + ":" + client.port + " Id: " + client.id);
	}

	protected void disconnect(DisconnectPacket packet)
	{
		ServerClient client = this.getClientById(packet.getId());
		this.disconnect(client, packet);
	}

	protected void disconnect(ServerClient client, DisconnectPacket packet)
	{
		if (client == null)
			return;

		this.clients.remove(client);
		this.outgoingPackets.send(packet);
		String sReason = (packet.getReason() == DisconnectPacket.REASON_TIMEOUT) ? " timed out from " : " disconnected from ";
		System.out.println(client.name + sReason + client.address.getHostAddress() + ":" + client.port + " Id: " + client.id);
	}

	protected void broadcast(BroadcastPacket packet)
	{
		ServerClient sender = this.getClientById(packet.getId());

		if (sender == null)
			return;

		String msg = String.format("%s: %s", sender.name, packet.getMessage());
		
		synchronized (this.clients)
		{
			for (ServerClient client : this.clients)
			{
				this.outgoingPackets.send(new BroadcastPacket(client.address, client.port, sender.id, msg));
			}
		}
	}

	protected void markClientActive(long id)
	{
		this.getClientById(id).markActive();
	}

	private ServerClient getClientByName(String name)
	{
		synchronized (this.clients)
		{
			for (ServerClient client : this.clients)
				if (client.name.equals(name))
					return client;
		}
		return null;
	}

	private ServerClient getClientById(long id)
	{
		synchronized (this.clients)
		{
			for (ServerClient client : this.clients)
				if (client.id == id)
					return client;
		}
		return null;
	}

	private void queryClients()
	{
		System.out.println("===< Clients >===");

		synchronized (this.clients)
		{
			for (ServerClient client : this.clients)
				System.out.println(client);
		}
	}

	private byte[] queryClientNames()
	{	
		String users = new String();
		
		synchronized (this.clients)
		{
			for (ServerClient client : this.clients)
				users += client.name + "\u0000";
			
			ByteBuffer dataId = ByteBuffer.allocate(this.clients.size() * Long.BYTES + Integer.BYTES);
			
			dataId.putInt(dataId.capacity() / Long.BYTES);
			
			for (ServerClient client : this.clients)
				dataId.putLong(client.id);
			
			byte[] data = new byte[dataId.capacity() + users.getBytes().length];
			
			System.arraycopy(dataId.array(), 0, data, 0, dataId.capacity());
			System.arraycopy(users.getBytes(), 0, data, dataId.capacity(), users.getBytes().length);
			return data;
		}
	}
	
//	private byte[] queryClientNames()
//	{	
//		String users = new String();
//		
//		synchronized (this.clients)
//		{
//			for (ServerClient client : this.clients)
//				users += client.name + "\u0000";		
//		}
//		return users.getBytes();
//	}

	protected Protocol getProtocol()
	{
		if (this.connect.isValid())
		{
			if (this.connect instanceof UDPConnection)
			{
				return Protocol.UDP;
			}
			else if (this.connect instanceof TCPConnection)
			{
				return Protocol.TCP;
			}
		}
		return Protocol.UNKNOWN;
	}
	
	private void toggleRawMode()
	{
		boolean raw = !this.isRaw.get();
		System.out.println(raw ? "Raw mode on." : "Raw mode off.");	
		this.isRaw.set(raw);
	}

	private void printHelp()
	{
		System.out.println(
				"Here is a list of all available commands:\n" +
				"=========================================\n" +
				"$raw - Enables raw mode.\n" +
				"$queryclients - Shows all connected clients.\n" +
				"$kick [id | username] - Kicks a client.\n" + 
				"$help - Shows this help dialog.\n" + 
				"$stop - Shuts down the server.\n");
	}
}
