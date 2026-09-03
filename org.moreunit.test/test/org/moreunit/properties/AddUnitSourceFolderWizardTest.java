package org.moreunit.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckable;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.configs.SimpleJUnit4Project;

/**
 * Tests {@link AddUnitSourceFolderWizard} and
 * {@link AddUnitSourceFolderWizardPage} with real SWT widgets. The wizard
 * dialog itself is never opened (it is modal); instead the wizard API is
 * driven directly and check state changes are simulated the way JFace
 * notifies {@link ICheckStateListener}s (via the private {@code check} helper
 * below).
 */
@Context(SimpleJUnit4Project.class)
public class AddUnitSourceFolderWizardTest extends SwtPageTestCase
{
    private MoreUnitPropertyPage propertyPage;
    private UnitSourceFolderBlock block;
    private AddUnitSourceFolderWizard wizard;
    private IJavaProject javaProject;

    @BeforeEach
    public void createWizard()
    {
        javaProject = context.getProjectHandler().get();
        propertyPage = new MoreUnitPropertyPage();
        propertyPage.setElement(javaProject);
        createContents(propertyPage, shell);

        block = (UnitSourceFolderBlock) getField(propertyPage, "firstTabUnitSourceFolder");
        wizard = new AddUnitSourceFolderWizard(javaProject, block);
        wizard.addPages();
    }

    private AddUnitSourceFolderWizardPage wizardPage()
    {
        return (AddUnitSourceFolderWizardPage) wizard.getPages()[0];
    }

    private CheckboxTreeViewer checkboxTreeViewer()
    {
        return (CheckboxTreeViewer) getField(wizardPage(), "checkboxTreeViewer");
    }

    /**
     * Simulates a user click on the check box of the given element: sets the
     * item's state and notifies the page's check state listener.
     */
    private static void check(CheckboxTreeViewer viewer, AddUnitSourceFolderWizardPage page, Object element, boolean checked)
    {
        viewer.setChecked(element, checked);
        page.checkStateChanged(new CheckStateChangedEvent((ICheckable) viewer, element, checked));
    }

    @Test
    public void should_declare_a_single_page_not_complete_by_default()
    {
        IWizardPage[] pages = wizard.getPages();

        assertEquals(1, pages.length);
        assertEquals("Add Unit Source Folder", wizardPage().getTitle());
        assertEquals("Please select source folders to add", wizardPage().getDescription());
        assertFalse(wizardPage().isPageComplete());
    }

    @Test
    public void should_display_source_folders_that_are_not_already_test_folders()
    {
        AddUnitSourceFolderWizardPage page = wizardPage();
        page.createControl(shell);
        CheckboxTreeViewer viewer = checkboxTreeViewer();

        ITreeContentProvider contentProvider = (ITreeContentProvider) viewer.getContentProvider();

        Object[] elements = contentProvider.getElements(viewer.getInput());
        assertEquals(1, elements.length);
        assertEquals(javaProject, elements[0]);

        // the default test folder ("test") is filtered out, only "src" remains
        Object[] children = contentProvider.getChildren(javaProject);
        assertEquals(1, children.length);
        assertEquals(context.getProjectHandler().getMainSrcFolderHandler().get(), children[0]);
    }

    @Test
    public void should_complete_page_when_all_source_folders_of_a_project_are_checked()
    {
        AddUnitSourceFolderWizardPage page = wizardPage();
        page.createControl(shell);
        CheckboxTreeViewer viewer = checkboxTreeViewer();

        check(viewer, page, javaProject, true);

        assertTrue(page.isPageComplete());
        assertTrue(viewer.getChecked(javaProject));

        check(viewer, page, javaProject, false);

        assertFalse(page.isPageComplete());
        assertFalse(viewer.getChecked(javaProject));
    }

    @Test
    public void should_gray_project_when_only_part_of_its_source_folders_are_checked()
    {
        // remove the default mapping so that all source folders are offered
        Preferences.getInstance().setHasProjectSpecificSettings(javaProject, true);
        Preferences.getInstance().setMappingList(javaProject, List.of());
        try
        {
            UnitSourceFolderBlock blockWithoutMappings = new UnitSourceFolderBlock(javaProject, propertyPage);
            blockWithoutMappings.getControl(shell);

            AddUnitSourceFolderWizard wizardWithoutMappings = new AddUnitSourceFolderWizard(javaProject, blockWithoutMappings);
            wizardWithoutMappings.addPages();
            AddUnitSourceFolderWizardPage page = (AddUnitSourceFolderWizardPage) wizardWithoutMappings.getPages()[0];
            page.createControl(shell);

            CheckboxTreeViewer viewer = (CheckboxTreeViewer) getField(page, "checkboxTreeViewer");
            ITreeContentProvider contentProvider = (ITreeContentProvider) viewer.getContentProvider();
            assertEquals(2, contentProvider.getChildren(javaProject).length);

            // check only one folder: project gets grayed
            check(viewer, page, context.getProjectHandler().getMainSrcFolderHandler().get(), true);

            assertTrue(viewer.getGrayed(javaProject));

            // check the other folder too: project gets fully checked
            check(viewer, page, context.getProjectHandler().getTestSrcFolderHandler().get(), true);

            assertFalse(viewer.getGrayed(javaProject));
            assertTrue(viewer.getChecked(javaProject));
            assertTrue(page.isPageComplete());

            // uncheck one folder again: project gets grayed
            check(viewer, page, context.getProjectHandler().getTestSrcFolderHandler().get(), false);

            assertTrue(viewer.getGrayed(javaProject));

            // uncheck the last folder: the project stays grayed (setChecked
            // does not clear the grayed state) but becomes unchecked
            check(viewer, page, context.getProjectHandler().getMainSrcFolderHandler().get(), false);

            assertTrue(viewer.getGrayed(javaProject));
            assertFalse(viewer.getChecked(javaProject));
            assertFalse(page.isPageComplete());
        }
        finally
        {
            Preferences.getInstance().setHasProjectSpecificSettings(javaProject, false);
        }
    }

    @Test
    public void should_add_selected_source_folders_to_block_when_wizard_performs_finish()
    {
        AddUnitSourceFolderWizardPage page = wizardPage();
        page.createControl(shell);
        CheckboxTreeViewer viewer = checkboxTreeViewer();

        int initialMappingCount = block.getListOfUnitSourceFolder().size();

        check(viewer, page, context.getProjectHandler().getMainSrcFolderHandler().get(), true);

        assertTrue(wizard.performFinish());

        List<SourceFolderMapping> mappings = block.getListOfUnitSourceFolder();
        assertEquals(initialMappingCount + 1, mappings.size());
        assertEquals(context.getProjectHandler().getMainSrcFolderHandler().get(), mappings.get(mappings.size() - 1).getTestFolder());
    }

    @Test
    public void should_return_mappings_of_property_page()
    {
        assertEquals(block.getListOfUnitSourceFolder(), wizard.getUnitSourceFolderFromPropertyPage());
    }
}
