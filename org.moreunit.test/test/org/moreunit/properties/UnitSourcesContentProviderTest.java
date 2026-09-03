package org.moreunit.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.configs.SimpleJUnit4Project;

/**
 * Tests {@link UnitSourcesContentProvider}.
 */
@Context(SimpleJUnit4Project.class)
public class UnitSourcesContentProviderTest extends SwtPageTestCase
{
    private IJavaProject javaProject;
    private UnitSourcesContentProvider provider;

    @BeforeEach
    public void createProvider()
    {
        javaProject = context.getProjectHandler().get();
        provider = new UnitSourcesContentProvider(javaProject);
    }

    @Test
    public void should_return_existing_mappings_as_elements()
    {
        final List<SourceFolderMapping> mappings = Preferences.getInstance().getSourceMappingList(javaProject);

        final Object[] elements = provider.getElements(null);

        assertEquals(mappings.size(), elements.length);
        assertEquals(mappings.get(0).getTestFolder(), ((SourceFolderMapping) elements[0]).getTestFolder());
    }

    @Test
    public void should_return_source_folders_of_mapping_as_children()
    {
        final SourceFolderMapping mapping = Preferences.getInstance().getSourceMappingList(javaProject).get(0);

        assertSame(mapping.getSourceFolderList().toArray()[0], provider.getChildren(mapping)[0]);
    }

    @Test
    public void should_return_all_mappings_as_children_of_any_other_element()
    {
        final List<SourceFolderMapping> mappings = Preferences.getInstance().getSourceMappingList(javaProject);

        final Object[] children = provider.getChildren(null);

        assertEquals(mappings.size(), children.length);
        assertEquals(mappings.get(0).getTestFolder(), ((SourceFolderMapping) children[0]).getTestFolder());
    }

    @Test
    public void should_declare_children_only_for_mappings()
    {
        final SourceFolderMapping mapping = Preferences.getInstance().getSourceMappingList(javaProject).get(0);

        assertTrue(provider.hasChildren(mapping));
        assertFalse(provider.hasChildren(mapping.getTestFolder()));
    }

    @Test
    public void should_return_no_parent()
    {
        final SourceFolderMapping mapping = Preferences.getInstance().getSourceMappingList(javaProject).get(0);

        assertNull(provider.getParent(mapping));
        assertNull(provider.getParent(mapping.getTestFolder()));
    }

    @Test
    public void should_add_and_remove_mappings()
    {
        final List<SourceFolderMapping> initialContent = provider.getListOfUnitSourceFolder();
        final int initialSize = initialContent.size();

        final SourceFolderMapping newMapping = new SourceFolderMapping(javaProject, initialContent.get(0).getTestFolder());
        provider.add(newMapping);
        assertEquals(initialSize + 1, provider.getListOfUnitSourceFolder().size());
        assertTrue(provider.getListOfUnitSourceFolder().contains(newMapping));

        final SourceFolderMapping anotherMapping = new SourceFolderMapping(javaProject, context.getProjectHandler().getMainSrcFolderHandler().get());
        provider.add(List.of(anotherMapping, initialContent.get(0)));
        assertEquals(initialSize + 3, provider.getListOfUnitSourceFolder().size());

        assertTrue(provider.remove(newMapping));
        assertEquals(initialSize + 2, provider.getListOfUnitSourceFolder().size());
        assertFalse(provider.remove(newMapping));
        assertFalse(provider.getListOfUnitSourceFolder().contains(newMapping));
    }

    @Test
    public void should_refresh_content_when_input_changes()
    {
        Preferences.getInstance().setHasProjectSpecificSettings(javaProject, true);
        try
        {
            Preferences.getInstance().setMappingList(javaProject,
                    List.of(new SourceFolderMapping(javaProject, context.getProjectHandler().getMainSrcFolderHandler().get(),
                            context.getProjectHandler().getTestSrcFolderHandler().get()),
                            new SourceFolderMapping(javaProject, context.getProjectHandler().getTestSrcFolderHandler().get(),
                                    context.getProjectHandler().getMainSrcFolderHandler().get())));

            provider.inputChanged(null, null, javaProject);

            assertEquals(2, provider.getElements(null).length);
        }
        finally
        {
            Preferences.getInstance().setHasProjectSpecificSettings(javaProject, false);
        }
    }

    @Test
    public void should_implement_tree_content_provider()
    {
        assertTrue(provider instanceof ITreeContentProvider);
    }
}
