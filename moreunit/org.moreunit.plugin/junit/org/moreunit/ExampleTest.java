package org.moreunit;

import junit.framework.TestCase;


import org.eclipse.jdt.core.IPackageFragment;
import org.moreunit.util.MagicNumbers;

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
	}
	
	private String getJavaSource() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(MagicNumbers.NEWLINE);
		source.append("public class Hello {").append(MagicNumbers.NEWLINE);
		source.append("public int getOne() { return 1; }").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	public void testNothing() {
		assertNotNull(testProject);
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.1  2006/06/19 20:11:29  gianasista
// CVS Refactoring
//
// Revision 1.5  2006/05/18 06:57:48  channingwalton
// fixed some warnings and deprecated APIs
//
// Revision 1.4  2006/01/31 21:34:20  gianasista
// Extended TestProject to write more tests.
//
// Revision 1.3  2006/01/20 21:34:52  gianasista
// First plugin testcase implemented
//
// Revision 1.2  2006/01/19 21:40:18  gianasista
// Added CVS-commit-logging to all java-files
//