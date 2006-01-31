package moreUnit.elements;

import junit.framework.TestCase;
import moreUnit.TestProject;
import moreUnit.util.MagicNumbers;

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
		
		IPackageFragment comPaket = testProject.createPackage("com");
		IType helloType = testProject.createType(comPaket, "Hello.java", getJavaSource());
		javaFileFacade = new JavaFileFacade(helloType.getCompilationUnit());
		
		junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testHelloType = testProject.createType(junitComPaket, "HelloTest.java", getTestSource());
		testJavaFileFacade = new JavaFileFacade(testHelloType.getCompilationUnit());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		
		testProject.dispose();
	}

	public void testGetCorrespondingTestCase() {
		IType testCaseType = javaFileFacade.getCorrespondingTestCase();
		
		assertNotNull(testCaseType);
		assertEquals("com.HelloTest", testCaseType.getFullyQualifiedName());
	}
	
	public void testIsTestCase() {
		assertTrue(testJavaFileFacade.isTestCase());
		assertFalse(javaFileFacade.isTestCase());
	}
	
	private String getJavaSource() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class Hello {").append(MagicNumbers.NEWLINE);
		source.append("public int getOne() { return 1; }").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getTestSource() {
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
}

// $Log$