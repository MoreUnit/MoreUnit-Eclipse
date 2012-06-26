package org.moreunit.core.util;

import java.util.Arrays;

public final class Objects
{
    public static boolean equal(Object o1, Object o2)
    {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }

    public static int hash(Object... objects)
    {
        return Arrays.hashCode(objects);
    }
}
