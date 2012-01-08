package org.moreunit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.junit.Test;
import org.moreunit.test.SimpleProjectTestCase;
import org.moreunit.test.workspace.WorkspaceHelper;

/**
 * @author giana 13.05.2006 13:49:29
 */
public class TestCaseDivinerTest extends SimpleProjectTestCase
{

    @Test
    public void testGetMatchesOnlySuffix() throws CoreException
    {
        IPackageFragment comPaket = WorkspaceHelper.createNewPackageInSourceFolder(sourcesFolder, "com");
        IType fooType = WorkspaceHelper.createJavaClass(comPaket, "Foo");

        IPackageFragment junitComPaket = WorkspaceHelper.createNewPackageInSourceFolder(testFolder, "com");
        IType testHelloType = WorkspaceHelper.createJavaClass(junitComPaket, "FooTest");
        IType testNGHelloType = WorkspaceHelper.createJavaClass(junitComPaket, "FooTestNG");

        PreferencesMock preferencesMock = new PreferencesMock(new String[] {}, new String[] { "Test" });
        TestCaseDiviner testCaseDiviner = new TestCaseDiviner(fooType.getCompilationUnit(), preferencesMock);
        Set<IType> result = testCaseDiviner.getMatches();
        assertNotNull(result);

        assertEquals(1, result.size());
        assertEquals(testHelloType, result.toArray()[0]);

        preferencesMock.setSuffixes(new String[] { "Test", "TestNG" });
        testCaseDiviner = new TestCaseDiviner(fooType.getCompilationUnit(), preferencesMock);
        result = testCaseDiviner.getMatches();
        assertEquals(2, result.size());
        assertTrue(result.contains(testHelloType));
        assertTrue(result.contains(testNGHelloType));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {fooType, testHelloType, testNGHelloType});
    }

    @Test
    public void testGetMatchesPrefixes() throws CoreException
    {
        IPackageFragment comPaket = WorkspaceHelper.createNewPackageInSourceFolder(sourcesFolder, "com");
        IType fooType = WorkspaceHelper.createJavaClass(comPaket, "Foo");

        IPackageFragment junitComPaket = WorkspaceHelper.createNewPackageInSourceFolder(testFolder, "com");
        IType testHelloType = WorkspaceHelper.createJavaClass(junitComPaket, "TestFoo");
        IType testNGHelloType = WorkspaceHelper.createJavaClass(junitComPaket, "BFooTest");

        PreferencesMock preferencesMock = new PreferencesMock(new String[] { "Test" }, new String[] {});
        TestCaseDiviner testCaseDiviner = new TestCaseDiviner(fooType.getCompilationUnit(), preferencesMock);
        Set<IType> result = testCaseDiviner.getMatches();

        assertEquals(1, result.size());
        assertTrue(result.contains(testHelloType));
        assertFalse(result.contains(testNGHelloType));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {fooType, testHelloType, testNGHelloType});
    }

    @Test
    public void testGetMatchesWhenPackageNameDiffers() throws CoreException
    {
        IPackageFragment comPaket = WorkspaceHelper.createNewPackageInSourceFolder(sourcesFolder, "com.foo.bar");
        IType fooType = WorkspaceHelper.createJavaClass(comPaket, "Foo");

        IPackageFragment junitComPaket = WorkspaceHelper.createNewPackageInSourceFolder(testFolder, "com.something");
        IType testHelloType = WorkspaceHelper.createJavaClass(junitComPaket, "FooTest");
        IType testNGHelloType = WorkspaceHelper.createJavaClass(junitComPaket, "FooTestNG");

        PreferencesMock preferencesMock = new PreferencesMock(new String[] {}, new String[] { "Test" });
        TestCaseDiviner testCaseDiviner = new TestCaseDiviner(fooType.getCompilationUnit(), preferencesMock);
        Set<IType> result = testCaseDiviner.getMatches();

        assertEquals(1, result.size());
        assertTrue(result.contains(testHelloType));
        assertFalse(result.contains(testNGHelloType));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {fooType, testHelloType, testNGHelloType});
    }

    /**
     * Test for #2881409 (Switching in enums)
     * @throws CoreException
     */
    @Test
    public void testGetSource() throws CoreException 
    {
        IPackageFragment comPaket = WorkspaceHelper.createNewPackageInSourceFolder(sourcesFolder, "com.foo.bar");
        IType enumType = WorkspaceHelper.createJavaEnum(comPaket, "Foo");
        PreferencesMock preferencesMock = new PreferencesMock(new String[] {}, new String[] { "Test" });
        
        TestCaseDiviner testCaseDiviner = new TestCaseDiviner(enumType.getCompilationUnit(), preferencesMock);
        assertNotNull(testCaseDiviner.getSource());
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {enumType});
    }
}

// $Log: not supported by cvs2svn $
// Revision 1.11  2011/01/08 18:01:01  ndemengel
// Removes commented out code and TO.DO comments that are not relevant anymore
//
// Revision 1.10  2010/06/30 22:51:52  makkimesser
// FindBugs-Warning resolved
//
// Revision 1.9  2009/11/08 20:09:51  gianasista
// Refactoring of tests
//
// Revision 1.8  2009/10/19 19:33:54  gianasista
// Bugfix switching for enums
//
// Revision 1.7  2009/06/17 19:04:57  gianasista
// Switched tests to junit4
//
// Revision 1.6  2009/04/05 19:15:32  gianasista
// code formatter
//
// Revision 1.5 2009/01/25 20:11:43 gianasista
// Test refactoring
//
// Revision 1.4 2008/12/06 16:42:38 gianasista
// Test refactoring
//
// Revision 1.3 2008/03/21 18:25:15 gianasista
// First version of new property page with source folder mapping
//
// Revision 1.2 2008/02/20 19:26:54 gianasista
// Rename of classes for constants
//
// Revision 1.1 2008/02/04 20:41:11 gianasista
// Initital
//
// Revision 1.2 2006/09/19 21:48:27 channingwalton
// added some tests and logging to help debug a problem
//
// Revision 1.1.1.1 2006/08/13 14:30:56 gianasista
// initial
//
// Revision 1.1 2006/06/22 20:21:44 gianasista
// package rename
//
// Revision 1.1 2006/06/19 20:11:29 gianasista
// CVS Refactoring
//
// Revision 1.1 2006/05/13 18:32:47 gianasista
// Searching for testcases for a class (based on preferences) + Tests
//
