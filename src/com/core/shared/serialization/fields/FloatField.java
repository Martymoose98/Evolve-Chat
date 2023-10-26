package com.core.shared.serialization.fields;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Field;
import com.core.shared.serialization.PrimitiveType;

public class FloatField extends Field
{
	protected FloatField(String name, float value)
	{
		super(name, PrimitiveType.FLOAT);
		writeBytes(this.data, 0, value);
	}
}