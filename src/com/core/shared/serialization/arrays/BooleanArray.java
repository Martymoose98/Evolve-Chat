package com.core.shared.serialization.arrays;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Array;
import com.core.shared.serialization.PrimitiveType;

public class BooleanArray extends Array
{
	public boolean[] bData;
	
	public BooleanArray(String name, boolean[] data)
	{
		super(name, PrimitiveType.BOOLEAN);
		this.count = data.length;
		this.bData = data;
	}
	
	@Override
	public int getBytes(byte[] dst, int pointer)
	{
		pointer = super.getBytes(dst, pointer);
		pointer = writeBytes(dst, pointer, bData);
		return pointer;	
	}

	@Override
	public int getDataSize()
	{
		return this.bData.length * PrimitiveType.BOOLEAN.size();
	}
}