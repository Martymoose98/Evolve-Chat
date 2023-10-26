package com.core.shared.serialization;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

public class Field extends Container
{	
	public byte primitiveType;
	public byte[] data;
	
	protected Field(String name, PrimitiveType type)
	{
		super(name, Type.FIELD);
		this.primitiveType = type.value;
		this.data = new byte[type.size()];
	}

	public void setName(String name)
	{
		this.nameLength = name.length();
		this.name = name.getBytes();
	}
	
	public int getBytes(byte[] dst)
	{
		return getBytes(dst, 0);
	}

	public int getBytes(byte[] dst, int pointer)
	{
		pointer = writeBytes(dst, pointer, type);
		pointer = writeBytes(dst, pointer, nameLength);
		pointer = writeBytes(dst, pointer, name);
		pointer = writeBytes(dst, pointer, primitiveType);
		pointer = writeBytes(dst, pointer, data);
		return pointer;
	}
	
	@Override
	public int size() 
	{
		return 2 * Byte.BYTES + Integer.BYTES + name.length + data.length;
	}
}
