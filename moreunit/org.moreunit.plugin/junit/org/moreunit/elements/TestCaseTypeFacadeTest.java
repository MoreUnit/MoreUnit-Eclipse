package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:13:50
 */

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.moreunit.ProjectTestCase;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.TestCaseTypeFacade;
import org.moreunit.util.MagicNumbers;

public class TestCaseTypeFacadeTest extends ProjectTestCase {
	
	public void testCreateTestMethodForMethod() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType musterType = testProject.createType(comPaket, "Muster.java", getJavaFileSource3());
		ClassTypeFacade javaFileFacade = new ClassTypeFacade(musterType.getCompilationUnit());
		
		IPackageFragmentRoot junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testMusterType = testProject.createType(junitComPaket, "MusterTest.java", getTestCaseSource3());
		TestCaseTypeFacade testJavaFileFacade = new TestCaseTypeFacade(testMusterType.getCompilationUnit());

		IMethod methodGetOneString = musterType.getMethods()[0];
		IMethod methodGetTwoString = musterType.getMethods()[1];
		
		IMethod correspondingTestMethodGetOneString = javaFileFacade.getCorrespondingTestMethod(methodGetOneString, testMusterType);
		assertNotNull(correspondingTestMethodGetOneString);
		assertEquals("testGetOneString", correspondingTestMethodGetOneString.getElementName());
		assertNull(javaFileFacade.getCorrespondingTestMethod(methodGetTwoString, testMusterType));
		
		testJavaFileFacade.createTestMethodForMethod(methodGetTwoString);
		IMethod correspondingTestMethodGetTwoString = javaFileFacade.getCorrespondingTestMethod(methodGetTwoString, testMusterType);
		assertEquals(2, testMusterType.getMethods().length);
		assertNotNull(correspondingTestMethodGetTwoString);
		assertEquals("testGetTwoString", correspondingTestMethodGetTwoString.getElementName());
	}

	private String getJavaFileSource3() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class Muster {").append(MagicNumbers.NEWLINE);
		source.append("public String getOneString() { return \"1\"; }").append(MagicNumbers.NEWLINE);
		source.append("public String getTwoString() { return \"2\"; }").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getTestCaseSource3() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("import junit.framework.TestCase;").append(MagicNumbers.NEWLINE);
		source.append("public class MusterTest extends TestCase{").append(MagicNumbers.NEWLINE);
		source.append("public void testGetOneString() {").append(MagicNumbers.NEWLINE);
		source.append("assertTrue(true);").append(MagicNumbers.NEWLINE);
		source.append("}").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();		
	}
}