package com.core.server;

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
import com.core.shared.HeartbeatPacket;
import com.core.shared.TCPConnection;
import com.core.shared.UDPConnection;
import com.core.shared.UnknownConnection;
import com.core.shared.UnknownPacket;
import com.core.shared.UserListPacket;

public class Server implements Runnable
{
	private List<ServerClient> clients;
	private UnknownConnection connect;
	private Thread server;
	private Thread manage;
	private Thread send;
	private Thread receive;
	private Scanner scanner;
	private AtomicBoolean isRunning;
	private AtomicBoolean isRaw;

	public Server(int port, boolean useUDP)
	{
		this.scanner = new Scanner(System.in);
		this.clients = Collections.synchronizedList(new ArrayList<ServerClient>());
		this.isRaw = new AtomicBoolean(false);
		this.isRunning = new AtomicBoolean(false);
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
		this.receive();

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
				this.send(new DisconnectPacket(client.address, client.port, client.id, DisconnectPacket.REASON_SERVER_REQUESTED));
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
							send(new UserListPacket(client.address, client.port, names));
							client.ping(connect);
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

	private void receive()
	{
		this.receive = new Thread("Inbound Packets")
		{
			@Override
			public void run()
			{
				while (isRunning.get())
				{
					process(connect.recv());
				}
			}
		};
		this.receive.start();
	}

	private void process(UnknownPacket packet)
	{
		if (packet == null)
			return;

		if (this.isRaw.get())
			System.out.println(packet);

		switch (packet.getType())
		{
		case UnknownPacket.CONNECT:
			this.connect(new ConnectPacket(packet));
			break;
		case UnknownPacket.DISCONNECT:
			this.disconnect(new DisconnectPacket(packet));
			break;
		case UnknownPacket.MESSAGE_ALL:
			this.broadcast(new BroadcastPacket(packet));
			break;
		case UnknownPacket.QUERY_RESPONSE:
			this.markClientActive(new HeartbeatPacket(packet).getId());
			break;
		}
	}

	private void connect(ConnectPacket packet)
	{
		ServerClient client = new ServerClient(packet.getName(), packet.getAddress(), packet.getPort(), UniqueIdentifier.getIndentifier());
		this.clients.add(client);
		this.send(new ConnectResponsePacket(client.address, client.port, client.id));
		System.out.println(client.name + " connected from " + client.address.getHostAddress() + ":" + client.port + " Id: " + client.id);
	}

	private void disconnect(DisconnectPacket packet)
	{
		ServerClient client = this.getClientById(packet.getId());
		this.disconnect(client, packet);
	}

	private void disconnect(ServerClient client, DisconnectPacket packet)
	{
		if (client == null)
			return;

		this.clients.remove(client);
		this.send(packet);
		String sReason = (packet.getReason() == DisconnectPacket.REASON_TIMEOUT) ? " timed out from " : " disconnected from ";
		System.out.println(client.name + sReason + client.address.getHostAddress() + ":" + client.port + " Id: " + client.id);
	}

	private void broadcast(BroadcastPacket packet)
	{
		ServerClient sender = this.getClientById(packet.getId());

		if (sender == null)
			return;

		synchronized (this.clients)
		{
			for (ServerClient client : this.clients)
			{
				this.send(new BroadcastPacket(client.address, client.port, client.id, packet.getMessage()));
			}
		}
	}

	private void send(final UnknownPacket packet)
	{
		this.send = new Thread("Outbound Packets")
		{
			@Override
			public void run()
			{
				connect.send(packet);
			}
		};
		this.send.start();
	}

	private void markClientActive(long id)
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
		}
		return users.getBytes();
	}

	private void toggleRawMode()
	{
		boolean raw = !this.isRaw.get();

		if (raw)
			System.out.println("Raw mode on.");
		else
			System.out.println("Raw mode off.");

		this.isRaw.set(raw);
	}

	private void printHelp()
	{
		System.out.println("Here is a list of all available commands:");
		System.out.println("=========================================");
		System.out.println("$raw - Enables raw mode.");
		System.out.println("$queryclients - Shows all connected clients.");
		System.out.println("$kick [id | username] - Kicks a client.");
		System.out.println("$help - Shows this help dialog.");
		System.out.println("$stop - Shuts down the server.");
	}
}
