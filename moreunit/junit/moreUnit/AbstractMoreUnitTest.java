package moreUnit;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public abstract class AbstractMoreUnitTest extends MockObjectTestCase {

	protected Mock	type	= mock(IType.class);

	protected void stubTypeQualifiedName(String name) {
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
}

// $Log$