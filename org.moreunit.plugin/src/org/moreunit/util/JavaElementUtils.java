package org.moreunit.util;

import java.util.Collection;

import org.eclipse.jdt.core.IJavaElement;

public class JavaElementUtils
{
    public static IJavaElement[] toArray(Collection< ? extends IJavaElement> elements)
    {
        IJavaElement[] result = new IJavaElement[elements.size()];
        int i = 0;
        for (IJavaElement element : elements)
        {
            result[i++] = element;
        }
        return result;
    }
}
