package com.core.shared.serialization.arrays;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Array;
import com.core.shared.serialization.PrimitiveType;

public class DoubleArray extends Array
{
	public double[] dblData;
	
	public DoubleArray(String name, double[] data)
	{
		super(name, PrimitiveType.DOUBLE);
		this.count = data.length;
		this.dblData = data;
	}
	
	@Override
	public int getBytes(byte[] dst, int pointer)
	{
		pointer = super.getBytes(dst, pointer);
		pointer = writeBytes(dst, pointer, dblData);
		return pointer;	
	}
	
	@Override
	public int getDataSize()
	{
		return this.dblData.length * PrimitiveType.DOUBLE.size();
	}
}