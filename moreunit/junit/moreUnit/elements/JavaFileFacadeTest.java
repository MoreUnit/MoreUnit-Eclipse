package moreUnit.elements;

import junit.framework.TestCase;
import moreUnit.TestProject;
import moreUnit.util.MagicNumbers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;

public class JavaFileFacadeTest extends TestCase {
	
	TestProject testProject;

	JavaFileFacade javaFileFacade;
	JavaFileFacade testJavaFileFacade;
	
	IPackageFragmentRoot junitSourceRoot;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		testProject = new TestProject("ProjektJavaFileFacade");
		testProject.createPackage("com");
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		
		testProject.dispose();
	}

	public void testGetCorrespondingTestCase() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType helloType = testProject.createType(comPaket, "Hello.java", getJavaFileSource1());
		javaFileFacade = new JavaFileFacade(helloType.getCompilationUnit());
		
		junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testHelloType = testProject.createType(junitComPaket, "HelloTest.java", getTestCaseSource1());
		testJavaFileFacade = new JavaFileFacade(testHelloType.getCompilationUnit());		
		
		IType testCaseType = javaFileFacade.getCorrespondingTestCase();
		
		assertNotNull(testCaseType);
		assertEquals("com.HelloTest", testCaseType.getFullyQualifiedName());
	}
	
	public void testIsTestCase() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType helloType = testProject.createType(comPaket, "Hello.java", getJavaFileSource1());
		javaFileFacade = new JavaFileFacade(helloType.getCompilationUnit());
		
		junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testHelloType = testProject.createType(junitComPaket, "HelloTest.java", getTestCaseSource1());
		testJavaFileFacade = new JavaFileFacade(testHelloType.getCompilationUnit());
		
		assertTrue(testJavaFileFacade.isTestCase());
		assertFalse(javaFileFacade.isTestCase());
	}
	
	public void testCreateTestCase() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType wowType = testProject.createType(comPaket, "Wow.java", getJavaFileSource2());
		javaFileFacade = new JavaFileFacade(wowType.getCompilationUnit());
		
		junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		
		IType testCaseType = javaFileFacade.createTestCase();
		assertNotNull(testCaseType);
		assertEquals("com.WowTest", testCaseType.getFullyQualifiedName());
	}
	
	public void testGetCorrespondingTestMethod() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType musterType = testProject.createType(comPaket, "Muster.java", getJavaFileSource3());
		javaFileFacade = new JavaFileFacade(musterType.getCompilationUnit());
		
		junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		testProject.createType(junitComPaket, "MusterTest.java", getTestCaseSource3());

		IMethod methodGetOneString = musterType.getMethods()[0];
		IMethod methodGetTwoString = musterType.getMethods()[1];
		
		IMethod correspondingTestMethodGetOneString = javaFileFacade.getCorrespondingTestMethod(methodGetOneString);
		assertNotNull(correspondingTestMethodGetOneString);
		assertEquals("testGetOneString", correspondingTestMethodGetOneString.getElementName());
		assertNull(javaFileFacade.getCorrespondingTestMethod(methodGetTwoString));
	}	
	
	public void testCreateTestMethodForMethod() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType musterType = testProject.createType(comPaket, "Muster.java", getJavaFileSource3());
		javaFileFacade = new JavaFileFacade(musterType.getCompilationUnit());
		
		junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testMusterType = testProject.createType(junitComPaket, "MusterTest.java", getTestCaseSource3());
		testJavaFileFacade = new JavaFileFacade(testMusterType.getCompilationUnit());

		IMethod methodGetOneString = musterType.getMethods()[0];
		IMethod methodGetTwoString = musterType.getMethods()[1];
		
		IMethod correspondingTestMethodGetOneString = javaFileFacade.getCorrespondingTestMethod(methodGetOneString);
		assertNotNull(correspondingTestMethodGetOneString);
		assertEquals("testGetOneString", correspondingTestMethodGetOneString.getElementName());
		assertNull(javaFileFacade.getCorrespondingTestMethod(methodGetTwoString));
		
		testJavaFileFacade.createTestMethodForMethod(methodGetTwoString);
		IMethod correspondingTestMethodGetTwoString = javaFileFacade.getCorrespondingTestMethod(methodGetTwoString);
		assertEquals(2, testMusterType.getMethods().length);
		assertNotNull(correspondingTestMethodGetTwoString);
		assertEquals("testGetTwoString", correspondingTestMethodGetTwoString.getElementName());
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
	
	private String getJavaFileSource2() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class Wow {").append(MagicNumbers.NEWLINE);
		source.append("public String getOneString() { return \"1\"; }").append(MagicNumbers.NEWLINE);
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
}

// $Log$
// Revision 1.2  2006/02/01 20:57:39  gianasista
// New testmethod for createTestCase
//
// Revision 1.1  2006/01/31 21:33:32  gianasista
// Started writing the first tests for this class.
//