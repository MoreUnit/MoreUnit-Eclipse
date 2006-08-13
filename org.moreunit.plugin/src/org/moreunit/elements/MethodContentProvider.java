package org.moreunit.elements;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * This content provider is used to show a list dialog of corresponding
 * testmethods if a use renames a method.
 * 
 * @author vera
 * 29.03.2006 21:35:05
 */
public class MethodContentProvider implements IStructuredContentProvider {
	
	List methods;
	
	public MethodContentProvider(List methods) {
		this.methods = methods;
	}

	public Object[] getElements(Object inputElement) {
		return methods.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.1  2006/06/22 20:22:29  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.2  2006/05/12 17:52:38  gianasista
// added comments
//
// Revision 1.1  2006/04/14 17:14:22  gianasista
// Refactoring Support with dialog
//