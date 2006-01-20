package moreUnit;

import moreUnit.util.MagicNumbers;

import org.eclipse.jdt.core.IPackageFragment;

import junit.framework.TestCase;

/**
 * @author vera
 * 26.12.2005 18:25:50
 */
public class ExampleTest extends TestCase {
	TestProject testProject;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		// Projekt anlegen
		testProject = new TestProject("Test");
		IPackageFragment packageFragment = testProject.createPackage("com");
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class Hello {").append(MagicNumbers.NEWLINE);
		source.append("public int getOne() { return 1; }").append(MagicNumbers.NEWLINE);
		source.append("}");
		testProject.createType(packageFragment, "Hello.java", source.toString());
	}
	
	public void testNothing() {
		assertNotNull(testProject);
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.2  2006/01/19 21:40:18  gianasista
// Added CVS-commit-logging to all java-files
//