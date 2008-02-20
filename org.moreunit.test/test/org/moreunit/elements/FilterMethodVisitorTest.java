package org.moreunit.elements;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.moreunit.ProjectTestCase;
import org.moreunit.util.StringConstants;

/**
 * @author vera
 *
 */
public class FilterMethodVisitorTest extends ProjectTestCase {
	
	private IPackageFragment comPaket;
	private IType javaType;
	private FilterMethodVisitor methodVisitor;

	public void testGetPrivateMethods() throws CoreException {
		initSourceVisitorWithOnePrivateMethodAndOnePublicMethod();
		
		List<MethodDeclaration> privateMethods = methodVisitor.getPrivateMethods();
		assertEquals(1, privateMethods.size());
		MethodDeclaration methodDeclaration = privateMethods.get(0);
		assertEquals("getTwo", methodDeclaration.getName().getFullyQualifiedName());
	}

	private void initSourceVisitorWithOnePrivateMethodAndOnePublicMethod() throws CoreException, JavaModelException {
		comPaket = testProject.createPackage("com");
		javaType = testProject.createType(comPaket, "PMV.java", getPmvSourceWithOnePublicAndOnePrivateMethod());
		methodVisitor = new FilterMethodVisitor(javaType);
	}

	public void testGetPrivateMethodsOverloaded() throws CoreException {
		initSourceVisitorWithOverloadedPrivateMethod();
		
		List<MethodDeclaration> privateMethods = methodVisitor.getPrivateMethods();
		assertEquals(1, privateMethods.size());
		MethodDeclaration methodDeclaration = privateMethods.get(0);
		assertEquals("getTwo", methodDeclaration.getName().getFullyQualifiedName());
	}

	private void initSourceVisitorWithOverloadedPrivateMethod()	throws CoreException, JavaModelException {
		comPaket = testProject.createPackage("com");
		javaType = testProject.createType(comPaket, "PMV2.java", getPmv2SourceWithOverloadedGetTwoMethod());
		methodVisitor = new FilterMethodVisitor(javaType);
	}
	
	public void testGetPrivateMethodsOverloaded2() throws CoreException {
		initSourceVisitorWithOverloadedPrivateMethodSituation2();
		
		List<MethodDeclaration> privateMethods = methodVisitor.getPrivateMethods();
		assertEquals(1, privateMethods.size());
		MethodDeclaration methodDeclaration = privateMethods.get(0);
		assertEquals("getTwo", methodDeclaration.getName().getFullyQualifiedName());
	}

	private void initSourceVisitorWithOverloadedPrivateMethodSituation2() throws CoreException, JavaModelException {
		comPaket = testProject.createPackage("com");
		javaType = testProject.createType(comPaket, "PMV3.java", getPmv3SourceWithOverloadedGetTwoMethod());
		methodVisitor = new FilterMethodVisitor(javaType);
	}
	
	public void testIsPrivateMethod() throws CoreException {
		initSourceVisitorWithOnePrivateMethodAndOnePublicMethod();
		
		IMethod method = javaType.getMethod("getOne", new String[0]);
		assertNotNull(method);
		assertFalse(methodVisitor.isPrivateMethod(method));
		method = javaType.getMethod("getTwo", new String[0]);
		assertNotNull(method);
		assertTrue(methodVisitor.isPrivateMethod(method));
	}
	
	public void testIsPrivateMethodOverloaded() throws CoreException {
		initSourceVisitorWithOverloadedPrivateMethod();
		
		IMethod method = javaType.getMethod("getOne", new String[0]);
		assertNotNull(method);
		assertFalse(methodVisitor.isPrivateMethod(method));
		method = javaType.getMethod("getTwo", new String[0]);
		assertNotNull(method);
		assertTrue(methodVisitor.isPrivateMethod(method));
		method = javaType.getMethod("getTwo", new String[] {Signature.createTypeSignature("String", false)});
		assertNotNull(method);
		assertFalse(methodVisitor.isPrivateMethod(method));
	}
	
	public void testIsPrivateMethodOverloaded2() throws CoreException {
		initSourceVisitorWithOverloadedPrivateMethodSituation2();
		
		IMethod method = javaType.getMethod("getOne", new String[0]);
		assertNotNull(method);
		assertFalse(methodVisitor.isPrivateMethod(method));
		method = javaType.getMethod("getTwo", new String[] {Signature.createTypeSignature("boolean", false)});
		assertNotNull(method);
		assertTrue(methodVisitor.isPrivateMethod(method));
		method = javaType.getMethod("getTwo", new String[] {Signature.createTypeSignature("String", false)});
		assertNotNull(method);
		assertFalse(methodVisitor.isPrivateMethod(method));
	}
	
	public void testGetFieldDeclarations() throws CoreException {
		initSourceWithOverloadedMethodsAndCorrectGetterAndSetter();
		
		List<FieldDeclaration> fieldDeclarations = methodVisitor.getFieldDeclarations();
		assertEquals(2, fieldDeclarations.size());
		
		FieldDeclaration fieldDeclaration = fieldDeclarations.get(0);
		VariableDeclarationFragment variable = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
		assertEquals("one", variable.getName().getFullyQualifiedName());
		
		fieldDeclaration = fieldDeclarations.get(1);
		variable = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
		assertEquals("two", variable.getName().getFullyQualifiedName());
	}

	private void initSourceWithOverloadedMethodsAndCorrectGetterAndSetter() throws CoreException, JavaModelException {
		comPaket = testProject.createPackage("com");
		javaType = testProject.createType(comPaket, "FD.java", getFdSourceWithOverloadedMethodsAndCorrectGetterSetterForTwo());
		methodVisitor = new FilterMethodVisitor(javaType);
	}
	
	public void testGetGetterMethods() throws CoreException {
		initSourceWithOverloadedMethodsAndCorrectGetterAndSetter();
		
		assertEquals(3, methodVisitor.getGetterMethods().size());
	}
	
	public void testGetSetterMethods() throws CoreException {
		initSourceWithOverloadedMethodsAndCorrectGetterAndSetter();
		
		assertEquals(1, methodVisitor.getSetterMethods().size());
	}
	
	public void testIsGetterMethod() throws CoreException {
		initSourceWithOverloadedMethodsAndCorrectGetterAndSetter();
		
		IMethod[] methods = javaType.getMethods();
		
		IMethod getOneMethod = methods[0];
		assertFalse(methodVisitor.isGetterMethod(getOneMethod));
		
		IMethod getTwoMethod1 = methods[1];
		assertFalse(methodVisitor.isGetterMethod(getTwoMethod1));
		
		IMethod getTwoMethod2 = methods[2];
		assertTrue(methodVisitor.isGetterMethod(getTwoMethod2));
	}
	
	private String getPmvSourceWithOnePublicAndOnePrivateMethod() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("public class PMV {").append(StringConstants.NEWLINE);
		source.append("public int getOne() { return 1; }").append(StringConstants.NEWLINE);
		source.append("private int getTwo() { return 2; }").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getPmv2SourceWithOverloadedGetTwoMethod() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("public class PMV2 {").append(StringConstants.NEWLINE);
		source.append("public int getOne() { return 1; }").append(StringConstants.NEWLINE);
		source.append("private int getTwo() { return 2; }").append(StringConstants.NEWLINE);
		source.append("public int getTwo(String aParameter) { return 2; }").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getPmv3SourceWithOverloadedGetTwoMethod() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("public class PMV3 {").append(StringConstants.NEWLINE);
		source.append("public int getOne() { return 1; }").append(StringConstants.NEWLINE);
		source.append("private int getTwo(boolean isParameter) { return 2; }").append(StringConstants.NEWLINE);
		source.append("public int getTwo(String aParameter) { return 2; }").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getFdSourceWithOverloadedMethodsAndCorrectGetterSetterForTwo() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("public class FD {").append(StringConstants.NEWLINE);
		source.append("private String one;").append(StringConstants.NEWLINE);
		source.append("private int two;").append(StringConstants.NEWLINE);
		source.append("public int getOne() { return 1; }").append(StringConstants.NEWLINE);
		source.append("private int getTwo(boolean isParameter) { return 2; }").append(StringConstants.NEWLINE);
		source.append("public int getTwo() { return 2; }").append(StringConstants.NEWLINE);
		source.append("public void setTwo(String aParameter) { }").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
}
