package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 19:54:02
 */
import junit.framework.TestCase;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.TestProject;
import org.moreunit.elements.TestMethodVisitor;
import org.moreunit.util.MagicNumbers;

public class TestMethodVisitorTest extends TestCase {
	
	TestProject testProject;

	IPackageFragmentRoot junitSourceRoot;
	IPackageFragment comPaket;

	protected void setUp() throws Exception {
		super.setUp();
		
		testProject = new TestProject("ProjektJavaFileVisitor");
		comPaket = testProject.createPackage("com");
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		
		testProject.dispose();
	}
	
	public void testGetTestMethods() throws JavaModelException {
		IType exampleType = testProject.createType(comPaket, "HelloTest.java", getJavaFileSource1());
		TestMethodVisitor visitor = new TestMethodVisitor(exampleType);
		assertEquals(3, visitor.getTestMethods().size());
	}

	private String getJavaFileSource1() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class HelloTest {").append(MagicNumbers.NEWLINE);
		
		// Test annotation
		source.append("@Test").append(MagicNumbers.NEWLINE);
		source.append("public int getOne() { return 1; }").append(MagicNumbers.NEWLINE);
		
		// Test prefix
		source.append("public int testGetTwo() { return 2; }").append(MagicNumbers.NEWLINE);
		
		// nothing
		source.append("public int getThree() { return 3; }").append(MagicNumbers.NEWLINE);
		
		// Test annotation and prefix
		source.append("@Test").append(MagicNumbers.NEWLINE);
		source.append("public int testGetThree() { return 4; }").append(MagicNumbers.NEWLINE);
		
		source.append("}");
		
		return source.toString();
	}
}