package moreUnit.elements;

import junit.framework.TestCase;
import moreUnit.TestProject;
import moreUnit.util.MagicNumbers;

import org.eclipse.core.runtime.CoreException;
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
		IType helloType = testProject.createType(comPaket, "Hello.java", getJavaSource());
		javaFileFacade = new JavaFileFacade(helloType.getCompilationUnit());
		
		junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testHelloType = testProject.createType(junitComPaket, "HelloTest.java", getTestSource());
		testJavaFileFacade = new JavaFileFacade(testHelloType.getCompilationUnit());		
		
		IType testCaseType = javaFileFacade.getCorrespondingTestCase();
		
		assertNotNull(testCaseType);
		assertEquals("com.HelloTest", testCaseType.getFullyQualifiedName());
	}
	
	public void testIsTestCase() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType helloType = testProject.createType(comPaket, "Hello.java", getJavaSource());
		javaFileFacade = new JavaFileFacade(helloType.getCompilationUnit());
		
		junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testHelloType = testProject.createType(junitComPaket, "HelloTest.java", getTestSource());
		testJavaFileFacade = new JavaFileFacade(testHelloType.getCompilationUnit());
		
		assertTrue(testJavaFileFacade.isTestCase());
		assertFalse(javaFileFacade.isTestCase());
	}
	
	public void testCreateTestCase() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType wowType = testProject.createType(comPaket, "Wow.java", getJavaSource2());
		javaFileFacade = new JavaFileFacade(wowType.getCompilationUnit());
		
		junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		
		IType testCaseType = javaFileFacade.createTestCase();
		assertNotNull(testCaseType);
		assertEquals("com.WowTest", testCaseType.getFullyQualifiedName());
	}
	
	private String getJavaSource() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class Hello {").append(MagicNumbers.NEWLINE);
		source.append("public int getOne() { return 1; }").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getJavaSource2() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class Wow {").append(MagicNumbers.NEWLINE);
		source.append("public String getOneString() { return \"1\"; }").append(MagicNumbers.NEWLINE);
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
// Revision 1.1  2006/01/31 21:33:32  gianasista
// Started writing the first tests for this class.
//