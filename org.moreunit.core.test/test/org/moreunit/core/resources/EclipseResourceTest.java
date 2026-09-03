package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.junit.jupiter.api.Test;

public class EclipseResourceTest
{
    @Test
    public void delete_should_call_delete_on_underlying_resource() throws Exception
    {
        final IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        final Resource resource = new EclipseFile(underlyingResource);
        resource.delete();

        verify(underlyingResource).delete(true, null);
    }

    @Test
    public void delete_should_throw_resource_exception_when_core_exception_occurs() throws Exception
    {
        final IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        doThrow(new CoreException(mock(org.eclipse.core.runtime.IStatus.class)))
            .when(underlyingResource).delete(anyBoolean(), any());

        final Resource resource = new EclipseFile(underlyingResource);
        assertThrows(ResourceException.class, resource::delete);
    }

    @Test
    public void exists_should_delegate_to_underlying_resource()
    {
        final IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        when(underlyingResource.exists()).thenReturn(true);
        final Resource resource1 = new EclipseFile(underlyingResource);
        assertTrue(resource1.exists());

        when(underlyingResource.exists()).thenReturn(false);
        final Resource resource2 = new EclipseFile(underlyingResource);
        assertFalse(resource2.exists());
    }

    @Test
    public void getParent_should_return_workspace_when_parent_is_null()
    {
        final IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));
        when(underlyingResource.getParent()).thenReturn(null);

        final Resource resource = new EclipseFile(underlyingResource);
        final ResourceContainer parent = resource.getParent();

        assertTrue(parent instanceof EclipseWorkspace);
    }

    @Test
    public void getParent_should_return_workspace_when_parent_is_workspace_root()
    {
        final IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        final IWorkspaceRoot root = mock(IWorkspaceRoot.class);
        when(underlyingResource.getParent()).thenReturn(root);

        final Resource resource = new EclipseFile(underlyingResource);
        final ResourceContainer parent = resource.getParent();

        assertTrue(parent instanceof EclipseWorkspace);
    }

    @Test
    public void getParent_should_return_project_when_parent_is_project()
    {
        final IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        final IProject project = mock(IProject.class);
        when(project.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/parent"));
        when(underlyingResource.getParent()).thenReturn(project);

        final Resource resource = new EclipseFile(underlyingResource);
        final ResourceContainer parent = resource.getParent();

        assertTrue(parent instanceof EclipseProject);
    }

    @Test
    public void getParent_should_return_folder_when_parent_is_folder()
    {
        final IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        final IFolder folder = mock(IFolder.class);
        when(folder.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/parent"));
        when(underlyingResource.getParent()).thenReturn(folder);

        final Resource resource = new EclipseFile(underlyingResource);
        final ResourceContainer parent = resource.getParent();

        assertTrue(parent instanceof EclipseFolder);
    }

    @Test
    public void getParent_should_throw_resource_exception_when_parent_is_unknown_type()
    {
        final IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        final IContainer unknownContainer = mock(IContainer.class);
        when(underlyingResource.getParent()).thenReturn(unknownContainer);

        final Resource resource = new EclipseFile(underlyingResource);
        assertThrows(ResourceException.class, resource::getParent);
    }

    @Test
    public void getUnderlyingPlatformResource_should_return_the_resource()
    {
        final IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        final Resource resource = new EclipseFile(underlyingResource);
        assertEquals(underlyingResource, resource.getUnderlyingPlatformResource());
    }

    @Test
    public void should_equal_itself()
    {
        final Resource resource = newResource("/project/file.txt");

        assertTrue(resource.equals(resource));
    }

    @Test
    public void should_not_equal_null()
    {
        assertFalse(newResource("/project/file.txt").equals(null));
    }

    @Test
    public void should_not_equal_resource_of_different_type_with_same_path()
    {
        final IFolder folder = mock(IFolder.class);
        when(folder.getFullPath()).thenReturn(new Path("/project/file.txt"));

        assertFalse(newResource("/project/file.txt").equals(new EclipseFolder(folder)));
    }

    @Test
    public void should_equal_resource_of_same_type_with_same_path()
    {
        assertTrue(newResource("/project/file.txt").equals(newResource("/project/file.txt")));
    }

    @Test
    public void should_not_equal_resource_with_different_path()
    {
        assertFalse(newResource("/project/file.txt").equals(newResource("/project/other.txt")));
    }

    @Test
    public void should_use_path_string_for_hash_code_and_to_string()
    {
        final Resource resource = newResource("/project/file.txt");

        assertEquals("/project/file.txt", resource.toString());
        assertEquals("/project/file.txt".hashCode(), resource.hashCode());
    }

    private Resource newResource(String fullPath)
    {
        final IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new Path(fullPath));
        return new EclipseFile(underlyingResource);
    }
}
