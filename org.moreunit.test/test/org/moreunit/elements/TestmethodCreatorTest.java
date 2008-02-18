package org.moreunit.elements;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.ProjectTestCase;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.util.MagicNumbers;

/**
 * @author vera
 *
 * 02.08.2007 07:37:24
 */
public class TestmethodCreatorTest extends ProjectTestCase {

	private IType cutType;
	private IType testType;
	
	private static final String TEST_METHODNAME = "testGetSomething";
	
	public void testCreateFirstTestMethodJUnit3() throws CoreException {
		IMethod cutMethod = initCutWithMethod();
		initTestCase(false);
		
		TestmethodCreator testmethodCreator = new TestmethodCreator(cutType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3);
		IMethod createdMethod = testmethodCreator.createTestMethod(cutMethod);
		
		assertNotNull(createdMethod);
		assertEquals(TEST_METHODNAME, createdMethod.getElementName());
		assertFalse(createdMethod.getSource().startsWith("@Test"));
	}
	
	private IMethod initTestCase(boolean shouldCreateTestMethod) throws CoreException, JavaModelException {
		IPackageFragmentRoot junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		testType = testProject.createType(junitComPaket, "MusterTest.java", getTestCaseSource(shouldCreateTestMethod));
		
		if(shouldCreateTestMethod)
			return testType.getMethods()[0];
		
		return null;
	}
	
	private IMethod initCutWithMethod() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		cutType = testProject.createType(comPaket, "Muster.java", getCutSourceWithOneMethod());
		IMethod method = cutType.getMethods()[0];
		
		return method;
	}
	
	public void testCreateFirstTestMethodJUnit4() throws CoreException {
		IMethod method = initCutWithMethod();
		initTestCase(false);
		
		TestmethodCreator testmethodCreator = new TestmethodCreator(cutType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4);
		IMethod createdMethod = testmethodCreator.createTestMethod(method);
		
		assertNotNull(createdMethod);
		assertEquals(TEST_METHODNAME, createdMethod.getElementName());
		assertTrue(createdMethod.getSource().startsWith("@Test"));
	}

	private String getTestCaseSource(boolean shouldCreateTestMethod) {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("import junit.framework.TestCase;").append(MagicNumbers.NEWLINE);
		source.append("public class MusterTest extends TestCase {").append(MagicNumbers.NEWLINE);
		if(shouldCreateTestMethod)
			source.append("public void "+TEST_METHODNAME+"() {}").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();		
	}

	private String getCutSourceWithOneMethod() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class Muster {").append(MagicNumbers.NEWLINE);
		source.append("public String getSomething() { return \"1\"; }").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	public void testCreateSecondTestMethodJUnit3() throws CoreException {
		initCutWithMethod();
		IMethod testMethod = initTestCase(true);
		
		TestmethodCreator testmethodCreator = new TestmethodCreator(testType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3);
		IMethod createdMethod = testmethodCreator.createTestMethod(testMethod);
		
		assertNotNull(createdMethod);
		assertEquals(TEST_METHODNAME+"Suffix", createdMethod.getElementName());
		assertFalse(createdMethod.getSource().startsWith("@Test"));
		assertEquals(2, testType.getMethods().length);
	}
	
	public void testCreateSecondTestMethodJUnit4() throws CoreException {
		initCutWithMethod();
		IMethod testMethod = initTestCase(true);
		
		TestmethodCreator testmethodCreator = new TestmethodCreator(testType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4);
		IMethod createdMethod = testmethodCreator.createTestMethod(testMethod);
		
		assertNotNull(createdMethod);
		assertEquals(TEST_METHODNAME+"Suffix", createdMethod.getElementName());
		assertTrue(createdMethod.getSource().startsWith("@Test"));
		assertEquals(2, testType.getMethods().length);
	}
}