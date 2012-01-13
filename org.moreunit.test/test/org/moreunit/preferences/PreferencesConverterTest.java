package org.moreunit.preferences;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.test.SimpleProjectTestCase;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.configs.SimpleJUnit3Project;
import org.moreunit.test.workspace.WorkspaceHelper;

@Context(SimpleJUnit3Project.class)
public class PreferencesConverterTest extends ContextTestCase
{
    private static final String SOURCEFOLDER_NAME_UNIT1 = "unit1";
    private static final String SOURCEFOLDER_NAME_UNIT2 = "unit2";
    private static final String SOURCEFOLDER_NAME_UNIT3 = "unit3";
    private static final String SOURCEFOLDER_NAME_TEST_UNIT = "atest/unit";

    private IPackageFragmentRoot unit1SourceFolder;
    private IPackageFragmentRoot unit2SourceFolder;
    private IPackageFragmentRoot unit3SourceFolder;
    private IPackageFragmentRoot testUnitSourceFolder;
    private IFolder sampleFolder;

    @Before
    public void setUp() throws Exception
    {
        // TODO Perhaps this is not the best idea
        unit1SourceFolder = WorkspaceHelper.createSourceFolderInProject(context.getProjectHandler().get(), SOURCEFOLDER_NAME_UNIT1);
        unit2SourceFolder = WorkspaceHelper.createSourceFolderInProject(context.getProjectHandler().get(), SOURCEFOLDER_NAME_UNIT2);
        unit3SourceFolder = WorkspaceHelper.createSourceFolderInProject(context.getProjectHandler().get(), SOURCEFOLDER_NAME_UNIT3);
        sampleFolder = WorkspaceHelper.createFolder(context.getProjectHandler().get(), "atest");
        testUnitSourceFolder = WorkspaceHelper.createSourceFolderInProject(context.getProjectHandler().get(), SOURCEFOLDER_NAME_TEST_UNIT);
    }

    @After
    public void tearDown() throws Exception
    {
        testUnitSourceFolder.delete(IResource.FORCE, IPackageFragmentRoot.ORIGINATING_PROJECT_CLASSPATH, null);
        sampleFolder.delete(true, null);
        
        unit1SourceFolder.delete(IResource.FORCE, IPackageFragmentRoot.ORIGINATING_PROJECT_CLASSPATH, null);
        unit2SourceFolder.delete(IResource.FORCE, IPackageFragmentRoot.ORIGINATING_PROJECT_CLASSPATH, null);
        unit3SourceFolder.delete(IResource.FORCE, IPackageFragmentRoot.ORIGINATING_PROJECT_CLASSPATH, null);
    }

    @Test
    public void testConvertSourceMappingsToString()
    {
        SourceFolderMapping mapping1 = new SourceFolderMapping(context.getProjectHandler().get(), unit1SourceFolder, unit2SourceFolder);
        SourceFolderMapping mapping2 = new SourceFolderMapping(context.getProjectHandler().get(), unit2SourceFolder, unit3SourceFolder);

        List<SourceFolderMapping> mappingList = new ArrayList<SourceFolderMapping>();
        mappingList.add(mapping1);
        mappingList.add(mapping2);

        assertEquals(unit1SourceFolder.getJavaProject().getElementName() + ":" + unit1SourceFolder.getElementName() + ":" + unit2SourceFolder.getJavaProject().getElementName() + ":" + unit2SourceFolder.getElementName() + "#" + unit2SourceFolder.getJavaProject().getElementName() + ":" + unit2SourceFolder.getElementName() + ":" + unit3SourceFolder.getJavaProject().getElementName() + ":" + unit3SourceFolder.getElementName(), PreferencesConverter.convertSourceMappingsToString(mappingList));
    }

    @Test
    public void testConvertSourceMappingsToStringWithSubfolder()
    {
        SourceFolderMapping mapping1 = new SourceFolderMapping(context.getProjectHandler().get(), unit1SourceFolder, testUnitSourceFolder);

        List<SourceFolderMapping> mappingList = new ArrayList<SourceFolderMapping>();
        mappingList.add(mapping1);

        assertEquals(unit1SourceFolder.getJavaProject().getElementName() + ":" + unit1SourceFolder.getElementName() + ":" + testUnitSourceFolder.getJavaProject().getElementName() + ":" + SOURCEFOLDER_NAME_TEST_UNIT, PreferencesConverter.convertSourceMappingsToString(mappingList));

    }

    @Test
    public void testConvertSourceMappingsToStringLeereListe()
    {
        assertEquals("", PreferencesConverter.convertSourceMappingsToString(new ArrayList<SourceFolderMapping>()));
    }

    @Test
    public void testConvertStringToSourceMappingList()
    {
        String mappingString = unit1SourceFolder.getJavaProject().getElementName() + ":" + unit1SourceFolder.getElementName() + ":" + unit2SourceFolder.getJavaProject().getElementName() + ":" + unit2SourceFolder.getElementName() + "#" + unit2SourceFolder.getJavaProject().getElementName() + ":" + unit2SourceFolder.getElementName() + ":" + unit3SourceFolder.getJavaProject().getElementName() + ":" + unit3SourceFolder.getElementName();

        List<SourceFolderMapping> mappingList = PreferencesConverter.convertStringToSourceMappingList(mappingString);
        assertEquals(2, mappingList.size());

        SourceFolderMapping firstMapping = mappingList.get(0);
        assertEquals(context.getProjectHandler().get(), firstMapping.getJavaProject());
        assertEquals(unit1SourceFolder, firstMapping.getSourceFolder());
        assertEquals(unit2SourceFolder, firstMapping.getTestFolder());

        SourceFolderMapping secondMapping = mappingList.get(1);
        assertEquals(context.getProjectHandler().get(), secondMapping.getJavaProject());
        assertEquals(unit2SourceFolder, secondMapping.getSourceFolder());
        assertEquals(unit3SourceFolder, secondMapping.getTestFolder());
    }

    @Test
    public void testConvertStringToSourceMappingListWithSubfolder()
    {
        String mappingString = unit1SourceFolder.getJavaProject().getElementName() + ":" + unit1SourceFolder.getElementName() + ":" + testUnitSourceFolder.getJavaProject().getElementName() + ":" + SOURCEFOLDER_NAME_TEST_UNIT;

        List<SourceFolderMapping> mappingList = PreferencesConverter.convertStringToSourceMappingList(mappingString);
        assertEquals(1, mappingList.size());

        SourceFolderMapping firstMapping = mappingList.get(0);
        assertEquals(context.getProjectHandler().get(), firstMapping.getJavaProject());
        assertEquals(unit1SourceFolder, firstMapping.getSourceFolder());
        assertEquals(testUnitSourceFolder, firstMapping.getTestFolder());
    }

    @Test
    public void testConvertArrayToString()
    {
        assertEquals("token1,token2", PreferencesConverter.convertArrayToString(new String[] { "token1", "token2" }));
    }

}
