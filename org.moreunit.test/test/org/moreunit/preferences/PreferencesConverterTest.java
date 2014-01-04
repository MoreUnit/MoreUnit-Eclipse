package org.moreunit.preferences;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
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
    public void convertSourceMappingsToString()
    {
        SourceFolderMapping mapping1 = new SourceFolderMapping(context.getProjectHandler().get(), unit1SourceFolder, unit2SourceFolder);
        SourceFolderMapping mapping2 = new SourceFolderMapping(context.getProjectHandler().get(), unit2SourceFolder, unit3SourceFolder);

        List<SourceFolderMapping> mappingList = new ArrayList<SourceFolderMapping>();
        mappingList.add(mapping1);
        mappingList.add(mapping2);

        String expected = String.format("%s:%s:%s:%s#%s:%s:%s:%s", unit1SourceFolder.getJavaProject().getElementName(), unit1SourceFolder.getElementName() 
                                                                 , unit2SourceFolder.getJavaProject().getElementName(), unit2SourceFolder.getElementName()
                                                                 , unit2SourceFolder.getJavaProject().getElementName(), unit2SourceFolder.getElementName()
                                                                 , unit3SourceFolder.getJavaProject().getElementName(), unit3SourceFolder.getElementName());
    
        assertThat(PreferencesConverter.convertSourceMappingsToString(mappingList)).isEqualTo(expected);
    }
    
    @Test
    public void createStringFromSourceMapping_should_convert_multiple_sourcefolder_to_concatenated_mapping_string()
    {
        SourceFolderMapping mapping = new SourceFolderMapping(context.getProjectHandler().get(), unit1SourceFolder, unit3SourceFolder);
        
        List<IPackageFragmentRoot> asList = new ArrayList<IPackageFragmentRoot>();
        asList.add(unit1SourceFolder);
        asList.add(unit2SourceFolder);
        mapping.setSourceFolderList(asList);
        
        String expected = String.format("%s:%s:%s:%s#%s:%s:%s:%s", unit1SourceFolder.getJavaProject().getElementName(), unit1SourceFolder.getElementName()
                                                                 , unit3SourceFolder.getJavaProject().getElementName(), unit3SourceFolder.getElementName()
                                                                 , unit2SourceFolder.getJavaProject().getElementName(), unit2SourceFolder.getElementName()
                                                                 , unit3SourceFolder.getJavaProject().getElementName(), unit3SourceFolder.getElementName());
        
        assertThat(PreferencesConverter.createStringFromSourceMapping(mapping)).isEqualTo(expected);
    }

    @Test
    public void convertSourceMappingsToString_with_subfolders()
    {
        SourceFolderMapping mapping1 = new SourceFolderMapping(context.getProjectHandler().get(), unit1SourceFolder, testUnitSourceFolder);

        List<SourceFolderMapping> mappingList = new ArrayList<SourceFolderMapping>();
        mappingList.add(mapping1);

        String expected = String.format("%s:%s:%s:%s", unit1SourceFolder.getJavaProject().getElementName(), unit1SourceFolder.getElementName()
                                                     , testUnitSourceFolder.getJavaProject().getElementName(), SOURCEFOLDER_NAME_TEST_UNIT);
        assertThat(PreferencesConverter.convertSourceMappingsToString(mappingList)).isEqualTo(expected);
    }

    @Test
    public void convertSourceMappingsToString_should_convert_empty_mapping_to_empty_string()
    {
        assertThat(PreferencesConverter.convertSourceMappingsToString(new ArrayList<SourceFolderMapping>())).isEqualTo("");
    }

    @Test
    public void convertStringToSourceMappingList()
    {
        String mappingString = String.format("%s:%s:%s:%s#%s:%s:%s:%s", unit1SourceFolder.getJavaProject().getElementName(), unit1SourceFolder.getElementName()
                                                                      , unit2SourceFolder.getJavaProject().getElementName(), unit2SourceFolder.getElementName()
                                                                      , unit2SourceFolder.getJavaProject().getElementName(), unit2SourceFolder.getElementName() 
                                                                      , unit3SourceFolder.getJavaProject().getElementName(), unit3SourceFolder.getElementName());

        List<SourceFolderMapping> mappingList = PreferencesConverter.convertStringToSourceMappingList(mappingString);
        assertThat(mappingList).hasSize(2);

        SourceFolderMapping firstMapping = mappingList.get(0);
        assertThat(firstMapping.getJavaProject()).isEqualTo(context.getProjectHandler().get());
        assertThat(firstMapping.getSourceFolderList().get(0)).isEqualTo(unit1SourceFolder);
        assertThat(firstMapping.getTestFolder()).isEqualTo(unit2SourceFolder);

        SourceFolderMapping secondMapping = mappingList.get(1);
        assertThat(secondMapping.getJavaProject()).isEqualTo(context.getProjectHandler().get());
        assertThat(secondMapping.getSourceFolderList().get(0)).isEqualTo(unit2SourceFolder);
        assertThat(secondMapping.getTestFolder()).isEqualTo(unit3SourceFolder);
    }

    @Test
    public void convertStringToSourceMappingList_with_subfolders()
    {
        String mappingString = unit1SourceFolder.getJavaProject().getElementName() + ":" + unit1SourceFolder.getElementName() + ":" + testUnitSourceFolder.getJavaProject().getElementName() + ":" + SOURCEFOLDER_NAME_TEST_UNIT;

        List<SourceFolderMapping> mappingList = PreferencesConverter.convertStringToSourceMappingList(mappingString);
        assertThat(mappingList).hasSize(1);

        SourceFolderMapping firstMapping = mappingList.get(0);
        assertThat(firstMapping.getJavaProject()).isEqualTo(context.getProjectHandler().get());
        assertThat(firstMapping.getSourceFolderList().get(0)).isEqualTo(unit1SourceFolder);
        assertThat(firstMapping.getTestFolder()).isEqualTo(testUnitSourceFolder);
    }

    @Test
        public void convertArrayToStringWithListValueDelimiter_array_with_two_elements()
        {
            assertThat(PreferencesConverter.convertArrayToStringWithListValueDelimiter(new String[] { "token1", "token2" })).isEqualTo("token1,token2");
        }

}
