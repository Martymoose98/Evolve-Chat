package com.core.shared.serialization.fields;

import static com.core.shared.serialization.SerializationWriter.writeBytes;

import com.core.shared.serialization.Field;
import com.core.shared.serialization.PrimitiveType;

public class ByteField extends Field
{
	protected ByteField(String name, byte value)
	{
		super(name, PrimitiveType.BYTE);
		writeBytes(this.data, 0, value);
	}
}
