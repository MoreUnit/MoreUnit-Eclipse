/**
 * 
 */
package org.moreunit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.DummyPreferencesForTesting;
import org.moreunit.preferences.Preferences;

/**
 * @author vera
 *
 * 06.12.2008 16:16:21
 */
public abstract class SimpleProjectTestCase extends WorkspaceTestCase {

	private static final String TEST_FOLDER_NAME = "test";
	private static final String PACKAGE_NAME = "org";
	private static final String SOURCES_FOLDER_NAME = "sources";
	
	protected IPackageFragmentRoot sourcesFolder;
	protected IPackageFragment sourcesPackage;
	protected IPackageFragmentRoot testFolder;
	protected IPackageFragment testPackage;

	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		sourcesFolder = WorkspaceHelper.createSourceFolderInProject(workspaceTestProject, SOURCES_FOLDER_NAME);
		sourcesPackage = WorkspaceHelper.createNewPackageInSourceFolder(sourcesFolder, PACKAGE_NAME);
		testFolder = WorkspaceHelper.createSourceFolderInProject(workspaceTestProject, TEST_FOLDER_NAME);
		testPackage = WorkspaceHelper.createNewPackageInSourceFolder(testFolder, PACKAGE_NAME);
		
		initPreferencesForTestCaseContext();
	}

	private void initPreferencesForTestCaseContext() 
	{
		Preferences preferences = new DummyPreferencesForTesting();
		preferences.setHasProjectSpecificSettings(workspaceTestProject, true);
		List<SourceFolderMapping> mappingList = new ArrayList<SourceFolderMapping>();
		mappingList.add(new SourceFolderMapping(workspaceTestProject, sourcesFolder, testFolder));
		preferences.setMappingList(workspaceTestProject, mappingList);
		
		preferences.setSuffixes(workspaceTestProject, new String[] {"Test"});
		preferences.setPrefixes(workspaceTestProject, new String[] {});
	}
}
