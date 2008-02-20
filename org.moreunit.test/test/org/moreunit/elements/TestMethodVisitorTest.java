package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 19:54:02
 */
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.moreunit.ProjectTestCase;
import org.moreunit.util.StringConstants;

public class TestMethodVisitorTest extends ProjectTestCase {
	
	IPackageFragment comPaket;

	protected void setUp() throws Exception {
		super.setUp();
		
		comPaket = testProject.createPackage("com");
	}
	
	public void testGetTestMethodsOnlyTestAnnotation() throws JavaModelException {
		String methodSource = "@Test \n public int getOne() { return 1; }";
		IType testType = createTestCaseTypeWithMethodSource(methodSource);
		TestMethodVisitor visitor = new TestMethodVisitor(testType);
		
		assertEquals(1, visitor.getTestMethods().size());
		assertMethodName("getOne", visitor.getTestMethods().get(0));
	}
	
	private void assertMethodName(String resultMethodName, MethodDeclaration methodDeclaration) {
		assertEquals(resultMethodName, methodDeclaration.getName().toString());
	}
	
	public void testGetTestMethodsTestPrefix() throws JavaModelException {
		String methodSource = "public int testGetTwo() { return 2; }";
		IType testType = createTestCaseTypeWithMethodSource(methodSource);
		TestMethodVisitor visitor = new TestMethodVisitor(testType);
		
		assertEquals(1, visitor.getTestMethods().size());
		assertMethodName("testGetTwo", visitor.getTestMethods().get(0));
	}
	
	public void testGetTestMethodsTestAnnotationAndTestPrefix() throws JavaModelException {
		String methodSource = "@Test \n public int testGetTwo() { return 2; }";
		IType testType = createTestCaseTypeWithMethodSource(methodSource);
		TestMethodVisitor visitor = new TestMethodVisitor(testType);
		
		assertEquals(1, visitor.getTestMethods().size());
		assertMethodName("testGetTwo", visitor.getTestMethods().get(0));
	}
	
	public void testGetTestMethodsNoTestMethod() throws JavaModelException {
		String methodSource = "public int getThree() { return 3; }";
		IType testType = createTestCaseTypeWithMethodSource(methodSource);
		TestMethodVisitor visitor = new TestMethodVisitor(testType);
		
		assertEquals(0, visitor.getTestMethods().size());
	}
	
	private IType createTestCaseTypeWithMethodSource(String methodSource) throws JavaModelException {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("public class HelloTest {").append(StringConstants.NEWLINE);
		
		source.append(methodSource);
		
		source.append("}");
		
		return testProject.createType(comPaket, "HelloTest.java", source.toString());
	}
}