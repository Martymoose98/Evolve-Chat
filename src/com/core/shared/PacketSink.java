package com.core.shared;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * Generic packet sink for the server and client.
 * 
 * @author Martin
 *
 */
public abstract class PacketSink implements Runnable
{
	protected String name;
	protected AtomicBoolean isRunning;
	protected UnknownConnection connect;
	protected Thread worker;

	/**
	 * Instantiates a new packet sink.
	 *
	 * @param connect the connect
	 * @param running the running
	 * @param name the name
	 */
	protected PacketSink(UnknownConnection connect, AtomicBoolean running, String name) 
	{
		this.name = name;
		this.isRunning = running;
		this.connect = connect;
		this.worker = new Thread(this, this.name);
	
	}	
	
	/**
	 * Run function overridden from Runnable.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Process.
	 *
	 * @param packet - the packet to be processed.
	 */
	protected abstract void process(UnknownPacket packet);
	
	/**
	 * Starts the packet sink.
	 */
	public void start()
	{
		this.worker.start();
	}
}
