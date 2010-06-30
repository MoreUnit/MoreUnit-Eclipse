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

// $Log: not supported by cvs2svn $
// Revision 1.3  2009/04/05 19:14:27  gianasista
// code formatter
//
// Revision 1.2 2006/09/18 20:00:06 channingwalton
// the CVS substitions broke with my last check in because I put newlines in
// them
//
// Revision 1.1 2006/09/18 19:56:03 channingwalton
// Fixed bug [ 1537839 ] moreunit cannot find test class if it is in wrong
// package. Also found a classcast exception in UnitDecorator whicj I've guarded
// for.Fixed the Class wizard icon
//
//
