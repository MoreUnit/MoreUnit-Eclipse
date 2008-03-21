package org.moreunit.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.moreunit.ProjectTestCase;
import org.moreunit.elements.SourceFolderMapping;

public class PreferencesConverterTest extends ProjectTestCase {
	
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

	public void testConvertSourceMappingsToString() {
		SourceFolderMapping mapping1 = new SourceFolderMapping(testProject.getJavaProject(), unit1SourceFolder, unit2SourceFolder);
		SourceFolderMapping mapping2 = new SourceFolderMapping(testProject.getJavaProject(), unit2SourceFolder, unit3SourceFolder);
		
		List<SourceFolderMapping> mappingList = new ArrayList<SourceFolderMapping>();
		mappingList.add(mapping1);
		mappingList.add(mapping2);
		
		assertEquals(unit1SourceFolder.getJavaProject().getElementName()+":"+unit1SourceFolder.getElementName()+":"+
				     unit2SourceFolder.getJavaProject().getElementName()+":"+unit2SourceFolder.getElementName()+"#"+
				     unit2SourceFolder.getJavaProject().getElementName()+":"+unit2SourceFolder.getElementName()+":"+
				     unit3SourceFolder.getJavaProject().getElementName()+":"+unit3SourceFolder.getElementName(),
				     PreferencesConverter.convertSourceMappingsToString(mappingList));
	}

	public void testConvertStringToSourceMappingList() {
		String mappingString = unit1SourceFolder.getJavaProject().getElementName()+":"+unit1SourceFolder.getElementName()+":"+
	     					   unit2SourceFolder.getJavaProject().getElementName()+":"+unit2SourceFolder.getElementName()+"#"+
	     					   unit2SourceFolder.getJavaProject().getElementName()+":"+unit2SourceFolder.getElementName()+":"+
	     					   unit3SourceFolder.getJavaProject().getElementName()+":"+unit3SourceFolder.getElementName();
		
		List<SourceFolderMapping> mappingList = PreferencesConverter.convertStringToSourceMappingList(mappingString);
		assertEquals(2, mappingList.size());
		
		SourceFolderMapping firstMapping = mappingList.get(0);
		assertEquals(testProject.getJavaProject(), firstMapping.getJavaProject());
		assertEquals(unit1SourceFolder, firstMapping.getSourceFolder());
		assertEquals(unit2SourceFolder, firstMapping.getTestFolder());
		
		SourceFolderMapping secondMapping = mappingList.get(1);
		assertEquals(testProject.getJavaProject(), secondMapping.getJavaProject());
		assertEquals(unit2SourceFolder, secondMapping.getSourceFolder());
		assertEquals(unit3SourceFolder, secondMapping.getTestFolder());
	}

	public void testConvertArrayToString() {
		assertEquals("token1,token2", PreferencesConverter.convertArrayToString(new String[] {"token1", "token2"}));
	}

}
