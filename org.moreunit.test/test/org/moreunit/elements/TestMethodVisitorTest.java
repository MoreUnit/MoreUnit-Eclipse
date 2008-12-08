package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 19:54:02
 */
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.moreunit.SimpleProjectTestCase;
import org.moreunit.WorkspaceHelper;

public class TestMethodVisitorTest extends SimpleProjectTestCase {
	
	IType testcaseType;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testcaseType = WorkspaceHelper.createJavaClass(testPackage, "HelloTest");
	}
	
	public void testGetTestMethodsOnlyTestAnnotation() throws JavaModelException {
		String methodSource = "@Test \n public int getOne() { return 1; }";
		IMethod annotationTestMethod = testcaseType.createMethod(methodSource, null, true, null);
		TestMethodVisitor visitor = new TestMethodVisitor(testcaseType);
		List<MethodDeclaration> testMethods = visitor.getTestMethods();
		assertEquals(1, testMethods.size());
		WorkspaceHelper.assertSameMethodName(annotationTestMethod, testMethods.get(0));
	}
	
	public void testGetTestMethodsTestPrefix() throws JavaModelException {
		IMethod testMethodWithPrefix = WorkspaceHelper.createMethodInJavaType(testcaseType, "public int testGetTwo()", "");
		TestMethodVisitor visitor = new TestMethodVisitor(testcaseType);
		List<MethodDeclaration> testMethods = visitor.getTestMethods();
		assertEquals(1, testMethods.size());
		WorkspaceHelper.assertSameMethodName(testMethodWithPrefix, testMethods.get(0));
	}
	
	public void testGetTestMethodsTestAnnotationAndTestPrefix() throws JavaModelException {
		String methodSource = "@Test \n public void testGetOne() {  }";
		IMethod annotationTestMethod = testcaseType.createMethod(methodSource, null, true, null);
		TestMethodVisitor visitor = new TestMethodVisitor(testcaseType);
		List<MethodDeclaration> testMethods = visitor.getTestMethods();
		assertEquals(1, testMethods.size());
		WorkspaceHelper.assertSameMethodName(annotationTestMethod, testMethods.get(0));
	}
	
	public void testGetTestMethodsNoTestMethod() throws JavaModelException {
		TestMethodVisitor visitor = new TestMethodVisitor(testcaseType);
		List<MethodDeclaration> testMethods = visitor.getTestMethods();
		assertEquals(0, testMethods.size());
	}
}