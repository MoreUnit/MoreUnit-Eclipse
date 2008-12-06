package org.moreunit.util;

import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.moreunit.TestProject;
import org.moreunit.preferences.DummyPreferencesForTesting;
import org.moreunit.preferences.Preferences;
import org.moreunit.util.TestCaseDiviner;

/**
 * @author giana
 *
 * 13.05.2006 13:49:29
 */
public class TestCaseDivinerTest extends TestCase {

	TestProject testProject;
	IPackageFragmentRoot junitSourceRoot;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		testProject = new TestProject("ProjektTestCaseDiviner");
		junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		
		testProject.dispose();
	}
	public void testGetMatchesOnlySuffix() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType fooType = testProject.createType(comPaket, "Foo.java", getJavaFileSource("com", "Foo"));
		
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testHelloType = testProject.createType(junitComPaket, "FooTest.java", getTestCaseSource("com", "FooTest"));
		IType testNGHelloType = testProject.createType(junitComPaket, "FooTestNG.java", getTestCaseSource("com", "FooTestNG"));
		
		PreferencesMock preferencesMock = new PreferencesMock(new String[] {}, new String[] {"Test"});
		// TODO
		//Preferences.setInstance(preferencesMock);
		TestCaseDiviner testCaseDiviner = new TestCaseDiviner(fooType.getCompilationUnit(), preferencesMock);
		Set result = testCaseDiviner.getMatches();
		assertNotNull(result);
		
		assertEquals(1, result.size());
		assertEquals(testHelloType, result.toArray()[0]);
		
		preferencesMock.setSuffixes(new String[] {"Test", "TestNG"});
		testCaseDiviner = new TestCaseDiviner(fooType.getCompilationUnit(), preferencesMock);
		result = testCaseDiviner.getMatches();
		assertEquals(2, result.size());
		assertTrue(result.contains(testHelloType));
		assertTrue(result.contains(testNGHelloType));
	}
	
	public void testGetMatchesPrefixes() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com");
		IType fooType = testProject.createType(comPaket, "Foo.java", getJavaFileSource("com", "Foo"));
		
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		IType testHelloType = testProject.createType(junitComPaket, "TestFoo.java", getTestCaseSource("com", "TestFoo"));
		IType testNGHelloType = testProject.createType(junitComPaket, "BFooTest.java", getTestCaseSource("com", "BFooTest"));
				
		PreferencesMock preferencesMock = new PreferencesMock(new String[]{"Test"}, new String[] {});
		TestCaseDiviner testCaseDiviner = new TestCaseDiviner(fooType.getCompilationUnit(), preferencesMock);
		Set result = testCaseDiviner.getMatches();
		
		assertEquals(1, result.size());
		assertTrue(result.contains(testHelloType));
		assertFalse(result.contains(testNGHelloType));
	}

	public void testGetMatchesWhenPackageNameDiffers() throws CoreException {
		IPackageFragment comPaket = testProject.createPackage("com.foo.bar");
		IType fooType = testProject.createType(comPaket, "Foo.java", getJavaFileSource("com", "Foo"));
		
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com.something");
		IType testHelloType = testProject.createType(junitComPaket, "FooTest.java", getTestCaseSource("com.something", "FooTest"));
		IType testNGHelloType = testProject.createType(junitComPaket, "FooTestNg.java", getTestCaseSource("com.something", "FooTestNg"));
		
		PreferencesMock preferencesMock = new PreferencesMock(new String[] {}, new String[] {"Test"});
		TestCaseDiviner testCaseDiviner = new TestCaseDiviner(fooType.getCompilationUnit(), preferencesMock);
		Set result = testCaseDiviner.getMatches();
		
		assertEquals(1, result.size());
		assertTrue(result.contains(testHelloType));
		assertFalse(result.contains(testNGHelloType));
	}

	private String getJavaFileSource(String packageName, String className) {
		StringBuffer source = new StringBuffer();
		source.append("package "+packageName+";").append(StringConstants.NEWLINE);
		source.append("public class "+className+" {").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getTestCaseSource(String packageName, String testcaseName) {
		StringBuffer source = new StringBuffer();
		source.append("package "+packageName+";").append(StringConstants.NEWLINE);
		source.append("import junit.framework.TestCase;").append(StringConstants.NEWLINE);
		source.append("public class "+testcaseName+" extends TestCase{").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.3  2008/03/21 18:25:15  gianasista
// First version of new property page with source folder mapping
//
// Revision 1.2  2008/02/20 19:26:54  gianasista
// Rename of classes for constants
//
// Revision 1.1  2008/02/04 20:41:11  gianasista
// Initital
//
// Revision 1.2  2006/09/19 21:48:27  channingwalton
// added some tests and logging to help debug a problem
//
// Revision 1.1.1.1  2006/08/13 14:30:56  gianasista
// initial
//
// Revision 1.1  2006/06/22 20:21:44  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:11:29  gianasista
// CVS Refactoring
//
// Revision 1.1  2006/05/13 18:32:47  gianasista
// Searching for testcases for a class (based on preferences) + Tests
//