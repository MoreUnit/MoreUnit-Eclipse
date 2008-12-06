package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:09:05
 */

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.WorkspaceHelper;
import org.moreunit.WorkspaceTestCase;
import org.moreunit.preferences.DummyPreferencesForTesting;
import org.moreunit.preferences.Preferences;

public class TypeFacadeTest extends WorkspaceTestCase {
	
	private IPackageFragment packageFragmentForSources;
	
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		
		IPackageFragmentRoot sourceFolder = WorkspaceHelper.createSourceFolderInProject(workspaceTestProject, "sources");
		packageFragmentForSources = WorkspaceHelper.createNewPackageInSourceFolder(sourceFolder, "com");
	}
		
	public void testIsTestCaseRegularClass() throws CoreException 
	{
		IType javaClass = WorkspaceHelper.createJavaClass(packageFragmentForSources, "Hello");		
		assertFalse(TypeFacade.isTestCase(javaClass));
	}

	public void testIsTestCaseTestWithSuffix() throws JavaModelException 
	{
		initPreferencesWithPrefixesSuffixes(new String[] {}, new String[] {"Test"});
		IType javaClass = WorkspaceHelper.createJavaClass(packageFragmentForSources, "HelloTest");
		assertTrue(TypeFacade.isTestCase(javaClass));		
	}

	public void testIsTestCaseTestWithPrefix() throws JavaModelException 
	{
		initPreferencesWithPrefixesSuffixes(new String[] {"Test"}, new String[] {});
		IType javaClass = WorkspaceHelper.createJavaClass(packageFragmentForSources, "TestHello");
		assertTrue(TypeFacade.isTestCase(javaClass));		
	}
	
	private void initPreferencesWithPrefixesSuffixes(String[] prefixes, String[] suffixes)
	{
		Preferences preferences = new DummyPreferencesForTesting();
		preferences.setPrefixes(workspaceTestProject, prefixes);
		preferences.setSuffixes(workspaceTestProject, suffixes);
	}
}