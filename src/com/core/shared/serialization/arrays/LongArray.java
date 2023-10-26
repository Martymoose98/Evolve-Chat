package com.core.shared.serialization.arrays;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Array;
import com.core.shared.serialization.PrimitiveType;

public class LongArray extends Array
{
	public long[] lData;
	
	public LongArray(String name, long[] data)
	{
		super(name, PrimitiveType.LONG);
		this.count = data.length;
		this.lData = data;
	}
	
	@Override
	public int getBytes(byte[] dst, int pointer)
	{
		pointer = super.getBytes(dst, pointer);
		pointer = writeBytes(dst, pointer, lData);
		return pointer;	
	}

	@Override
	public int getDataSize()
	{
		return this.lData.length * PrimitiveType.LONG.size();
	}
}