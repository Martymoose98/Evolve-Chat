package com.core.shared.serialization.arrays;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Array;
import com.core.shared.serialization.PrimitiveType;

public class CharArray extends Array
{
	public char[] charData;
	
	public CharArray(String name, char[] data)
	{
		super(name, PrimitiveType.CHAR);
		this.count = data.length;
		this.charData = data;
	}

	@Override
	public int getBytes(byte[] dst, int pointer)
	{
		pointer = super.getBytes(dst, pointer);
		pointer = writeBytes(dst, pointer, charData);
		return pointer;	
	}
	
	@Override
	public int getDataSize()
	{
		return this.charData.length * PrimitiveType.CHAR.size();
	}
}
