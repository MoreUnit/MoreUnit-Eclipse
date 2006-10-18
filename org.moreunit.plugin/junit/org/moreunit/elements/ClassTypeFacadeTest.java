package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:22:53
 */

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.moreunit.ProjectTestCase;
import org.moreunit.util.MagicNumbers;

public class ClassTypeFacadeTest extends ProjectTestCase {
	
	public void testGetOneCorrespondingTestCase() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType helloType = testProject.createType(comPaket, "Hello.java", getJavaFileSource1());
		ClassTypeFacade javaFileFacade = new ClassTypeFacade(helloType.getCompilationUnit());
		
		IPackageFragmentRoot junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testHelloType = testProject.createType(junitComPaket, "HelloTest.java", getTestCaseSource1());
		
		IType testCaseType = javaFileFacade.getOneCorrespondingTestCase();
		
		assertNotNull(testCaseType);
		assertEquals("com.HelloTest", testCaseType.getFullyQualifiedName());
	}
	
	public void testGetOneCorrespondingTestCaseWithEnum() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType helloType = testProject.createType(comPaket, "HelloEnum.java", getEnumSourceFile());
		
		ClassTypeFacade javaFileFacade = new ClassTypeFacade(helloType.getCompilationUnit());
		assertNull(javaFileFacade.getOneCorrespondingTestCase());
	}
	
	public void testGetCorrespondingTestMethod() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType musterType = testProject.createType(comPaket, "Muster.java", getJavaFileSource3());
		ClassTypeFacade javaFileFacade = new ClassTypeFacade(musterType.getCompilationUnit());
		
		IPackageFragmentRoot junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testCaseType = testProject.createType(junitComPaket, "MusterTest.java", getTestCaseSource3());

		IMethod methodGetOneString = musterType.getMethods()[0];
		IMethod methodGetTwoString = musterType.getMethods()[1];
		
		IMethod correspondingTestMethodGetOneString = javaFileFacade.getCorrespondingTestMethod(methodGetOneString, testCaseType);
		assertNotNull(correspondingTestMethodGetOneString);
		assertEquals("testGetOneString", correspondingTestMethodGetOneString.getElementName());
		assertNull(javaFileFacade.getCorrespondingTestMethod(methodGetTwoString, testCaseType));
	}	

	public void testGetCorrespondingTestMethods() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType musterType = testProject.createType(comPaket, "Muster.java", getJavaFileSource3());
		ClassTypeFacade javaFileFacade = new ClassTypeFacade(musterType.getCompilationUnit());
		
		IPackageFragmentRoot junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testCaseType = testProject.createType(junitComPaket, "MusterTest.java", getTestCaseSource4());
		
		IMethod methodGetOneString = musterType.getMethods()[0];
		IMethod methodGetTwoString = musterType.getMethods()[1];
		
		List<IMethod> correspondingTestMethodGetOneString = javaFileFacade.getCorrespondingTestMethods(methodGetOneString);
		assertNotNull(correspondingTestMethodGetOneString);
		assertEquals(3, correspondingTestMethodGetOneString.size());
		assertEquals(0, javaFileFacade.getCorrespondingTestMethods(methodGetTwoString).size());
	}	
	
	private String getJavaFileSource1() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class Hello {").append(MagicNumbers.NEWLINE);
		source.append("public int getOne() { return 1; }").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getTestCaseSource1() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("import junit.framework.TestCase;").append(MagicNumbers.NEWLINE);
		source.append("public class HelloTest extends TestCase{").append(MagicNumbers.NEWLINE);
		source.append("public void testGetOne() {").append(MagicNumbers.NEWLINE);
		source.append("assertTrue(true);").append(MagicNumbers.NEWLINE);
		source.append("}").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();		
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

	private String getTestCaseSource4() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("import junit.framework.TestCase;").append(MagicNumbers.NEWLINE);
		source.append("public class MusterTest extends TestCase{").append(MagicNumbers.NEWLINE);
		source.append("public void testGetOneString() {").append(MagicNumbers.NEWLINE);
		source.append("}").append(MagicNumbers.NEWLINE);
		source.append("public void testGetOneStringFoo() {").append(MagicNumbers.NEWLINE);
		source.append("}").append(MagicNumbers.NEWLINE);
		source.append("public void testGetOneStringBar() {").append(MagicNumbers.NEWLINE);
		source.append("assertTrue(true);").append(MagicNumbers.NEWLINE);
		source.append("}").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();		
	}

	private String getEnumSourceFile() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public enum MyEnum {").append(MagicNumbers.NEWLINE);
		source.append("}").append(MagicNumbers.NEWLINE);
		
		return source.toString();
	}
}