package com.core.shared.serialization;

public enum PrimitiveType
{
	UNKNOWN((byte) 0), 
	BYTE((byte) 1), 
	SHORT((byte) 2), 
	CHAR((byte) 3), 
	INT((byte) 4), 
	LONG((byte) 5), 
	FLOAT((byte) 6), 
	DOUBLE((byte) 7), 
	BOOLEAN((byte) 8);
 
	protected final byte value;

	PrimitiveType(byte type) { this.value = type; }
	
	public int size()
	{
		switch (this)
		{
		default:
		case UNKNOWN:
			System.err.println("Unknown type size!");
			break;
		case BOOLEAN:
		case BYTE:
			return Byte.BYTES;
		case SHORT:
			return Short.BYTES;
		case CHAR:
			return Character.BYTES;
		case INT:
			return Integer.BYTES;
		case LONG:
			return Long.BYTES;
		case FLOAT:
			return Float.BYTES;
		case DOUBLE:
			return Double.BYTES;
		}
		return 0;
	}
}
