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
//