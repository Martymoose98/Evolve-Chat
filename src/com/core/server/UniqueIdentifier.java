/*
 * Decompiled with CFR 0.151.
 */
package com.core.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UniqueIdentifier {
    public static final long INVALID_ID = -1L;
    private static List<Long> ids = new ArrayList<Long>();
    private static final int RANGE = 10000;
    private static int index = 0;

    static {
        long i = 0L;
        while (i < 10000L) {
            ids.add(i);
            ++i;
        }
        Collections.shuffle(ids);
    }

    private UniqueIdentifier() {
    }

    public static long getIndentifier() {
        if (index > ids.size() - 1) {
            index = 0;
        }
        return ids.get(index++);
    }
}
