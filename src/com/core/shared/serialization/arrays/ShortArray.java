package com.core.shared.serialization.arrays;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Array;
import com.core.shared.serialization.PrimitiveType;

public class ShortArray extends Array
{
	public short[] sData;
	
	public ShortArray(String name, short[] data)
	{
		super(name, PrimitiveType.SHORT);
		this.count = data.length;
		this.sData = data;
	}
	
	@Override
	public int getBytes(byte[] dst, int pointer)
	{
		pointer = super.getBytes(dst, pointer);
		pointer = writeBytes(dst, pointer, sData);
		return pointer;	
	}

	@Override
	public int getDataSize()
	{
		return this.sData.length * PrimitiveType.SHORT.size();
	}
}