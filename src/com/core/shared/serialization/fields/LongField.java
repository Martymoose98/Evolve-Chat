package com.core.shared.serialization.fields;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Field;
import com.core.shared.serialization.PrimitiveType;

public class LongField extends Field
{
	protected LongField(String name, long value)
	{
		super(name, PrimitiveType.LONG);
		writeBytes(this.data, 0, value);
	}
}