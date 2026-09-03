package org.moreunit;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit3Project;
import org.moreunit.test.workspace.WorkspaceHelper;

@Context(SimpleJUnit3Project.class)
public class SourceFolderContextTest extends ContextTestCase
{
    @Test
    public void getSourceFolderToSearch_should_return_mapped_test_folder_for_mapped_main_folder()
    {
        final SourceFolderContext sourceFolderContext = SourceFolderContext.getInstance();
        sourceFolderContext.initContextForWorkspace();

        final IPackageFragmentRoot mainSrcFolder = context.getProjectHandler().getMainSrcFolderHandler().get();

        final List<IPackageFragmentRoot> foldersToSearch = sourceFolderContext.getSourceFolderToSearch(mainSrcFolder);

        assertEquals(1, foldersToSearch.size());
        assertEquals(context.getProjectHandler().getTestSrcFolderHandler().get(), foldersToSearch.get(0));
    }

    @Test
    public void getSourceFolderToSearch_should_return_mapped_main_folders_for_mapped_test_folder()
    {
        final SourceFolderContext sourceFolderContext = SourceFolderContext.getInstance();
        sourceFolderContext.initContextForWorkspace();

        final IPackageFragmentRoot testSrcFolder = context.getProjectHandler().getTestSrcFolderHandler().get();

        final List<IPackageFragmentRoot> foldersToSearch = sourceFolderContext.getSourceFolderToSearch(testSrcFolder);

        assertEquals(1, foldersToSearch.size());
        assertEquals(context.getProjectHandler().getMainSrcFolderHandler().get(), foldersToSearch.get(0));
    }

    @Test
    public void getSourceFolderToSearch_should_return_all_non_archive_folders_when_folder_is_not_mapped() throws Exception
    {
        final SourceFolderContext sourceFolderContext = SourceFolderContext.getInstance();
        sourceFolderContext.initContextForWorkspace();

        // create an additional, unmapped source folder
        WorkspaceHelper.createSourceFolderInProject(context.getProjectHandler().get(), "other");
        final IPackageFragmentRoot unmappedFolder = findRoot("other");

        final List<IPackageFragmentRoot> foldersToSearch = sourceFolderContext.getSourceFolderToSearch(unmappedFolder);

        final List<String> names = foldersToSearch.stream().map(root -> root.getPath().removeFirstSegments(1).toString()).collect(toList());
        assertEquals(3, foldersToSearch.size());
        assertTrue(names.contains("src"));
        assertTrue(names.contains("test"));
        assertTrue(names.contains("other"));
    }

    private IPackageFragmentRoot findRoot(String name) throws Exception
    {
        for (final IPackageFragmentRoot root : context.getProjectHandler().get().getPackageFragmentRoots())
        {
            if(name.equals(root.getPath().removeFirstSegments(1).toString()))
                return root;
        }
        throw new AssertionError("source folder not found: " + name);
    }
}
