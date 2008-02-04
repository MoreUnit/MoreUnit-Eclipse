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
	private TestCaseTypeFacade testJavaFileFacade;
	private ClassTypeFacade javaFileFacade;
	
	public void testGetCorrespondingTestedMethod() throws CoreException {
		initClassAndTestWithTestmethodAndCorrespondingTestedMethod();
		
		IMethod testMethod = testJavaFileFacade.getType().getMethods()[0];
		IMethod method = testJavaFileFacade.getCorrespondingTestedMethod(testMethod, javaFileFacade.getType());
		assertNotNull(method);
		assertEquals("getOneString", method.getElementName());
	}
	
	private void initClassAndTestWithTestmethodAndCorrespondingTestedMethod() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType javaType = testProject.createType(comPaket, "Muster2.java", getJavaFileSourceForTestGetCorrespondingTestedMethod());
		javaFileFacade = new ClassTypeFacade(javaType.getCompilationUnit());
		
		IPackageFragmentRoot junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testMusterType = testProject.createType(junitComPaket, "Muster2Test.java", getTestCaseSourceTestGetCorrespondingTestedMethod());
		testJavaFileFacade = new TestCaseTypeFacade(testMusterType.getCompilationUnit());
	}
	
	private String getJavaFileSourceForTestGetCorrespondingTestedMethod() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class Muster2 {").append(MagicNumbers.NEWLINE);
		source.append("public String getOneString() { return \"1\"; }").append(MagicNumbers.NEWLINE);
		source.append("public String getTwoString() { return \"2\"; }").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getTestCaseSourceTestGetCorrespondingTestedMethod() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("import junit.framework.TestCase;").append(MagicNumbers.NEWLINE);
		source.append("public class Muster2Test extends TestCase{").append(MagicNumbers.NEWLINE);
		source.append("public void testGetOneString() {").append(MagicNumbers.NEWLINE);
		source.append("assertTrue(true);").append(MagicNumbers.NEWLINE);
		source.append("}").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();		
	}

	
}