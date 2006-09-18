/**
 * 
 */
package org.moreunit.util;

import java.util.Comparator;

import org.eclipse.jdt.core.IType;

public final class TypeComparator implements Comparator<IType> {
	public int compare(IType first, IType second) {
		return first.getFullyQualifiedName().compareTo(second.getFullyQualifiedName());
	}
}

//$Log: not supported by cvs2svn $
//Revision 1.1  2006/09/18 19:56:03  channingwalton
//Fixed bug [ 1537839 ] moreunit cannot find test class if it is in wrong package. Also found a classcast exception in UnitDecorator whicj I've guarded for.Fixed the Class wizard icon
//
//