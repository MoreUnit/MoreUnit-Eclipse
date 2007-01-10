package org.moreunit.elements;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.junit.Test;
import org.moreunit.ProjectTestCase;
import org.moreunit.util.MagicNumbers;

/**
 * @author vera
 *
 */
public class FilterMethodVisitorTest extends ProjectTestCase {

	public void testGetPrivateMethods() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType pmvType = testProject.createType(comPaket, "PMV.java", getJavaFileSource1());
		FilterMethodVisitor privateMethodVisitor = new FilterMethodVisitor(pmvType);
		List<MethodDeclaration> privateMethods = privateMethodVisitor.getPrivateMethods();
		assertEquals(1, privateMethods.size());
		MethodDeclaration methodDeclaration = privateMethods.get(0);
		assertEquals("getTwo", methodDeclaration.getName().getFullyQualifiedName());
	}

	public void testGetPrivateMethodsOverloaded() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType pmvType = testProject.createType(comPaket, "PMV2.java", getJavaFileSource2());
		FilterMethodVisitor privateMethodVisitor = new FilterMethodVisitor(pmvType);
		List<MethodDeclaration> privateMethods = privateMethodVisitor.getPrivateMethods();
		assertEquals(1, privateMethods.size());
		MethodDeclaration methodDeclaration = privateMethods.get(0);
		assertEquals("getTwo", methodDeclaration.getName().getFullyQualifiedName());
	}
	
	public void testGetPrivateMethodsOverloaded2() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType pmvType = testProject.createType(comPaket, "PMV3.java", getJavaFileSource3());
		FilterMethodVisitor privateMethodVisitor = new FilterMethodVisitor(pmvType);
		List<MethodDeclaration> privateMethods = privateMethodVisitor.getPrivateMethods();
		assertEquals(1, privateMethods.size());
		MethodDeclaration methodDeclaration = privateMethods.get(0);
		assertEquals("getTwo", methodDeclaration.getName().getFullyQualifiedName());
	}
	
	public void testIsPrivateMethod() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType pmvType = testProject.createType(comPaket, "PMV.java", getJavaFileSource1());
		FilterMethodVisitor privateMethodVisitor = new FilterMethodVisitor(pmvType);
		IMethod method = pmvType.getMethod("getOne", new String[0]);
		assertNotNull(method);
		assertFalse(privateMethodVisitor.isPrivateMethod(method));
		method = pmvType.getMethod("getTwo", new String[0]);
		assertNotNull(method);
		assertTrue(privateMethodVisitor.isPrivateMethod(method));
	}
	
	public void testIsPrivateMethodOverloaded() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType pmvType = testProject.createType(comPaket, "PMV2.java", getJavaFileSource2());
		FilterMethodVisitor privateMethodVisitor = new FilterMethodVisitor(pmvType);
		IMethod method = pmvType.getMethod("getOne", new String[0]);
		assertNotNull(method);
		assertFalse(privateMethodVisitor.isPrivateMethod(method));
		method = pmvType.getMethod("getTwo", new String[0]);
		assertNotNull(method);
		assertTrue(privateMethodVisitor.isPrivateMethod(method));
		method = pmvType.getMethod("getTwo", new String[] {Signature.createTypeSignature("String", false)});
		assertNotNull(method);
		assertFalse(privateMethodVisitor.isPrivateMethod(method));
	}
	
	public void testIsPrivateMethodOverloaded2() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType pmvType = testProject.createType(comPaket, "PMV3.java", getJavaFileSource3());
		FilterMethodVisitor privateMethodVisitor = new FilterMethodVisitor(pmvType);
		IMethod method = pmvType.getMethod("getOne", new String[0]);
		assertNotNull(method);
		assertFalse(privateMethodVisitor.isPrivateMethod(method));
		method = pmvType.getMethod("getTwo", new String[] {Signature.createTypeSignature("boolean", false)});
		assertNotNull(method);
		assertTrue(privateMethodVisitor.isPrivateMethod(method));
		method = pmvType.getMethod("getTwo", new String[] {Signature.createTypeSignature("String", false)});
		assertNotNull(method);
		assertFalse(privateMethodVisitor.isPrivateMethod(method));
	}
	
	public void testGetFieldDeclarations() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType pmvType = testProject.createType(comPaket, "FD.java", getJavaFileSource4());
		FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(pmvType);
		List<FieldDeclaration> fieldDeclarations = filterMethodVisitor.getFieldDeclarations();
		assertEquals(2, fieldDeclarations.size());
		
		FieldDeclaration fieldDeclaration = fieldDeclarations.get(0);
		VariableDeclarationFragment variable = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
		assertEquals("one", variable.getName().getFullyQualifiedName());
		
		fieldDeclaration = fieldDeclarations.get(1);
		variable = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
		assertEquals("two", variable.getName().getFullyQualifiedName());
	}
	
	public void testGetGetterMethods() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType pmvType = testProject.createType(comPaket, "FD.java", getJavaFileSource4());
		FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(pmvType);
		
		assertEquals(3, filterMethodVisitor.getGetterMethods().size());
	}
	
	public void testGetSetterMethods() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType pmvType = testProject.createType(comPaket, "FD.java", getJavaFileSource4());
		FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(pmvType);
		
		assertEquals(1, filterMethodVisitor.getSetterMethods().size());
	}
	
	public void testIsGetterMethod() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType pmvType = testProject.createType(comPaket, "FD.java", getJavaFileSource4());
		FilterMethodVisitor filterMethodVisitor = new FilterMethodVisitor(pmvType);
		
		IMethod[] methods = pmvType.getMethods();
		
		IMethod getOneMethod = methods[0];
		assertFalse(filterMethodVisitor.isGetterMethod(getOneMethod));
		
		IMethod getTwoMethod1 = methods[1];
		assertFalse(filterMethodVisitor.isGetterMethod(getTwoMethod1));
		
		IMethod getTwoMethod2 = methods[2];
		assertTrue(filterMethodVisitor.isGetterMethod(getTwoMethod2));
		
		//IMethod setTwoMethod
		
	}
	
	private String getJavaFileSource1() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class PMV {").append(MagicNumbers.NEWLINE);
		source.append("public int getOne() { return 1; }").append(MagicNumbers.NEWLINE);
		source.append("private int getTwo() { return 2; }").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getJavaFileSource2() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class PMV2 {").append(MagicNumbers.NEWLINE);
		source.append("public int getOne() { return 1; }").append(MagicNumbers.NEWLINE);
		source.append("private int getTwo() { return 2; }").append(MagicNumbers.NEWLINE);
		source.append("public int getTwo(String aParameter) { return 2; }").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getJavaFileSource3() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class PMV3 {").append(MagicNumbers.NEWLINE);
		source.append("public int getOne() { return 1; }").append(MagicNumbers.NEWLINE);
		source.append("private int getTwo(boolean isParameter) { return 2; }").append(MagicNumbers.NEWLINE);
		source.append("public int getTwo(String aParameter) { return 2; }").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getJavaFileSource4() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class FD {").append(MagicNumbers.NEWLINE);
		source.append("private String one;").append(MagicNumbers.NEWLINE);
		source.append("private int two;").append(MagicNumbers.NEWLINE);
		source.append("public int getOne() { return 1; }").append(MagicNumbers.NEWLINE);
		source.append("private int getTwo(boolean isParameter) { return 2; }").append(MagicNumbers.NEWLINE);
		source.append("public int getTwo() { return 2; }").append(MagicNumbers.NEWLINE);
		source.append("public void setTwo(String aParameter) { }").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}





}
