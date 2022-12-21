package com.core.shared.serialization;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

public abstract class Array extends Container
{
	public byte primitiveType; // primitive type
	public int count;	// part of Container rn makes slim sense
	public byte[] data;

	public Array(String name, PrimitiveType type)
	{
		super(name, Type.ARRAY);
		this.primitiveType = type.value;
	}

	@Override
	public int getBytes(byte[] dst, int pointer)
	{
		pointer = writeBytes(dst, pointer, type);
		pointer = writeBytes(dst, pointer, nameLength);
		pointer = writeBytes(dst, pointer, name);
		pointer = writeBytes(dst, pointer, primitiveType);
		pointer = writeBytes(dst, pointer, count);
		
		return pointer;
	}
	

	@Override
	public int size()
	{
		// sizeof(byte) + sizeof(int)
		return Byte.BYTES + Integer.BYTES + this.size + this.getDataSize();
	}

	public abstract int getDataSize();
}
