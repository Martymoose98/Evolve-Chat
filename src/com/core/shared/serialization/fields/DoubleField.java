package com.core.shared.serialization.fields;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Field;
import com.core.shared.serialization.PrimitiveType;

public class DoubleField extends Field
{
	protected DoubleField(String name, double value)
	{
		super(name, PrimitiveType.DOUBLE);
		writeBytes(this.data, 0, value);
	}
}