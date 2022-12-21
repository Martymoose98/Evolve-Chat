package com.core.shared.serialization;

public abstract class Container
{
	enum Type
	{
		UNKNOWN((byte) 0), FIELD((byte) 1), ARRAY((byte) 2), OBJECT((byte) 3);

		protected final byte value;
		
		Type(byte type) { this.value = type; }
	}

	public int nameLength;	// 0
	public byte[] name;		// 4
	public byte type; 		// 8 container type
	//public int count;		// C

	protected int size;		// 10
	
	public Container(String name, Type type)
	{
		this.type = type.value;
		this.size = Integer.BYTES + Byte.BYTES; // sizeof(int) + sizeof(byte)
		setName(name);		
	}
	
	public void setName(String name)
	{		
		if (this.name != null)
			this.size -= this.nameLength;
		
		this.nameLength = name.length();
		this.name = name.getBytes();
		this.size += this.nameLength;
	}

	public int getBytes(byte[] dst)
	{
		return this.getBytes(dst, 0);
	}
	
	public abstract int getBytes(byte[] dst, int pointer);
	
	public abstract int size();
}
