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
        final IResource resource = mock(IResource.class);

        new CreatedFolderPath(resource).delete();

        verify(resource).delete(true, null);
    }

    @Test
    public void delete_should_delete_the_first_created_folder_not_the_child() throws Exception
    {
        final IResource parentResource = mock(IResource.class);
        final IResource childResource = mock(IResource.class);

        final CreatedFolderPath parent = new CreatedFolderPath(parentResource);
        final CreatedFolderPath child = new CreatedFolderPath(parent, childResource);

        child.delete();

        verify(parentResource).delete(true, null);
        verify(childResource, never()).delete(anyBoolean(), any());
    }

    @Test
    public void delete_folders_that_are_not_parent_should_delete_non_ancestor_child() throws Exception
    {
        final IResource parentResource = mock(IResource.class);
        final IResource childResource = mock(IResource.class);
        final IResource otherResource = mock(IResource.class);

        final IPath parentPath = mock(IPath.class);
        final IPath childPath = mock(IPath.class);
        final IPath otherPath = mock(IPath.class);

        when(parentResource.getFullPath()).thenReturn(parentPath);
        when(childResource.getFullPath()).thenReturn(childPath);
        when(otherResource.getFullPath()).thenReturn(otherPath);

        when(childPath.isPrefixOf(otherPath)).thenReturn(false);
        when(parentPath.isPrefixOf(otherPath)).thenReturn(true);

        final CreatedFolderPath parent = new CreatedFolderPath(parentResource);
        final CreatedFolderPath child = new CreatedFolderPath(parent, childResource);

        child.deleteFoldersThatAreNotParentOf(otherResource);

        verify(childResource).delete(true, null);
        verify(parentResource, never()).delete(anyBoolean(), any());
    }

    @Test
    public void delete_folders_that_are_not_parent_should_do_nothing_when_resource_is_parent() throws Exception
    {
        final IResource resource = mock(IResource.class);
        final IResource otherResource = mock(IResource.class);
        final IPath path = mock(IPath.class);
        final IPath otherPath = mock(IPath.class);

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
