package com.core.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UniqueIdentifier
{
	public static final long INVALID_ID = -1L;
	private static List<Long> ids = new ArrayList<Long>();
	private static final long RANGE = 10000L;
	private static int index = 0;

	static
	{
		for (long i = 0; i < RANGE; ++i)
			ids.add(i);
		
		Collections.shuffle(ids);
	}

	private UniqueIdentifier()
	{
	}

	public static long getIndentifier()
	{
		if (index > ids.size() - 1)
		{
			index = 0;
		}
		return ids.get(index++);
	}
}
