package com.core.server;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import com.core.shared.DisconnectPacket;
import com.core.shared.HeartbeatPacket;
import com.core.shared.PacketDispatcher;
import com.core.shared.UnknownPacket;

public class ServerClient
{
	public static final int MAX_HEARTBEAT_ATTEMPTS = 5;
	public String name;
	public InetAddress address;
	public int port;
	public long id;
	private AtomicInteger attempt;

	public ServerClient(String name, InetAddress address, int port, long id)
	{
		this.name = name;
		this.address = address;
		this.port = port;
		this.id = id;
		this.attempt = new AtomicInteger(0);
	}

	public boolean didTimeout()
	{
		return this.attempt.getAndIncrement() >= MAX_HEARTBEAT_ATTEMPTS;
	}

	public void ping(PacketDispatcher dispatcher)
	{
		UnknownPacket packet = this.didTimeout() ? 
				new DisconnectPacket(dispatcher.getConnection(), this.id, DisconnectPacket.REASON_TIMEOUT) 
				: new HeartbeatPacket(this.address, this.port, this.id);
		
		dispatcher.send(packet);
	}

	public void markActive()
	{
		this.attempt.set(0);
	}

	public String toString()
	{
		return this.name + " IPv4: " + this.address.getHostAddress() + ":" + this.port + " Id: " + this.id;
	}
	
	public byte[] toBytes()
	{
		ByteBuffer buffer = ByteBuffer.allocate(this.name.length() * Character.BYTES + Integer.BYTES + Long.BYTES);	
		buffer.put(this.name.getBytes());
		buffer.putInt(0);
		buffer.putLong(this.id);
		return buffer.array();
	}
}
