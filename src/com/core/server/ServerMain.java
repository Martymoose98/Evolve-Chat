package com.core.server;

import java.util.Random;

import com.core.shared.serialization.Array;
import com.core.shared.serialization.arrays.IntArray;

public class ServerMain
{
	private int port;
	private boolean useUDP;
	private Server server;

	public ServerMain(int port, boolean useUDP)
	{
		this.port = port;
		this.useUDP = useUDP;
		this.server = new Server(port, useUDP);
	}

	private static void printBytes(byte[] b)
	{
		for (byte it : b)
			System.err.printf("0x%02X ", it);
		
		System.err.println();
	}
	
	public static void main(String[] args)
	{
		boolean useUDP;
		int port;
			
//		Field field = new IntField("jew", 599);
//		byte[] buf = new byte[field.size()];
//		field.getBytes(buf);
//		Array<Byte> farray = new Array<Byte>("fuck", Byte.class, buf);
//		Random rand = new Random();
//		int[] data = new int[50000];
//		
//		for (int i = 0; i < data.length; ++i)
//			data[i] = rand.nextInt();
//		
//		int[] elements = new int[] { 1, 2, 3, 4, 5 };
//		Array arr = new IntArray("test", elements);
//		
//		byte[] stream = new byte[arr.size()];
//		arr.getBytes(stream);
//		printBytes(stream);
		
		if (args.length > 2)
		{
			System.err.println("Usage: java -jar server.jar [port] [useUDP true:false]");
			return;
		}
		
		if (args.length == 0)
		{
			port = 666;
			useUDP = true;
		}
		else
		{
			port = Integer.parseInt(args[0]);
			useUDP = Boolean.parseBoolean(args[1]);
		}
		new ServerMain(port, useUDP);
	}
}
