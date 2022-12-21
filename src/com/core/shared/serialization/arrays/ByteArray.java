package com.core.shared.serialization.arrays;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Array;
import com.core.shared.serialization.PrimitiveType;

public class ByteArray extends Array
{
	public ByteArray(String name, byte[] data)
	{
		super(name, PrimitiveType.BYTE);
		this.count = data.length;
		this.data = data;
	}
	
	@Override
	public int getBytes(byte[] dst, int pointer)
	{
		pointer = super.getBytes(dst, pointer);
		pointer = writeBytes(dst, pointer, data);
		return pointer;	
	}
	
	@Override
	public int getDataSize()
	{
		return this.data.length * PrimitiveType.BYTE.size();
	}
}
