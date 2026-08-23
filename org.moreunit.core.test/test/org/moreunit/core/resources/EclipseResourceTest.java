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
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.junit.jupiter.api.Test;

public class EclipseResourceTest
{
    private static class DummyEclipseResource extends EclipseResource
    {
        protected DummyEclipseResource(IResource resource)
        {
            super(resource);
        }

        @Override
        public void create()
        {
        }
    }

    @Test
    public void delete_should_call_delete_on_underlying_resource() throws Exception
    {
        IResource underlyingResource = mock(IResource.class);
        IPath path = mock(IPath.class);
        when(underlyingResource.getFullPath()).thenReturn(path);

        EclipseResource resource = new DummyEclipseResource(underlyingResource);
        resource.delete();

        verify(underlyingResource).delete(true, null);
    }

    @Test
    public void delete_should_throw_resource_exception_when_core_exception_occurs() throws Exception
    {
        IResource underlyingResource = mock(IResource.class);
        IPath path = mock(IPath.class);
        when(underlyingResource.getFullPath()).thenReturn(path);

        doThrow(new CoreException(mock(org.eclipse.core.runtime.IStatus.class)))
            .when(underlyingResource).delete(anyBoolean(), any());

        EclipseResource resource = new DummyEclipseResource(underlyingResource);
        assertThrows(ResourceException.class, resource::delete);
    }

    @Test
    public void exists_should_delegate_to_underlying_resource()
    {
        IResource underlyingResource = mock(IResource.class);
        IPath path = mock(IPath.class);
        when(underlyingResource.getFullPath()).thenReturn(path);

        when(underlyingResource.exists()).thenReturn(true);
        EclipseResource resource1 = new DummyEclipseResource(underlyingResource);
        assertTrue(resource1.exists());

        when(underlyingResource.exists()).thenReturn(false);
        EclipseResource resource2 = new DummyEclipseResource(underlyingResource);
        assertFalse(resource2.exists());
    }

    @Test
    public void getParent_should_return_workspace_when_parent_is_null()
    {
        IResource underlyingResource = mock(IResource.class);
        IPath path = mock(IPath.class);
        when(underlyingResource.getFullPath()).thenReturn(path);
        when(underlyingResource.getParent()).thenReturn(null);

        EclipseResource resource = new DummyEclipseResource(underlyingResource);
        ResourceContainer parent = resource.getParent();

        assertTrue(parent instanceof EclipseWorkspace);
    }

    @Test
    public void getParent_should_return_workspace_when_parent_is_workspace_root()
    {
        IResource underlyingResource = mock(IResource.class);
        IPath path = mock(IPath.class);
        when(underlyingResource.getFullPath()).thenReturn(path);

        IWorkspaceRoot root = mock(IWorkspaceRoot.class);
        when(underlyingResource.getParent()).thenReturn(root);

        EclipseResource resource = new DummyEclipseResource(underlyingResource);
        ResourceContainer parent = resource.getParent();

        assertTrue(parent instanceof EclipseWorkspace);
    }

    @Test
    public void getParent_should_return_project_when_parent_is_project()
    {
        IResource underlyingResource = mock(IResource.class);
        IPath path = mock(IPath.class);
        when(underlyingResource.getFullPath()).thenReturn(path);

        IProject project = mock(IProject.class);
        when(project.getFullPath()).thenReturn(mock(IPath.class));
        when(underlyingResource.getParent()).thenReturn(project);

        EclipseResource resource = new DummyEclipseResource(underlyingResource);
        ResourceContainer parent = resource.getParent();

        assertTrue(parent instanceof EclipseProject);
    }

    @Test
    public void getParent_should_return_folder_when_parent_is_folder()
    {
        IResource underlyingResource = mock(IResource.class);
        IPath path = mock(IPath.class);
        when(underlyingResource.getFullPath()).thenReturn(path);

        IFolder folder = mock(IFolder.class);
        when(folder.getFullPath()).thenReturn(mock(IPath.class));
        when(underlyingResource.getParent()).thenReturn(folder);

        EclipseResource resource = new DummyEclipseResource(underlyingResource);
        ResourceContainer parent = resource.getParent();

        assertTrue(parent instanceof EclipseFolder);
    }

    @Test
    public void getParent_should_throw_resource_exception_when_parent_is_unknown_type()
    {
        IResource underlyingResource = mock(IResource.class);
        IPath path = mock(IPath.class);
        when(underlyingResource.getFullPath()).thenReturn(path);

        IContainer unknownContainer = mock(IContainer.class);
        when(underlyingResource.getParent()).thenReturn(unknownContainer);

        EclipseResource resource = new DummyEclipseResource(underlyingResource);
        assertThrows(ResourceException.class, resource::getParent);
    }

    @Test
    public void getUnderlyingPlatformResource_should_return_the_resource()
    {
        IResource underlyingResource = mock(IResource.class);
        IPath path = mock(IPath.class);
        when(underlyingResource.getFullPath()).thenReturn(path);

        EclipseResource resource = new DummyEclipseResource(underlyingResource);
        assertEquals(underlyingResource, resource.getUnderlyingPlatformResource());
    }
}
