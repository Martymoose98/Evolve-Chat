package com.core.shared.serialization.arrays;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Array;
import com.core.shared.serialization.PrimitiveType;

public class IntArray extends Array
{
	public int[] iData;
	
	public IntArray(String name, int[] data)
	{
		super(name, PrimitiveType.INT);
		this.count = data.length;
		this.iData = data;
	}
	
	@Override
	public int getBytes(byte[] dst, int pointer)
	{
		pointer = super.getBytes(dst, pointer);
		pointer = writeBytes(dst, pointer, iData);
		return pointer;	
	}
	
	@Override
	public int getDataSize()
	{
		return this.iData.length * PrimitiveType.INT.size();
	}
}