package org.moreunit.elements;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.moreunit.ProjectTestCase;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.util.MagicNumbers;

/**
 * @author vera
 *
 * 02.08.2007 07:37:24
 */
public class TestmethodCreatorTest extends ProjectTestCase {

	/*
	 * Situation tested:
	 * - Creation of a new testmethod for a method.
	 * - JUnit 3
	 */
	public void testCreateTestMethod1_1() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType musterType = testProject.createType(comPaket, "Muster.java", getJavaFileSource1());
		ClassTypeFacade javaFileFacade = new ClassTypeFacade(musterType.getCompilationUnit());
		IMethod method = musterType.getMethods()[0];
		
		IPackageFragmentRoot junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testMusterType = testProject.createType(junitComPaket, "MusterTest.java", getTestCaseSource1());
		TestCaseTypeFacade testJavaFileFacade = new TestCaseTypeFacade(testMusterType.getCompilationUnit());
		
		TestmethodCreator testmethodCreator = new TestmethodCreator(javaFileFacade.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3);
		IMethod createdMethod = testmethodCreator.createTestMethod(method);
		
		assertNotNull(createdMethod);
		assertEquals("testGetOneString", createdMethod.getElementName());
		assertFalse(createdMethod.getSource().startsWith("@Test"));
	}
	
	/*
	 * Situation tested:
	 * - Creation of a new testmethod for a method.
	 * - JUnit 4
	 */
	public void testCreateTestMethod1_2() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType musterType = testProject.createType(comPaket, "Muster.java", getJavaFileSource1());
		ClassTypeFacade javaFileFacade = new ClassTypeFacade(musterType.getCompilationUnit());
		IMethod method = musterType.getMethods()[0];
		
		IPackageFragmentRoot junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testMusterType = testProject.createType(junitComPaket, "MusterTest.java", getTestCaseSource1());
		TestCaseTypeFacade testJavaFileFacade = new TestCaseTypeFacade(testMusterType.getCompilationUnit());
		
		TestmethodCreator testmethodCreator = new TestmethodCreator(javaFileFacade.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4);
		IMethod createdMethod = testmethodCreator.createTestMethod(method);
		
		assertNotNull(createdMethod);
		assertEquals("testGetOneString", createdMethod.getElementName());
		assertTrue(createdMethod.getSource().startsWith("@Test"));
	}

	private String getTestCaseSource1() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("import junit.framework.TestCase;").append(MagicNumbers.NEWLINE);
		source.append("public class MusterTest extends TestCase {").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();		
	}

	private String getJavaFileSource1() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class Muster {").append(MagicNumbers.NEWLINE);
		source.append("public String getOneString() { return \"1\"; }").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	/*
	 * Situation tested:
	 * - Creation of a new second testmethod.
	 * - JUnit 3
	 */
	public void testCreateTestMethod2_1() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType musterType = testProject.createType(comPaket, "Muster.java", getJavaFileSource2());
		ClassTypeFacade javaFileFacade = new ClassTypeFacade(musterType.getCompilationUnit());
		
		IPackageFragmentRoot junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testMusterType = testProject.createType(junitComPaket, "MusterTest.java", getTestCaseSource2());
		TestCaseTypeFacade testJavaFileFacade = new TestCaseTypeFacade(testMusterType.getCompilationUnit());
		IMethod method = testMusterType.getMethods()[0];
		
		TestmethodCreator testmethodCreator = new TestmethodCreator(testJavaFileFacade.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3);
		IMethod createdMethod = testmethodCreator.createTestMethod(method);
		
		assertNotNull(createdMethod);
		assertEquals("testGetOneStringSuffix", createdMethod.getElementName());
		assertFalse(createdMethod.getSource().startsWith("@Test"));
		assertEquals(2, testMusterType.getMethods().length);
	}
	
	/*
	 * Situation tested:
	 * - Creation of a new second testmethod.
	 * - JUnit 4
	 */
	public void testCreateTestMethod2_2() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType musterType = testProject.createType(comPaket, "Muster.java", getJavaFileSource2());
		ClassTypeFacade javaFileFacade = new ClassTypeFacade(musterType.getCompilationUnit());
		
		IPackageFragmentRoot junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testMusterType = testProject.createType(junitComPaket, "MusterTest.java", getTestCaseSource2());
		TestCaseTypeFacade testJavaFileFacade = new TestCaseTypeFacade(testMusterType.getCompilationUnit());
		IMethod method = testMusterType.getMethods()[0];
		
		TestmethodCreator testmethodCreator = new TestmethodCreator(testJavaFileFacade.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4);
		IMethod createdMethod = testmethodCreator.createTestMethod(method);
		
		assertNotNull(createdMethod);
		assertEquals("testGetOneStringSuffix", createdMethod.getElementName());
		assertTrue(createdMethod.getSource().startsWith("@Test"));
		assertEquals(2, testMusterType.getMethods().length);
	}
	
	private String getTestCaseSource2() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("import junit.framework.TestCase;").append(MagicNumbers.NEWLINE);
		source.append("public class MusterTest extends TestCase {").append(MagicNumbers.NEWLINE);
		source.append("public void testGetOneString() {}").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();		
	}

	private String getJavaFileSource2() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class Muster {").append(MagicNumbers.NEWLINE);
		source.append("public String getOneString() { return \"1\"; }").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
}