package org.moreunit.util;

import java.util.Collection;

import org.eclipse.jdt.core.IJavaElement;

public class JavaElementUtils
{
    public static IJavaElement[] toArray(Collection< ? extends IJavaElement> elements)
    {
        return elements.stream().toArray(IJavaElement[]::new);
    }
}
