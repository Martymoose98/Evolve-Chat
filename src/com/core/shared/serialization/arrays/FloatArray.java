package com.core.shared.serialization.arrays;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Array;
import com.core.shared.serialization.PrimitiveType;

public class FloatArray extends Array
{
	public float[] flData;
	
	public FloatArray(String name, float[] data)
	{
		super(name, PrimitiveType.FLOAT);
		this.count = data.length;
		this.flData = data;
	}
	
	@Override
	public int getBytes(byte[] dst, int pointer)
	{
		pointer = super.getBytes(dst, pointer);
		pointer = writeBytes(dst, pointer, flData);
		return pointer;	
	}
	
	@Override
	public int getDataSize()
	{
		return this.flData.length * PrimitiveType.FLOAT.size();
	}
}