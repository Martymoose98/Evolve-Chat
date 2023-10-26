package com.core.shared.serialization.fields;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Field;
import com.core.shared.serialization.PrimitiveType;

public class ShortField extends Field
{
	protected ShortField(String name, short value)
	{
		super(name, PrimitiveType.SHORT);
		writeBytes(this.data, 0, value);
	}
}