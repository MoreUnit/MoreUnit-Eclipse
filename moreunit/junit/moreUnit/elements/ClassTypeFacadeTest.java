package moreUnit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:22:53
 */
import moreUnit.ProjectTestCase;
import moreUnit.util.MagicNumbers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;

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

}