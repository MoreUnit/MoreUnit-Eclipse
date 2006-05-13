package moreUnit.util;

import java.util.Set;

import junit.framework.TestCase;
import moreUnit.TestProject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;

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
		
		PreferencesMock preferencesMock = new PreferencesMock();
		preferencesMock.setPrefixes(new String[] {});
		preferencesMock.setSuffixes(new String[] {"Test"});
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
				
		PreferencesMock preferencesMock = new PreferencesMock();
		preferencesMock.setPrefixes(new String[] {"Test"});
		preferencesMock.setSuffixes(new String[] {});
		TestCaseDiviner testCaseDiviner = new TestCaseDiviner(fooType.getCompilationUnit(), preferencesMock);
		Set result = testCaseDiviner.getMatches();
		
		assertEquals(1, result.size());
		assertTrue(result.contains(testHelloType));
		assertFalse(result.contains(testNGHelloType));
	}
	
	private String getJavaFileSource(String packageName, String className) {
		StringBuffer source = new StringBuffer();
		source.append("package "+packageName+";").append(MagicNumbers.NEWLINE);
		source.append("public class "+className+" {").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getTestCaseSource(String packageName, String testcaseName) {
		StringBuffer source = new StringBuffer();
		source.append("package "+packageName+";").append(MagicNumbers.NEWLINE);
		source.append("import junit.framework.TestCase;").append(MagicNumbers.NEWLINE);
		source.append("public class "+testcaseName+" extends TestCase{").append(MagicNumbers.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
}

// $Log: not supported by cvs2svn $