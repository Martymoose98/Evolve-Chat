package com.core.shared.serialization.fields;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Field;
import com.core.shared.serialization.PrimitiveType;

public class CharField extends Field
{
	protected CharField(String name, char value)
	{
		super(name, PrimitiveType.CHAR);
		writeBytes(this.data, 0, value);
	}
}