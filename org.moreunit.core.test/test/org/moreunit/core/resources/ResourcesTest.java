package org.moreunit.core.resources;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.fest.assertions.Condition;
import org.junit.Before;
import org.junit.Test;

abstract class ResourcesTest
{
    protected abstract Workspace getWorkspaceToTest() throws Exception;

    private Workspace workspace;

    @Before
    public void buildWorkspace() throws Exception
    {
        workspace = getWorkspaceToTest();

        workspace.getProject("project1").create();
        {
            workspace.getFolder("/project1/folderA/subfolderA1").create();
            workspace.getFolder("/project1/folderA/subfolderA2/subsubfolderA").create();
            workspace.getFile("/project1/folderA/subfolderA2/subsubfolderA/fileA2A").create();
            workspace.getFolder("/project1/folderB").create();
            workspace.getFolder("/project1/folderC/subfolderC").create();
            workspace.getFile("/project1/folderC/subfolderC/fileC").create();
            workspace.getFolder("/project1/folderD").create();
            workspace.getFile("/project1/folderD/fileD1").create();
            workspace.getFile("/project1/folderD/fileD2").create();
            workspace.getFile("/project1/folderD/fileD3"); // not created
            workspace.getFolder("/project1/folderF"); // not created
        }
        workspace.getProject("project2").create();
        {
            workspace.getFolder("/project2/folderZ/subfolderZ1").create();
            workspace.getFile("/project2/folderZ/subfolderZ1/fileZ1").create();
            workspace.getFolder("/project2/folderZ/subfolderZ2/subsubfolderZ21").create();
            workspace.getFolder("/project2/folderZ/subfolderZ2/subsubfolderZ22").create();
            workspace.getFile("/project2/fileAtProjectRoot").create();
        }
        workspace.getProject("project3"); // not created
    }

    @Test
    public void workspace_should_be_traversable_from_top_to_bottom_ignoring_resources_that_do_not_exist() throws Exception
    {
        List<Project> projects = workspace.listProjects();
        assertThat(namesOf(projects)).containsExactly("project1", "project2");

        assertContainsFolders(projects.get(0), "folderA", "folderB", "folderC", "folderD");
        assertContainsFiles(projects.get(0), none());
        assertContainsFolders(projects.get(0).getFolder("folderA"), "subfolderA1", "subfolderA2");
        assertContainsFiles(projects.get(0).getFolder("folderA").getFolder("subfolderA2").getFolder("subsubfolderA"), "fileA2A");
        assertThat(projects.get(0).getFolder("folderB").listFiles()).isEmpty();
        assertThat(projects.get(0).getFolder("folderB").listFolders()).isEmpty();

        assertContainsFolders(projects.get(1), "folderZ");
        assertContainsFiles(projects.get(1), "fileAtProjectRoot");
        assertContainsFolders(projects.get(1).getFolder("folderZ").getFolder("subfolderZ2"), "subsubfolderZ21", "subsubfolderZ22");
    }

    @Test
    public void workspace_should_be_traversable_from_bottom_to_top__existing_resource() throws Exception
    {
        File fileA2A = workspace.getFile("/project1/folderA/subfolderA2/subsubfolderA/fileA2A");

        assertThat(fileA2A.getParent()).isInstanceOf(Folder.class).satisfies(nameEqualTo("subsubfolderA"));
        assertThat(fileA2A.getParent().getParent().getParent()).isInstanceOf(Folder.class).satisfies(nameEqualTo("folderA"));
        assertThat(fileA2A.getParent().getParent().getParent().getParent()).isInstanceOf(Project.class).satisfies(nameEqualTo("project1"));
        assertThat(fileA2A.getParent().getParent().getParent().getParent().getParent()).isInstanceOf(Workspace.class).satisfies(nameEqualTo("/"));
        assertThat(fileA2A.getParent().getParent().getParent().getParent().getParent().getParent()).isInstanceOf(Workspace.class).satisfies(nameEqualTo("/"));
    }

    @Test
    public void workspace_should_be_traversable_from_bottom_to_top__non_existing_resource() throws Exception
    {
        File fileD3 = workspace.getFile("/project1/folderD/fileD3");

        assertFalse(fileD3.exists());
        assertThat(fileD3.getParent()).isInstanceOf(Folder.class).satisfies(nameEqualTo("folderD"));
        assertThat(fileD3.getParent().getParent()).isInstanceOf(Project.class).satisfies(nameEqualTo("project1"));
    }

    @Test
    public void resources_should_have_expected_path() throws Exception
    {
        File fileC = workspace.getFile("/project1/folderC/subfolderC/fileC");

        assertThat(fileC.getPath().toString()).isEqualTo("/project1/folderC/subfolderC/fileC");
        assertThat(fileC.getParent().getPath().toString()).isEqualTo("/project1/folderC/subfolderC");
        assertThat(fileC.getParent().getParent().getPath().toString()).isEqualTo("/project1/folderC");
        assertThat(fileC.getParent().getParent().getParent().getPath().toString()).isEqualTo("/project1");
        assertThat(fileC.getParent().getParent().getParent().getParent().getPath().toString()).isEqualTo("/");
        assertThat(fileC.getParent().getParent().getParent().getParent().getParent().getPath().toString()).isEqualTo("/");
    }

    @Test
    public void projects_should_allow_for_multiple_calls_to_create() throws Exception
    {
        assertFalse(workspace.getProject("project3").exists());

        workspace.getProject("project3").create();
        workspace.getProject("project3").create();

        assertTrue(workspace.getProject("project3").exists());
    }

    @Test
    public void folders_should_allow_for_multiple_calls_to_create() throws Exception
    {
        assertFalse(workspace.getFolder("/project1/folderF").exists());

        workspace.getFolder("/project1/folderF").create();
        workspace.getFolder("/project1/folderF").create();

        assertTrue(workspace.getFolder("/project1/folderF").exists());
    }

    @Test
    public void files_should_allow_for_multiple_calls_to_create() throws Exception
    {
        assertFalse(workspace.getFile("/project1/folderD/fileD3").exists());

        workspace.getFile("/project1/folderD/fileD3").create();
        workspace.getFile("/project1/folderD/fileD3").create();

        assertTrue(workspace.getFile("/project1/folderD/fileD3").exists());
    }

    @Test
    public void projects_should_allow_for_multiple_calls_to_delete() throws Exception
    {
        assertTrue(workspace.getProject("project1").exists());

        workspace.getProject("project1").delete();
        workspace.getProject("project1").delete();

        assertFalse(workspace.getProject("project1").exists());
    }

    @Test
    public void folders_should_allow_for_multiple_calls_to_delete() throws Exception
    {
        assertTrue(workspace.getFolder("/project1/folderD").exists());

        workspace.getFolder("/project1/folderD").delete();
        workspace.getFolder("/project1/folderD").delete();

        assertFalse(workspace.getFolder("/project1/folderD").exists());
    }

    @Test
    public void files_should_allow_for_multiple_calls_to_delete() throws Exception
    {
        assertTrue(workspace.getFile("/project1/folderD/fileD1").exists());

        workspace.getFile("/project1/folderD/fileD1").delete();
        workspace.getFile("/project1/folderD/fileD1").delete();

        assertFalse(workspace.getFile("/project1/folderD/fileD1").exists());
    }

    @Test
    public void folder_creation_should_be_cancellable() throws Exception
    {
        // given
        assertTrue(workspace.getFolder("/project1/folderA").exists());
        assertFalse(workspace.getFolder("/project1/folderA/subfolder").exists());
        assertFalse(workspace.getFolder("/project1/folderA/subfolder/subsubfolder").exists());

        // when
        ContainerCreationRecord record = workspace.getFolder("/project1/folderA/subfolder/subsubfolder").createWithRecord();

        // then
        assertTrue(workspace.getFolder("/project1/folderA/subfolder/subsubfolder").exists());

        // when
        record.cancelCreation();

        // then
        assertTrue(workspace.getFolder("/project1/folderA").exists());
        assertFalse(workspace.getFolder("/project1/folderA/subfolder").exists());
        assertFalse(workspace.getFolder("/project1/folderA/subfolder/subsubfolder").exists());
    }

    @Test
    public void workspace_should_always_exist_and_should_ignore_creation_or_deletion_requests() throws Exception
    {
        assertTrue(workspace.exists());

        workspace.create();
        // no exception

        workspace.delete();
        // no exception

        assertTrue(workspace.exists());
    }

    @Test
    public void resource_containers_other_than_workspace_should_search_for_relative_subfolders() throws Exception
    {
        Project project2 = workspace.getProject("project2");
        assertIllegalFolderAccess(project2, "/project2/folderZ");
        assertIllegalFolderAccess(project2.getFolder("folderZ"), "/project2/folderZ/subfolderZ1");
    }

    @Test
    public void resource_containers_other_than_workspace_should_search_for_relative_subfiles() throws Exception
    {
        Project project2 = workspace.getProject("project2");
        assertIllegalFileAccess(project2, "/project2/fileAtProjectRoot");
        assertIllegalFileAccess(project2.getFolder("folderZ/subfolderZ1"), "/project2/folderZ/subfolderZ1/fileZ1");
    }

    @Test
    public void resource_containers_should_complain_when_requested_subfolder_path_is_empty() throws Exception
    {
        Project project2 = workspace.getProject("project2");
        assertIllegalFolderAccess(project2, "");
        assertIllegalFolderAccess(project2.getFolder("folderZ"), "");
    }

    @Test
    public void resource_containers_should_complain_when_requested_subfile_path_is_empty() throws Exception
    {
        Project project2 = workspace.getProject("project2");
        assertIllegalFileAccess(project2, "");
        assertIllegalFileAccess(project2.getFolder("folderZ"), "");
    }

    @Test
    public void resource_containers_should_complain_when_requested_file_path_is_used_by_an_existing_folder() throws Exception
    {
        Project project2 = workspace.getProject("project2");

        // no FILE can be accessed/created with name "folderZ" because a FOLDER
        // already exists with this name
        assertFileAccessFailure(project2, "folderZ");

        // the same goes for "subfolderZ1"
        assertFileAccessFailure(project2.getFolder("folderZ"), "subfolderZ1");
    }

    @Test
    public void resource_containers_should_complain_when_requested_folder_path_is_used_by_an_existing_file() throws Exception
    {
        Project project2 = workspace.getProject("project2");

        // no FOLDER can be accessed/created with name "fileAtProjectRoot"
        // because a FILE already exists with this name
        assertFolderAccessFailure(project2, "fileAtProjectRoot");

        // the same goes for "fileZ1"
        assertFolderAccessFailure(project2.getFolder("folderZ/subfolderZ1"), "fileZ1");
    }

    @Test
    public void resource_containers_should_complain_when_requested_file_path_is_used_by_a_non_existing_folder() throws Exception
    {
        // given
        Project project2 = workspace.getProject("project2");

        // no call to create(), so folder does not exist
        project2.getFolder("some_path/some_name");

        // when
        project2.getFile("some_path");
        project2.getFile("some_path/some_name");

        // then, no exception = success
    }

    @Test
    public void resource_containers_should_complain_when_requested_folder_path_is_used_by_a_non_existing_file() throws Exception
    {
        // given
        Project project2 = workspace.getProject("project2");

        // no call to create(), so files do not exist
        project2.getFile("some_name1");
        project2.getFile("some_path/some_name2");

        // when
        project2.getFolder("some_name1");
        project2.getFolder("some_path/some_name2");

        // then, no exception = success
    }

    @Test(expected = IllegalArgumentException.class)
    public void project_names_should_contain_no_file_separator() throws Exception
    {
        workspace.getProject("/project1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void workspace_should_complain_when_requested_file_path_has_only_one_segment() throws Exception
    {
        workspace.getFile("/some-file");
    }

    @Test(expected = IllegalArgumentException.class)
    public void workspace_should_complain_when_requested_folder_path_has_only_one_segment() throws Exception
    {
        workspace.getFolder("/some-folder");
    }

    @Test
    public void paths_should_know_when_they_are_relatives() throws Exception
    {
        Path childPath = workspace.path("/some/path/with/lots/of/segments");
        Path parentPath = workspace.path("/some/path/");

        assertTrue(parentPath.isPrefixOf(childPath));
        assertFalse(childPath.isPrefixOf(parentPath));

        Path someOtherPath = workspace.path("/some/other/path");
        assertFalse(parentPath.isPrefixOf(someOtherPath));
    }

    @Test
    public void paths_should_return_themselves_without_the_last_segment() throws Exception
    {
        Path longPath = workspace.path("/some/path/with/lots/of/segments");
        assertThat(longPath.withoutLastSegment().toString()).isEqualTo("/some/path/with/lots/of");

        Path absolutePathWithSingleSegment = workspace.path("/path");
        assertThat(absolutePathWithSingleSegment.withoutLastSegment().toString()).isEqualTo("/");

        Path relativePathWithSingleSegment = workspace.path("path");
        assertThat(relativePathWithSingleSegment.withoutLastSegment().toString()).isEqualTo("");

        Path emptyPath = workspace.path("");
        assertThat(emptyPath.withoutLastSegment().toString()).isEqualTo("");

        Path rootPath = workspace.path("/");
        assertThat(rootPath.withoutLastSegment().toString()).isEqualTo("/");
    }

    @Test
    public void absolute_paths_should_return_themselves_up_to_a_given_segment() throws Exception
    {
        Path absolutePath = workspace.path("/some/path/with/lots/of/segments");

        assertThat(absolutePath.uptoSegment(0).toString()).isEqualTo("/");
        assertThat(absolutePath.uptoSegment(1).toString()).isEqualTo("/some");
        assertThat(absolutePath.uptoSegment(4).toString()).isEqualTo("/some/path/with/lots");

        try
        {
            absolutePath.uptoSegment(99);
            fail("expected IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e)
        {
            // success
        }

    }

    @Test
    public void relative_paths_should_return_themselves_up_to_a_given_segment() throws Exception
    {
        Path relativePath = workspace.path("some/path/with/lots/of/segments");

        assertThat(relativePath.uptoSegment(0).toString()).isEqualTo("");
        assertThat(relativePath.uptoSegment(1).toString()).isEqualTo("some");
        assertThat(relativePath.uptoSegment(4).toString()).isEqualTo("some/path/with/lots");

        try
        {
            relativePath.uptoSegment(99);
            fail("expected IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e)
        {
            // success
        }
    }

    @Test
    public void resource_containers_should_know_when_they_are_parent_of_other_resources() throws Exception
    {
        File childFile = workspace.getFile("/some/path/with/lots/of/segments");
        Folder parentFolder = workspace.getFolder("/some/path/");

        assertTrue(parentFolder.isParentOf(childFile));

        Folder someOtherFolder = workspace.getFolder("/some/other/path");
        assertFalse(parentFolder.isParentOf(someOtherFolder));
    }

    @Test
    public void file_extensions_should_never_be_null() throws Exception
    {
        File fileWithoutExtension = workspace.getFile("/some-project/some-file-without-extension");

        assertFalse(fileWithoutExtension.hasExtension());
        assertFalse(fileWithoutExtension.getPath().hasExtension());

        assertThat(fileWithoutExtension.getExtension()).isNotNull().isEmpty();
        assertThat(fileWithoutExtension.getPath().getExtension()).isNotNull().isEmpty();
    }

    protected void assertContainsFiles(Project project, String... fileNames)
    {
        assertContainsFiles((ResourceContainer) project, fileNames);
    }

    private void assertContainsFiles(ResourceContainer container, String... fileNames)
    {
        assertThat(namesOf(container.listFiles())).containsExactly((Object[]) fileNames);
    }

    protected void assertContainsFolders(ResourceContainer container, String... folderNames)
    {
        assertThat(namesOf(container.listFolders())).containsExactly((Object[]) folderNames);
    }

    private void assertIllegalFileAccess(ResourceContainer container, String filePath)
    {
        try
        {
            container.getFile(filePath);

            fail("expected IllegalArgumentException when accessing " + filePath + " from " + container);
        }
        catch (IllegalArgumentException e)
        {
            // success
        }
    }

    private void assertIllegalFolderAccess(ResourceContainer container, String folderPath)
    {
        try
        {
            container.getFolder(folderPath);

            fail("expected IllegalArgumentException when accessing " + folderPath + " from " + container);
        }
        catch (IllegalArgumentException e)
        {
            // success
        }
    }

    private void assertFileAccessFailure(ResourceContainer container, String filePath)
    {
        try
        {
            container.getFile(filePath);

            fail("expected IllegalArgumentException when accessing " + filePath + " from " + container);
        }
        catch (ResourceException e)
        {
            // success
        }
    }

    private void assertFolderAccessFailure(ResourceContainer container, String folderPath)
    {
        try
        {
            container.getFolder(folderPath);

            fail("expected IllegalArgumentException when accessing " + folderPath + " from " + container);
        }
        catch (ResourceException e)
        {
            // success
        }
    }

    private Condition<Object> nameEqualTo(final String expectedName)
    {
        return new Condition<Object>()
        {
            {
                as("name equal to " + expectedName);
            }

            @Override
            public boolean matches(Object r)
            {
                return r instanceof Resource && ((Resource) r).getName().equals(expectedName);
            }
        };
    }

    protected List<String> namesOf(List< ? extends Resource> resources)
    {
        return resources.stream()
                .map(Resource::getName)
                .toList();
    }

    private String[] none()
    {
        return new String[0];
    }
}
