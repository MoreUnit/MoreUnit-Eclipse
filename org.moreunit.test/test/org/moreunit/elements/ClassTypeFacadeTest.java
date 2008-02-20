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
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.ProjectTestCase;
import org.moreunit.util.StringConstants;

public class ClassTypeFacadeTest extends ProjectTestCase {
	
	private IPackageFragment comPaket;
	private IType javaClassType;
	private ClassTypeFacade javaFileFacade;
	
	private IPackageFragmentRoot junitSourceRoot;
	private IPackageFragment junitComPaket;
	private IType testCaseType;
	
	public void testGetOneCorrespondingTestCase() throws CoreException {
		initHelloClassInComPackageAndCorrespondingTestCase();
		IType testCaseType = javaFileFacade.getOneCorrespondingTestCase(false);
		
		assertNotNull(testCaseType);
		assertEquals("com.HelloTest", testCaseType.getFullyQualifiedName());
	}

	private void initHelloClassInComPackageAndCorrespondingTestCase() throws CoreException, JavaModelException {
		comPaket = testProject.createPackage("com");
		javaClassType = testProject.createType(comPaket, "Hello.java", getHelloSourceWithGetOneMethod());
		javaFileFacade = new ClassTypeFacade(javaClassType.getCompilationUnit());
		
		junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		
		testProject.createType(junitComPaket, "HelloTest.java", getHelloTestSourceWithGetOneTestMethod());
	}
	
	public void testGetOneCorrespondingTestCaseWithEnum() throws CoreException {
		initEnum();
		assertNull(javaFileFacade.getOneCorrespondingTestCase(false));
	}

	private void initEnum() throws CoreException, JavaModelException {
		comPaket = testProject.createPackage("com");
		javaClassType = testProject.createType(comPaket, "HelloEnum.java", getEnumSourceFile());
		javaFileFacade = new ClassTypeFacade(javaClassType.getCompilationUnit());
	}
	
	public void testGetCorrespondingTestMethod() throws CoreException {
		initMusterClassAndTestWithOneTestedAndOneUntestedMethod();

		IMethod methodGetOneString = javaClassType.getMethods()[0];
		IMethod methodGetTwoString = javaClassType.getMethods()[1];
		
		IMethod correspondingTestMethodGetOneString = javaFileFacade.getCorrespondingTestMethod(methodGetOneString, testCaseType);
		assertNotNull(correspondingTestMethodGetOneString);
		assertEquals("testGetOneString", correspondingTestMethodGetOneString.getElementName());
		assertNull(javaFileFacade.getCorrespondingTestMethod(methodGetTwoString, testCaseType));
	}

	private void initMusterClassAndTestWithOneTestedAndOneUntestedMethod() throws CoreException, JavaModelException {
		comPaket = testProject.createPackage("com");
		javaClassType = testProject.createType(comPaket, "Muster.java", getMusterSourceWithGetOneStringAndGetTwoStringMethods());
		javaFileFacade = new ClassTypeFacade(javaClassType.getCompilationUnit());
		
		junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		testCaseType = testProject.createType(junitComPaket, "MusterTest.java", getMusterTestSourceWithGetOneStringTestMethod());
	}	

	public void testGetCorrespondingTestMethods() throws CoreException {
		ClassTypeFacade javaFileFacade = initMusterClassAndTestWithMultipleTestMethodsForOneMethod();
		
		IMethod methodGetOneString = javaClassType.getMethods()[0];
		IMethod methodGetTwoString = javaClassType.getMethods()[1];
		
		List<IMethod> correspondingTestMethodGetOneString = javaFileFacade.getCorrespondingTestMethods(methodGetOneString);
		assertNotNull(correspondingTestMethodGetOneString);
		assertEquals(3, correspondingTestMethodGetOneString.size());
		assertEquals(0, javaFileFacade.getCorrespondingTestMethods(methodGetTwoString).size());
	}

	private ClassTypeFacade initMusterClassAndTestWithMultipleTestMethodsForOneMethod() throws CoreException, JavaModelException {
		comPaket = testProject.createPackage("com");
		javaClassType = testProject.createType(comPaket, "Muster.java", getMusterSourceWithGetOneStringAndGetTwoStringMethods());
		ClassTypeFacade javaFileFacade = new ClassTypeFacade(javaClassType.getCompilationUnit());
		
		IPackageFragmentRoot junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		testProject.createType(junitComPaket, "MusterTest.java", getMusterTestSourceWithMultipleTestMethodsForGetOneString());
		return javaFileFacade;
	}	
	
	private String getHelloSourceWithGetOneMethod() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("public class Hello {").append(StringConstants.NEWLINE);
		source.append("public int getOne() { return 1; }").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getHelloTestSourceWithGetOneTestMethod() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("import junit.framework.TestCase;").append(StringConstants.NEWLINE);
		source.append("public class HelloTest extends TestCase{").append(StringConstants.NEWLINE);
		source.append("public void testGetOne() {").append(StringConstants.NEWLINE);
		source.append("assertTrue(true);").append(StringConstants.NEWLINE);
		source.append("}").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();		
	}

	private String getMusterSourceWithGetOneStringAndGetTwoStringMethods() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("public class Muster {").append(StringConstants.NEWLINE);
		source.append("public String getOneString() { return \"1\"; }").append(StringConstants.NEWLINE);
		source.append("public String getTwoString() { return \"2\"; }").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getMusterTestSourceWithGetOneStringTestMethod() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("import junit.framework.TestCase;").append(StringConstants.NEWLINE);
		source.append("public class MusterTest extends TestCase{").append(StringConstants.NEWLINE);
		source.append("public void testGetOneString() {").append(StringConstants.NEWLINE);
		source.append("assertTrue(true);").append(StringConstants.NEWLINE);
		source.append("}").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();		
	}

	private String getMusterTestSourceWithMultipleTestMethodsForGetOneString() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("import junit.framework.TestCase;").append(StringConstants.NEWLINE);
		source.append("public class MusterTest extends TestCase{").append(StringConstants.NEWLINE);
		source.append("public void testGetOneString() {").append(StringConstants.NEWLINE);
		source.append("}").append(StringConstants.NEWLINE);
		source.append("public void testGetOneStringFoo() {").append(StringConstants.NEWLINE);
		source.append("}").append(StringConstants.NEWLINE);
		source.append("public void testGetOneStringBar() {").append(StringConstants.NEWLINE);
		source.append("assertTrue(true);").append(StringConstants.NEWLINE);
		source.append("}").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();		
	}

	private String getEnumSourceFile() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("public enum MyEnum {").append(StringConstants.NEWLINE);
		source.append("}").append(StringConstants.NEWLINE);
		
		return source.toString();
	}
}