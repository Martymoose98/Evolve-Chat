package com.core.shared;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * The listener interface for receiving packet events.
 * The class that is interested in processing a packet
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addPacketListener<code> method. When
 * the packet event occurs, that object's appropriate
 * method is invoked.
 *
 * @author Martin
 */
public abstract class PacketListener extends PacketSink
{
	/**
	 * Instantiates a new packet listener.
	 *
	 * @param connect - the connection to listen for packet on
	 * @param running - running
	 * @param name - name of the listener
	 */
	protected PacketListener(UnknownConnection connect, AtomicBoolean running, String name)
	{
		super(connect, running, name);
		this.start();
	}
	
	/**
	 * Run method inherited from <code>PacketSink</code>
	 */
	@Override
	public void run()
	{
		while (this.isRunning.get())
			receive();
	}
	
	/**
	 * Receive the packet then call the process
	 * functions. This function is blocking.
	 * 
	 */
	private void receive()
	{
		UnknownPacket packet = this.connect.recv();

		if (packet == null)
			return;
		
		prePacketProcessed(packet);
		process(packet);
		postPacketProcessed(packet);
	}

	
	/**
	 *  Process.
	 *
	 * @param packet - the packet to be processed
	 */
	protected abstract void process(UnknownPacket packet);
	
	/**
	 * Invoked before the packet is processed.
	 * 
	 * @param packet - the packet to be processed
	 */
	public abstract void prePacketProcessed(UnknownPacket packet);
	
	/**
	 * Invoked after the packet is processed.
	 *
	 * @param packet - the packet that was processed
	 */
	public abstract void postPacketProcessed(UnknownPacket packet);
}
