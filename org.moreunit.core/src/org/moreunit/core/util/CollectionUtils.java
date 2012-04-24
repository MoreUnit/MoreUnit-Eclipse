package org.moreunit.core.util;

import java.util.HashSet;
import java.util.Set;

public class CollectionUtils
{
    public static <T> Set<T> asSet(T... elements)
    {
        Set<T> set = new HashSet<T>(elements.length);
        for (int i = 0; i < elements.length; i++)
        {
            set.add(elements[i]);
        }
        return set;
    }
}
