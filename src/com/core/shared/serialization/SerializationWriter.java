package com.core.shared.serialization;

/**
 * 
 * Writes and reads in Big Endian
 * 
 * @author Martin
 *
 */
public class SerializationWriter
{
	/***
	 *  Magic number
	 */
	public static final byte[] HEADER = "EC".getBytes();
	
	/**
	 *  Version number (Big Endian)
	 *  
	 *  Low Byte = Major Version
	 *  High Byte = Minor Version
	 *  
	 */
	public static final short VERSION = 0x0100;
	
	public SerializationWriter() { }
	
	public static int writeBytes(byte[] dst, int pointer, byte value)
	{
		assert(dst.length > pointer + Byte.BYTES);
		dst[pointer++] = value;
		return pointer;
	}
	
	public static int writeBytes(byte[] dst, int pointer, char value)
	{
		assert(dst.length > pointer + Character.BYTES);
		dst[pointer++] = (byte) ((value >> 8) & 0xFF);
		dst[pointer++] = (byte) (value & 0xFF);
		return pointer;
	}
	
	public static int writeBytes(byte[] dst, int pointer, short value)
	{
		assert(dst.length > pointer + Short.BYTES);
		dst[pointer++] = (byte) ((value >> 8) & 0xFF);
		dst[pointer++] = (byte) (value & 0xFF);
		return pointer;
	}
	
	public static int writeBytes(byte[] dst, int pointer, int value)
	{
		assert(dst.length > pointer + Integer.BYTES);
		dst[pointer++] = (byte) ((value >> 24) & 0xFF);
		dst[pointer++] = (byte) ((value >> 16) & 0xFF);
		dst[pointer++] = (byte) ((value >> 8) & 0xFF);
		dst[pointer++] = (byte) (value & 0xFF);
		return pointer;
	}
	
	public static int writeBytes(byte[] dst, int pointer, long value)
	{
		assert(dst.length > pointer + Long.BYTES);
		dst[pointer++] = (byte) ((value >> 56) & 0xFF);
		dst[pointer++] = (byte) ((value >> 48) & 0xFF);
		dst[pointer++] = (byte) ((value >> 40) & 0xFF);
		dst[pointer++] = (byte) ((value >> 32) & 0xFF);
		dst[pointer++] = (byte) ((value >> 24) & 0xFF);
		dst[pointer++] = (byte) ((value >> 16) & 0xFF);
		dst[pointer++] = (byte) ((value >> 8) & 0xFF);
		dst[pointer++] = (byte) (value & 0xFF);
		return pointer;
	}
	
	public static int writeBytes(byte[] dst, int pointer, float value)
	{
		return writeBytes(dst, pointer, Float.floatToRawIntBits(value));
	}
	
	public static int writeBytes(byte[] dst, int pointer, double value)
	{
		return writeBytes(dst, pointer, Double.doubleToRawLongBits(value));
	}
	
	public static int writeBytes(byte[] dst, int pointer, boolean value)
	{	
		assert(dst.length > pointer + Byte.BYTES);
		dst[pointer++] = (byte)(value ? 1 : 0);
		return pointer;
	}
	
	public static int writeBytes(byte[] dst, int pointer, byte[] src)
	{
		assert(dst.length > pointer + src.length);
		
		for (byte it : src)
			dst[pointer++] = it;
		
		return pointer;
	}
	
	public static int writeBytes(byte[] dst, int pointer, short[] src)
	{
		assert(dst.length > pointer + Short.BYTES * src.length);
		
		for (short it : src)
			pointer = writeBytes(dst, pointer, it);
		
		return pointer;
	}
	
	public static int writeBytes(byte[] dst, int pointer, char[] src)
	{
		assert(dst.length > pointer + Character.BYTES * src.length);
		
		for (char it : src)
			pointer = writeBytes(dst, pointer, it);
		
		return pointer;
	}
	
	public static int writeBytes(byte[] dst, int pointer, int[] src)
	{
		assert(dst.length > pointer + Integer.BYTES * src.length);
		
		for (int it : src)
			pointer = writeBytes(dst, pointer, it);
		
		return pointer;
	}
	
	public static int writeBytes(byte[] dst, int pointer, long[] src)
	{
		assert(dst.length > pointer + Long.BYTES * src.length);
		
		for (long it : src)
			pointer = writeBytes(dst, pointer, it);
		
		return pointer;
	}
	
	public static int writeBytes(byte[] dst, int pointer, float[] src)
	{
		assert(dst.length > pointer + Float.BYTES * src.length);
		
		for (float it : src)
			pointer = writeBytes(dst, pointer, it);
		
		return pointer;
	}
	
	public static int writeBytes(byte[] dst, int pointer, double[] src)
	{
		assert(dst.length > pointer + Double.BYTES * src.length);
		
		for (double it : src)
			pointer = writeBytes(dst, pointer, it);
		
		return pointer;
	}

	public static int writeBytes(byte[] dst, int pointer, boolean[] src)
	{
		assert(dst.length > pointer + src.length);
		
		for (boolean it : src)
			pointer = writeBytes(dst, pointer, it);
		
		return pointer;
	}
	
	/**
	 * Write the string size before the string bytes
	 * @param dst
	 * @param pointer
	 * @param value
	 * @return
	 */
	public static int writeBytes(byte[] dst, int pointer, String value)
	{
		pointer = writeBytes(dst, pointer, value.length());
		return writeBytes(dst, pointer, value.getBytes());
	}
	
	public static byte readByte(byte[] src, int pointer)
	{
		return src[pointer];
	}

	public static short readShort(byte[] src, int pointer)
	{
		return (short) ((src[pointer] << 8) | src[pointer + 1]);
	}
	
	public static char readChar(byte[] src, int pointer)
	{
		return (char) ((src[pointer] << 8) | src[pointer + 1]);
	}
	
	public static int readInt(byte[] src, int pointer)
	{
		return (int) ((src[pointer] << 24) | (src[pointer + 1] << 16) | (src[pointer + 2] << 8) | src[pointer + 3]);
	}
	
	public static long readLong(byte[] src, int pointer)
	{
		return (long) ((src[pointer] << 56) | (src[pointer + 1] << 48) | (src[pointer + 2] << 40) | (src[pointer + 3] << 32) |
				(src[pointer + 4] << 24) | (src[pointer + 5] << 16) | (src[pointer + 6] << 8) | src[pointer + 7]);
	}
	
	public static float readFloat(byte[] src, int pointer)
	{
		return Float.intBitsToFloat(readInt(src, pointer));
	}
	
	public static double readDouble(byte[] src, int pointer)
	{
		return Double.longBitsToDouble(readLong(src, pointer));
	}
	
	public static boolean readBoolean(byte[] src, int pointer)
	{
		assert(src[pointer] == 0 || src[pointer] == 1);
		return src[pointer] != 0;
	}

}
