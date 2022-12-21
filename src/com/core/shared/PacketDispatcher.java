package com.core.shared;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  
 *  @author Martin
 */
public class PacketDispatcher extends PacketSink
{
	private ConcurrentLinkedQueue<UnknownPacket> queue;
	private Semaphore semaphore;
	
	/**
	 * Instantiates a new packet dispatcher.
	 *
	 * @param connect the connect
	 * @param name the name
	 */
	protected PacketDispatcher(UnknownConnection connect, String name)
	{
		this(connect, new AtomicBoolean(true), name);
	}

	/**
	 * Instantiates a new packet dispatcher.
	 *
	 * @param connect - the connection to attach to and dispatch packets on
	 * @param isRunning - the is dispatcher running
	 * @param name - the name of the dispatcher
	 */
	public PacketDispatcher(UnknownConnection connect, AtomicBoolean isRunning, String name)
	{
		super(connect, isRunning, name);
		this.queue = new ConcurrentLinkedQueue<UnknownPacket>();
		this.semaphore = new Semaphore(1, true);
		this.start();
	}

	@Override
	public void run()
	{
		while (this.isRunning.get())
		{
			process(this.queue.poll()); 
			
			try
			{
				this.semaphore.acquire();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

	@Override
	protected void process(UnknownPacket packet)
	{
		if (packet == null)
			return;
		
		this.connect.send(packet);
	}
	
	/**
	 *  Add the packet to the queue and release the semaphore.
	 * 
	 * @param packet - the packet to be sent
	 */
	public void send(UnknownPacket packet)
	{
		this.queue.add(packet);
		this.semaphore.release();
	}
	
	
	/**
	 * Gets the actively attached connection.
	 *
	 * @return the connection
	 */
	public UnknownConnection getConnection()
	{
		return this.connect;
	}
}
