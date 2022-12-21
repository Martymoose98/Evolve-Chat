package com.core.shared.serialization;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.TypeVariable;
import java.util.function.IntFunction;
import java.util.function.Supplier;

class Facto<E>
{
	private Supplier<E> supplier;

	Facto(Supplier<E> supplier)
	{
		this.supplier = supplier;
	}

	E create()
	{
		return supplier.get();
	}
}

public class ArrayT<T> extends Container
{
	static class Factory<T>
	{

		static <T, R extends T> T[] genericArray(int size, IntFunction<T[]> arrayCreator)
		{
			return (T[]) arrayCreator.apply(size);
		}

		T[] create(Class<T> clazz, int capacity)
		{
			T[] ret = (T[]) java.lang.reflect.Array.newInstance(clazz, capacity);

			for (int i = 0; i < capacity; ++i)
				ret[i] = create(clazz);

			return ret;
		}

		T create(Class<T> clazz)
		{
			Constructor<?>[] con = clazz.getDeclaredConstructors();
			TypeVariable<Class<T>>[] t = clazz.getTypeParameters();
			Constructor<?> c = clazz.getEnclosingConstructor();
			java.lang.reflect.Type ty = clazz.getGenericSuperclass();
			Class<? extends java.lang.reflect.Type> superclazz = ty.getClass();
			try
			{
				superclazz.getDeclaredConstructor().newInstance();
				
				return (T) clazz.getDeclaredConstructor().newInstance();
			}
			catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | NoSuchMethodException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	// public static final byte CONTAINER_TYPE = Container.Type.ARRAY.value;
	// public int nameLength;
	// public byte[] name;
	public byte type; // primitive type
	public int count;
	public byte[] data;

	public ArrayT(String name, Class<T> cl, byte[] buf)
	{
		super(name, Type.ARRAY);

		this.type = init(buf.getClass().getComponentType()).value;
		T[] Fun = (T[]) new Factory<T>().genericArray(9, Byte[]::new);
		Byte s = buf[0];
		T d = new Factory<T>().create((Class<T>) byte.class);
		T[] c = new Factory<T>().create((Class<T>) cl, buf.length);
		java.lang.reflect.Array.setByte(c, 0, (byte) 0x69);
		System.out.println(c[0]);
		// T[] s = new Facto<T[]>(Byte[]::new).create();
	}

	private PrimitiveType init(Class<?> cl)
	{
		if (cl.isInstance(Byte.class))
		{
			return PrimitiveType.BYTE;
		}
		else if (cl.isInstance(Boolean.class))
		{
			return PrimitiveType.BOOLEAN;
		}
		else if (cl.isInstance(Short.class))
		{
			return PrimitiveType.BOOLEAN;
		}
		else if (cl.isInstance(Character.class))
		{
			return PrimitiveType.CHAR;
		}
		else if (cl.isInstance(Integer.class))
		{
			return PrimitiveType.INT;
		}
		else if (cl.isInstance(Long.class))
		{
			return PrimitiveType.LONG;
		}
		else if (cl.isInstance(Float.class))
		{
			return PrimitiveType.FLOAT;
		}
		else if (cl.isInstance(Double.class))
		{
			return PrimitiveType.DOUBLE;
		}
		return PrimitiveType.UNKNOWN;
	}

	@Override
	public int getBytes(byte[] dst, int pointer)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int size()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
