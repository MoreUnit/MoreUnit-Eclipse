package org.moreunit.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.configs.SimpleJUnit4Project;

/**
 * Tests {@link UnitSourceFolderBlock} with real SWT widgets. The block is
 * created as part of a real {@link MoreUnitPropertyPage}, like in the
 * production code.
 */
@Context(SimpleJUnit4Project.class)
public class UnitSourceFolderBlockTest extends SwtPageTestCase
{
    private MoreUnitPropertyPage page;
    private UnitSourceFolderBlock block;
    private IJavaProject javaProject;
    private SourceFolderMapping mapping;

    @BeforeEach
    public void createBlockWithinPropertyPage()
    {
        javaProject = context.getProjectHandler().get();
        page = new MoreUnitPropertyPage();
        page.setElement(javaProject);
        createContents(page, shell);

        block = (UnitSourceFolderBlock) getField(page, "firstTabUnitSourceFolder");
        mapping = block.getListOfUnitSourceFolder().get(0);
    }

    @Test
    public void should_display_mappings_from_preferences()
    {
        List<SourceFolderMapping> mappings = block.getListOfUnitSourceFolder();

        assertEquals(1, mappings.size());
        assertEquals(context.getProjectHandler().getTestSrcFolderHandler().get(), mappings.get(0).getTestFolder());
    }

    @Test
    public void should_report_error_when_no_test_folder_is_mapped()
    {
        Preferences.getInstance().setHasProjectSpecificSettings(javaProject, true);
        Preferences.getInstance().setMappingList(javaProject, new ArrayList<>());
        try
        {
            MoreUnitPropertyPage pageWithoutMappings = new MoreUnitPropertyPage();
            pageWithoutMappings.setElement(javaProject);
            createContents(pageWithoutMappings, shell);

            UnitSourceFolderBlock emptyBlock = (UnitSourceFolderBlock) getField(pageWithoutMappings, "firstTabUnitSourceFolder");

            assertEquals("Choose at least one test folder!", emptyBlock.getError());
            assertEquals("Choose at least one test folder!", pageWithoutMappings.getErrorMessage());
        }
        finally
        {
            Preferences.getInstance().setHasProjectSpecificSettings(javaProject, false);
        }
    }

    @Test
    public void should_enable_remove_and_remap_buttons_when_a_mapping_is_selected()
    {
        Button removeButton = findButton(shell, "Remove");
        Button mappingButton = findButton(shell, "Remap");

        assertFalse(removeButton.getEnabled());
        assertFalse(mappingButton.getEnabled());

        tree().setSelection(new StructuredSelection(mapping));

        assertTrue(removeButton.getEnabled());
        assertTrue(mappingButton.getEnabled());

        tree().setSelection(StructuredSelection.EMPTY);

        assertFalse(removeButton.getEnabled());
        assertFalse(mappingButton.getEnabled());
    }

    @Test
    public void should_remove_selected_mapping_when_remove_button_is_pressed()
    {
        tree().setSelection(new StructuredSelection(mapping));

        findButton(shell, "Remove").notifyListeners(SWT.Selection, new Event());

        assertTrue(block.getListOfUnitSourceFolder().isEmpty());
        assertEquals(0, tree().getTree().getItemCount());
    }

    @Test
    public void should_add_mappings_when_wizard_perform_finish_is_reported()
    {
        SourceFolderMapping newMapping = new SourceFolderMapping(javaProject, context.getProjectHandler().getMainSrcFolderHandler().get(),
                context.getProjectHandler().getTestSrcFolderHandler().get());

        block.handlePerformFinishFromAddUnitSourceFolderWizard(List.of(newMapping));

        assertEquals(2, block.getListOfUnitSourceFolder().size());
        assertEquals(2, tree().getTree().getItemCount());
    }

    @Test
    public void should_ignore_empty_mapping_list_on_wizard_perform_finish()
    {
        int initialSize = block.getListOfUnitSourceFolder().size();

        block.handlePerformFinishFromAddUnitSourceFolderWizard(List.of());

        assertEquals(initialSize, block.getListOfUnitSourceFolder().size());
    }

    @Test
    public void should_update_source_folders_of_selected_mapping()
    {
        List<IPackageFragmentRoot> newSourceFolders = List.of(context.getProjectHandler().getMainSrcFolderHandler().get());

        block.handleSourceDialogMappingFinished(mapping, newSourceFolders);

        assertEquals(newSourceFolders, mapping.getSourceFolderList());
    }

    @Test
    public void should_save_mappings_on_save_properties()
    {
        Preferences.getInstance().setHasProjectSpecificSettings(javaProject, true);
        try
        {
            block.saveProperties();

            assertEquals(1, Preferences.getInstance().getSourceMappingList(javaProject).size());
            String storedMappings = Preferences.getInstance().getProjectStore(javaProject).getString(PreferenceConstants.UNIT_SOURCE_FOLDER);
            assertNotNull(storedMappings);
            assertFalse(storedMappings.isEmpty());
        }
        finally
        {
            Preferences.getInstance().setHasProjectSpecificSettings(javaProject, false);
        }
    }

    @Test
    public void should_reenable_remove_and_remap_buttons_when_block_is_enabled_with_a_selected_mapping()
    {
        tree().setSelection(new StructuredSelection(mapping));

        block.setEnabled(true);

        assertTrue(findButton(shell, "Remove").getEnabled());
        assertTrue(findButton(shell, "Remap").getEnabled());

        block.setEnabled(false);

        assertFalse(findButton(shell, "Remove").getEnabled());
        assertFalse(findButton(shell, "Remap").getEnabled());
    }

    @Test
    public void should_accept_default_selection_events_on_buttons()
    {
        createDefaultButtonsSelectionEvents();
    }

    private void createDefaultButtonsSelectionEvents()
    {
        for (String buttonText : List.of("Add", "Remove", "Remap"))
        {
            findButton(shell, buttonText).notifyListeners(SWT.DefaultSelection, new Event());
        }
    }

    @Test
    public void should_disable_all_controls_when_block_gets_disabled()
    {
        block.setEnabled(false);

        assertFalse(tree().getTree().getEnabled());
        assertFalse(findButton(shell, "Add").getEnabled());
        assertFalse(findButton(shell, "Remove").getEnabled());
        assertFalse(findButton(shell, "Remap").getEnabled());

        block.setEnabled(true);

        assertTrue(findButton(shell, "Add").getEnabled());
        assertFalse(findButton(shell, "Remove").getEnabled());
    }

    private TreeViewer tree()
    {
        return (TreeViewer) getField(block, "sourceFolderTree");
    }
}
