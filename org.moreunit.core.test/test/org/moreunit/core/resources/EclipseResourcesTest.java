package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class EclipseResourcesTest extends ResourcesTest
{
    private final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

    @AfterEach
    public void cleanWorkspace() throws Exception
    {
        workspaceRoot.delete(true, true, null);
    }

    @Override
    protected Workspace getWorkspaceToTest() throws Exception
    {
        return EclipseWorkspace.get();
    }

    @Override
    protected void assertContainsFiles(Project project, String... fileNames)
    {
        final var names = new ArrayList<>();
        names.add(".project");
        names.addAll(Arrays.asList(fileNames));
        super.assertContainsFiles(project, names.toArray(new String[0]));
    }

    @Override
    protected void assertContainsFolders(ResourceContainer container, String... folderNames)
    {
        final List<String> expectedFolders = new ArrayList<>(namesOf(container.listFolders()));
        expectedFolders.removeIf(".settings"::equals);
        assertEquals(Arrays.asList(folderNames), expectedFolders);
    }

    @Test
    public void createFolder_using_string_path_should_create_folder() throws Exception
    {
        final String path = "/project1/createFolderStringPathTest";
        final Resources.CreatedFolder created = Resources.createFolder(path);
        assertTrue(created.get().exists());
    }

    @Test
    public void createFolder_should_throw_FolderCreationException_when_core_exception_occurs() throws Exception
    {
        // Try creating a folder where a file already exists
        workspaceRoot.getProject("project1").getFile("existingFile").create(new java.io.ByteArrayInputStream(new byte[0]), true, null);
        assertThrows(FolderCreationException.class, () -> Resources.createFolder("/project1/existingFile"));
    }
}
