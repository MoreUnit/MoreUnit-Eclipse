package moreUnit.util;

import java.util.Comparator;

import org.eclipse.jdt.core.IType;

public class ITypeNameComparator implements Comparator {

	public int compare(Object firstObject, Object secondObject) {
		IType first = (IType) firstObject;
		IType second = (IType) secondObject;
		return first.getFullyQualifiedName().compareTo(second.getFullyQualifiedName());
	}

}
