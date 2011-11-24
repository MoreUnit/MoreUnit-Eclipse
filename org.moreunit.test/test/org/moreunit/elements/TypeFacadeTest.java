package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:09:05
 */

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.WorkspaceTestCase;
import org.moreunit.preferences.DummyPreferencesForTesting;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.workspace.WorkspaceHelper;

public class TypeFacadeTest extends WorkspaceTestCase
{

    private IPackageFragment packageFragmentForSources;
    private IPackageFragmentRoot sourceFolder;

    @Before
    public void setUp() throws Exception
    {
        sourceFolder = WorkspaceHelper.createSourceFolderInProject(workspaceTestProject, "sources");
        packageFragmentForSources = WorkspaceHelper.createNewPackageInSourceFolder(sourceFolder, "com");
    }
    
    @After
    public void tearDown() throws JavaModelException
    {
        packageFragmentForSources.delete(true, null);
        sourceFolder.delete(IResource.FORCE, IPackageFragmentRoot.ORIGINATING_PROJECT_CLASSPATH, null);
    }

    @Test
    public void testIsTestCaseRegularClass() throws CoreException
    {
        IType javaClass = WorkspaceHelper.createJavaClass(packageFragmentForSources, "Hello");
        assertFalse(TypeFacade.isTestCase(javaClass));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {javaClass});
    }

    @Test
    public void testIsTestCaseTestWithSuffix() throws JavaModelException
    {
        initPreferencesWithPrefixesSuffixes(new String[] {}, new String[] { "Test" });
        IType javaClass = WorkspaceHelper.createJavaClass(packageFragmentForSources, "HelloTest");
        assertTrue(TypeFacade.isTestCase(javaClass));
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {javaClass});
    }

    @Test
    public void testIsTestCaseTestWithPrefix() throws JavaModelException
    {
        initPreferencesWithPrefixesSuffixes(new String[] { "Test" }, new String[] {});
        IType javaClass = WorkspaceHelper.createJavaClass(packageFragmentForSources, "TestHello");
        assertTrue(TypeFacade.isTestCase(javaClass));
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {javaClass});
    }

    private void initPreferencesWithPrefixesSuffixes(String[] prefixes, String[] suffixes)
    {
        Preferences preferences = new DummyPreferencesForTesting();
        preferences.setPrefixes(workspaceTestProject, prefixes);
        preferences.setSuffixes(workspaceTestProject, suffixes);
    }
}
