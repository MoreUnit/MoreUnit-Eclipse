package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.junit.jupiter.api.Test;

public class CreatedFolderPathTest
{
    @Test
    public void delete_should_delete_the_resource_of_a_single_folder() throws Exception
    {
        IResource resource = mock(IResource.class);

        new CreatedFolderPath(resource).delete();

        verify(resource).delete(true, null);
    }

    @Test
    public void delete_should_delete_the_first_created_folder_not_the_child() throws Exception
    {
        IResource parentResource = mock(IResource.class);
        IResource childResource = mock(IResource.class);

        CreatedFolderPath parent = new CreatedFolderPath(parentResource);
        CreatedFolderPath child = new CreatedFolderPath(parent, childResource);

        child.delete();

        verify(parentResource).delete(true, null);
        verify(childResource, never()).delete(anyBoolean(), any());
    }

    @Test
    public void delete_folders_that_are_not_parent_should_delete_non_ancestor_child() throws Exception
    {
        IResource parentResource = mock(IResource.class);
        IResource childResource = mock(IResource.class);
        IResource otherResource = mock(IResource.class);

        IPath parentPath = mock(IPath.class);
        IPath childPath = mock(IPath.class);
        IPath otherPath = mock(IPath.class);

        when(parentResource.getFullPath()).thenReturn(parentPath);
        when(childResource.getFullPath()).thenReturn(childPath);
        when(otherResource.getFullPath()).thenReturn(otherPath);

        when(childPath.isPrefixOf(otherPath)).thenReturn(false);
        when(parentPath.isPrefixOf(otherPath)).thenReturn(true);

        CreatedFolderPath parent = new CreatedFolderPath(parentResource);
        CreatedFolderPath child = new CreatedFolderPath(parent, childResource);

        child.deleteFoldersThatAreNotParentOf(otherResource);

        verify(childResource).delete(true, null);
        verify(parentResource, never()).delete(anyBoolean(), any());
    }

    @Test
    public void delete_folders_that_are_not_parent_should_do_nothing_when_resource_is_parent() throws Exception
    {
        IResource resource = mock(IResource.class);
        IResource otherResource = mock(IResource.class);
        IPath path = mock(IPath.class);
        IPath otherPath = mock(IPath.class);

        when(resource.getFullPath()).thenReturn(path);
        when(otherResource.getFullPath()).thenReturn(otherPath);
        when(path.isPrefixOf(otherPath)).thenReturn(true);

        new CreatedFolderPath(resource).deleteFoldersThatAreNotParentOf(otherResource);

        verify(resource, never()).delete(anyBoolean(), any());
    }

    @Test
    public void to_string_should_be_empty_when_no_resource()
    {
        assertEquals("", new CreatedFolderPath((IResource) null).toString());
    }
}
