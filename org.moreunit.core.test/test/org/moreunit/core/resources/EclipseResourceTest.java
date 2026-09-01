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
import org.junit.jupiter.api.Test;

public class EclipseResourceTest
{
    @Test
    public void delete_should_call_delete_on_underlying_resource() throws Exception
    {
        IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        Resource resource = new EclipseFile(underlyingResource);
        resource.delete();

        verify(underlyingResource).delete(true, null);
    }

    @Test
    public void delete_should_throw_resource_exception_when_core_exception_occurs() throws Exception
    {
        IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        doThrow(new CoreException(mock(org.eclipse.core.runtime.IStatus.class)))
            .when(underlyingResource).delete(anyBoolean(), any());

        Resource resource = new EclipseFile(underlyingResource);
        assertThrows(ResourceException.class, resource::delete);
    }

    @Test
    public void exists_should_delegate_to_underlying_resource()
    {
        IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        when(underlyingResource.exists()).thenReturn(true);
        Resource resource1 = new EclipseFile(underlyingResource);
        assertTrue(resource1.exists());

        when(underlyingResource.exists()).thenReturn(false);
        Resource resource2 = new EclipseFile(underlyingResource);
        assertFalse(resource2.exists());
    }

    @Test
    public void getParent_should_return_workspace_when_parent_is_null()
    {
        IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));
        when(underlyingResource.getParent()).thenReturn(null);

        Resource resource = new EclipseFile(underlyingResource);
        ResourceContainer parent = resource.getParent();

        assertTrue(parent instanceof EclipseWorkspace);
    }

    @Test
    public void getParent_should_return_workspace_when_parent_is_workspace_root()
    {
        IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        IWorkspaceRoot root = mock(IWorkspaceRoot.class);
        when(underlyingResource.getParent()).thenReturn(root);

        Resource resource = new EclipseFile(underlyingResource);
        ResourceContainer parent = resource.getParent();

        assertTrue(parent instanceof EclipseWorkspace);
    }

    @Test
    public void getParent_should_return_project_when_parent_is_project()
    {
        IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        IProject project = mock(IProject.class);
        when(project.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/parent"));
        when(underlyingResource.getParent()).thenReturn(project);

        Resource resource = new EclipseFile(underlyingResource);
        ResourceContainer parent = resource.getParent();

        assertTrue(parent instanceof EclipseProject);
    }

    @Test
    public void getParent_should_return_folder_when_parent_is_folder()
    {
        IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        IFolder folder = mock(IFolder.class);
        when(folder.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/parent"));
        when(underlyingResource.getParent()).thenReturn(folder);

        Resource resource = new EclipseFile(underlyingResource);
        ResourceContainer parent = resource.getParent();

        assertTrue(parent instanceof EclipseFolder);
    }

    @Test
    public void getParent_should_throw_resource_exception_when_parent_is_unknown_type()
    {
        IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        IContainer unknownContainer = mock(IContainer.class);
        when(underlyingResource.getParent()).thenReturn(unknownContainer);

        Resource resource = new EclipseFile(underlyingResource);
        assertThrows(ResourceException.class, resource::getParent);
    }

    @Test
    public void getUnderlyingPlatformResource_should_return_the_resource()
    {
        IFile underlyingResource = mock(IFile.class);
        when(underlyingResource.getFullPath()).thenReturn(new org.eclipse.core.runtime.Path("/project/file.txt"));

        Resource resource = new EclipseFile(underlyingResource);
        assertEquals(underlyingResource, resource.getUnderlyingPlatformResource());
    }
}
