package com.core.shared.serialization.fields;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Field;
import com.core.shared.serialization.PrimitiveType;

public class BooleanField extends Field
{
	protected BooleanField(String name, boolean value)
	{
		super(name, PrimitiveType.BOOLEAN);
		writeBytes(this.data, 0, value);
	}
}