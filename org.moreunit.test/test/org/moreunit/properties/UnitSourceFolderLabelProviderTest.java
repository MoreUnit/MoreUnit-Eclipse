package org.moreunit.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.configs.SimpleJUnit4Project;

/**
 * Tests {@link UnitSourceFolderLabelProvider}.
 */
@Context(SimpleJUnit4Project.class)
public class UnitSourceFolderLabelProviderTest extends SwtPageTestCase
{
    private UnitSourceFolderLabelProvider labelProvider;
    private IJavaProject javaProject;
    private IPackageFragmentRoot testFolder;
    private SourceFolderMapping mapping;

    @BeforeEach
    public void createLabelProvider()
    {
        javaProject = context.getProjectHandler().get();
        testFolder = context.getProjectHandler().getTestSrcFolderHandler().get();
        mapping = Preferences.getInstance().getSourceMappingList(javaProject).get(0);
        labelProvider = new UnitSourceFolderLabelProvider();
    }

    @Test
    public void should_display_project_and_test_folder_for_mapping()
    {
        assertEquals(javaProject.getElementName() + "/test", labelProvider.getText(mapping));
        assertNotNull(labelProvider.getImage(mapping));
    }

    @Test
    public void should_display_project_and_test_folder_with_mapped_suffix_for_source_folder()
    {
        assertEquals(javaProject.getElementName() + "/test (mapped source folder)", labelProvider.getText(testFolder));
        assertNotNull(labelProvider.getImage(testFolder));
    }

    @Test
    public void should_fall_back_to_base_label_provider_for_other_elements()
    {
        assertEquals(javaProject.getElementName(), labelProvider.getText(javaProject));
        assertNotNull(labelProvider.getImage(javaProject));
    }

    @Test
    public void should_display_source_folder_of_mapping_for_its_own_label()
    {
        IPackageFragmentRoot mainSrcFolder = context.getProjectHandler().getMainSrcFolderHandler().get();

        assertEquals(javaProject.getElementName() + "/src (mapped source folder)", labelProvider.getText(mainSrcFolder));
        assertNotNull(labelProvider.getImage(mainSrcFolder));
    }
}
