package com.core.shared.serialization.fields;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Field;
import com.core.shared.serialization.PrimitiveType;

public class IntField extends Field
{
	public IntField(String name, int value)
	{
		super(name, PrimitiveType.INT);
		writeBytes(this.data, 0, value);
	}
}
