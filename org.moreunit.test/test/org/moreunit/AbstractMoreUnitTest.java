package org.moreunit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public abstract class AbstractMoreUnitTest extends MockObjectTestCase{

	protected void stubTypeQualifiedName(Mock type, String name) {
		type.stubs().method("getTypeQualifiedName").will(returnValue(name));
	}

	protected void setUpType(Mock type, String elementName, Mock compilationUnit, Mock packageFragment) {
		type.stubs().method("getElementName").will(returnValue(elementName));
		type.stubs().method("getParent").will(returnValue(compilationUnit.proxy()));
	
		compilationUnit.stubs().method("getElementType").will(returnValue(IJavaElement.COMPILATION_UNIT));
		compilationUnit.stubs().method("getType").with(eq(elementName)).will(returnValue(type.proxy()));
		compilationUnit.stubs().method("getParent").will(returnValue(packageFragment.proxy()));
	
		packageFragment.stubs().method("getElementType").will(returnValue(IJavaElement.PACKAGE_FRAGMENT));
		packageFragment.stubs().method("getCompilationUnit").with(eq(elementName + ".java")).will(returnValue(compilationUnit.proxy()));
	}

	protected void setUpMethod(Mock mockMethod, String name, Mock declaringType) {
		mockMethod.stubs().method("getElementName").will(returnValue(name));
		mockMethod.stubs().method("getDeclaringType").will(returnValue(declaringType.proxy()));
	}

	protected void dispose(TestProject testProject) throws CoreException {
		if (testProject != null) {
			testProject.dispose();
		}
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.3  2007/01/10 20:25:04  gianasista
// Added Filters to MissingTestMethods-View
//
// Revision 1.2  2006/10/02 18:22:24  channingwalton
// added actions for jumping from views. added some tests for project properties. improved some of the text
//
// Revision 1.1.1.1  2006/08/13 14:30:55  gianasista
// initial
//
// Revision 1.1  2006/06/22 20:21:44  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:11:29  gianasista
// CVS Refactoring
//
// Revision 1.2  2006/06/01 21:00:49  channingwalton
// made rename methods support undo, it would be nice to figure out how to show a preview too...
//
// Revision 1.1  2006/05/17 19:16:00  channingwalton
// enhanced rename refactoring to support undo and so that it is included in the preview with other changes.
//