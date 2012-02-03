/**
 * 
 */
package org.moreunit.util;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.jdt.core.IType;

public final class TypeComparator implements Comparator<IType>, Serializable
{
    private static final long serialVersionUID = 1824050814132275831L;

    public int compare(IType first, IType second)
    {
        return first.getFullyQualifiedName().compareTo(second.getFullyQualifiedName());
    }
}