package org.moreunit.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.moreunit.SimpleProjectTestCase;
import org.moreunit.WorkspaceHelper;
import org.moreunit.elements.SourceFolderMapping;

public class PreferencesConverterTest extends SimpleProjectTestCase
{

    private static final String SOURCEFOLDER_NAME_UNIT1 = "unit1";
    private static final String SOURCEFOLDER_NAME_UNIT2 = "unit2";
    private static final String SOURCEFOLDER_NAME_UNIT3 = "unit3";
    private static final String SOURCEFOLDER_NAME_TEST_UNIT = "atest/unit";

    private IPackageFragmentRoot unit1SourceFolder;
    private IPackageFragmentRoot unit2SourceFolder;
    private IPackageFragmentRoot unit3SourceFolder;
    private IPackageFragmentRoot testUnitSourceFolder;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        unit1SourceFolder = WorkspaceHelper.createSourceFolderInProject(workspaceTestProject, SOURCEFOLDER_NAME_UNIT1);
        unit2SourceFolder = WorkspaceHelper.createSourceFolderInProject(workspaceTestProject, SOURCEFOLDER_NAME_UNIT2);
        unit3SourceFolder = WorkspaceHelper.createSourceFolderInProject(workspaceTestProject, SOURCEFOLDER_NAME_UNIT3);
        WorkspaceHelper.createFolder(workspaceTestProject, "atest");
        testUnitSourceFolder = WorkspaceHelper.createSourceFolderInProject(workspaceTestProject, SOURCEFOLDER_NAME_TEST_UNIT);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();

        unit1SourceFolder = null;
        unit2SourceFolder = null;
        unit3SourceFolder = null;
        testUnitSourceFolder = null;
    }

    public void testConvertSourceMappingsToString()
    {
        SourceFolderMapping mapping1 = new SourceFolderMapping(workspaceTestProject, unit1SourceFolder, unit2SourceFolder);
        SourceFolderMapping mapping2 = new SourceFolderMapping(workspaceTestProject, unit2SourceFolder, unit3SourceFolder);

        List<SourceFolderMapping> mappingList = new ArrayList<SourceFolderMapping>();
        mappingList.add(mapping1);
        mappingList.add(mapping2);

        assertEquals(unit1SourceFolder.getJavaProject().getElementName() + ":" + unit1SourceFolder.getElementName() + ":" + unit2SourceFolder.getJavaProject().getElementName() + ":" + unit2SourceFolder.getElementName() + "#" + unit2SourceFolder.getJavaProject().getElementName() + ":" + unit2SourceFolder.getElementName() + ":" + unit3SourceFolder.getJavaProject().getElementName() + ":" + unit3SourceFolder.getElementName(), PreferencesConverter.convertSourceMappingsToString(mappingList));
    }

    public void testConvertSourceMappingsToStringWithSubfolder()
    {
        SourceFolderMapping mapping1 = new SourceFolderMapping(workspaceTestProject, unit1SourceFolder, testUnitSourceFolder);

        List<SourceFolderMapping> mappingList = new ArrayList<SourceFolderMapping>();
        mappingList.add(mapping1);

        assertEquals(unit1SourceFolder.getJavaProject().getElementName() + ":" + unit1SourceFolder.getElementName() + ":" + testUnitSourceFolder.getJavaProject().getElementName() + ":" + SOURCEFOLDER_NAME_TEST_UNIT, PreferencesConverter.convertSourceMappingsToString(mappingList));

    }

    public void testConvertSourceMappingsToStringLeereListe()
    {
        assertEquals("", PreferencesConverter.convertSourceMappingsToString(new ArrayList<SourceFolderMapping>()));
    }

    public void testConvertStringToSourceMappingList()
    {
        String mappingString = unit1SourceFolder.getJavaProject().getElementName() + ":" + unit1SourceFolder.getElementName() + ":" + unit2SourceFolder.getJavaProject().getElementName() + ":" + unit2SourceFolder.getElementName() + "#" + unit2SourceFolder.getJavaProject().getElementName() + ":" + unit2SourceFolder.getElementName() + ":" + unit3SourceFolder.getJavaProject().getElementName() + ":" + unit3SourceFolder.getElementName();

        List<SourceFolderMapping> mappingList = PreferencesConverter.convertStringToSourceMappingList(mappingString);
        assertEquals(2, mappingList.size());

        SourceFolderMapping firstMapping = mappingList.get(0);
        assertEquals(workspaceTestProject, firstMapping.getJavaProject());
        assertEquals(unit1SourceFolder, firstMapping.getSourceFolder());
        assertEquals(unit2SourceFolder, firstMapping.getTestFolder());

        SourceFolderMapping secondMapping = mappingList.get(1);
        assertEquals(workspaceTestProject, secondMapping.getJavaProject());
        assertEquals(unit2SourceFolder, secondMapping.getSourceFolder());
        assertEquals(unit3SourceFolder, secondMapping.getTestFolder());
    }

    public void testConvertStringToSourceMappingListWithSubfolder()
    {
        String mappingString = unit1SourceFolder.getJavaProject().getElementName() + ":" + unit1SourceFolder.getElementName() + ":" + testUnitSourceFolder.getJavaProject().getElementName() + ":" + SOURCEFOLDER_NAME_TEST_UNIT;

        List<SourceFolderMapping> mappingList = PreferencesConverter.convertStringToSourceMappingList(mappingString);
        assertEquals(1, mappingList.size());

        SourceFolderMapping firstMapping = mappingList.get(0);
        assertEquals(workspaceTestProject, firstMapping.getJavaProject());
        assertEquals(unit1SourceFolder, firstMapping.getSourceFolder());
        assertEquals(testUnitSourceFolder, firstMapping.getTestFolder());
    }

    public void testConvertArrayToString()
    {
        assertEquals("token1,token2", PreferencesConverter.convertArrayToString(new String[] { "token1", "token2" }));
    }

}
