package org.moreunit.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.moreunit.ProjectTestCase;

public class PluginToolsTest extends ProjectTestCase {
	
	private static final String SOURCEFOLDER_NAME_UNIT1 = "unit1";
	private static final String SOURCEFOLDER_NAME_UNIT2 = "unit2";
	private static final String SOURCEFOLDER_NAME_UNIT3 = "unit3";
	
	private IPackageFragmentRoot unit1SourceFolder;
	private IPackageFragmentRoot unit2SourceFolder;
	private IPackageFragmentRoot unit3SourceFolder;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		unit1SourceFolder = testProject.createAdditionalSourceFolder(SOURCEFOLDER_NAME_UNIT1);
		unit2SourceFolder = testProject.createAdditionalSourceFolder(SOURCEFOLDER_NAME_UNIT2);
		unit3SourceFolder = testProject.createAdditionalSourceFolder(SOURCEFOLDER_NAME_UNIT3);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		unit1SourceFolder = null;
		unit2SourceFolder = null;
		unit3SourceFolder = null;
	}

	public void testConvertSourceFoldersToString() {
		List<IPackageFragmentRoot> list = new ArrayList<IPackageFragmentRoot>(3);
		list.add(unit1SourceFolder);
		list.add(unit2SourceFolder);
		list.add(unit3SourceFolder);
		
		final String testProjectName = testProject.getJavaProject().getElementName();
		assertEquals(testProjectName+"/"+SOURCEFOLDER_NAME_UNIT1+"#"+testProjectName+"/"+SOURCEFOLDER_NAME_UNIT2+"#"+testProjectName+"/"+SOURCEFOLDER_NAME_UNIT3, PluginTools.convertSourceFoldersToString(list));
	}

	public void testConvertStringToSourceFolderList() {
		final String testProjectName = testProject.getJavaProject().getElementName();
		final String sourceFolderString = testProjectName+"/"+SOURCEFOLDER_NAME_UNIT1+"#"+testProjectName+"/"+SOURCEFOLDER_NAME_UNIT2+"#"+testProjectName+"/"+SOURCEFOLDER_NAME_UNIT3;
		
		List<IPackageFragmentRoot> sourceFolderList = PluginTools.convertStringToSourceFolderList(sourceFolderString);
		assertEquals(3, sourceFolderList.size());
		assertEquals(SOURCEFOLDER_NAME_UNIT1, sourceFolderList.get(0).getElementName());
		assertEquals(SOURCEFOLDER_NAME_UNIT2, sourceFolderList.get(1).getElementName());
		assertEquals(SOURCEFOLDER_NAME_UNIT3, sourceFolderList.get(2).getElementName());
	}
}