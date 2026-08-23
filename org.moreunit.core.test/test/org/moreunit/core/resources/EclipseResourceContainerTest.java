package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.junit.jupiter.api.Test;

public class EclipseResourceContainerTest
{
    private static class DummyEclipseResourceContainer extends EclipseResourceContainer
    {
        protected DummyEclipseResourceContainer(IContainer container)
        {
            super(container);
        }

        @Override
        public void create()
        {
        }
    }

    @Test
    public void getFile_should_throw_when_folder_already_exists()
    {
        IContainer platformContainer = mock(IContainer.class);
        when(platformContainer.getFullPath()).thenReturn(mock(IPath.class));

        IFolder platformFolder = mock(IFolder.class);
        when(platformFolder.exists()).thenReturn(true);
        when(platformContainer.getFolder(any(org.eclipse.core.runtime.Path.class))).thenReturn(platformFolder);

        EclipseResourceContainer container = new DummyEclipseResourceContainer(platformContainer);

        assertThrows(ResourceException.class, () -> container.getFile(new InMemoryPath("path/to/file")));
    }

    @Test
    public void getFile_should_return_eclipse_file_when_folder_does_not_exist()
    {
        IContainer platformContainer = mock(IContainer.class);
        when(platformContainer.getFullPath()).thenReturn(mock(IPath.class));

        IFolder platformFolder = mock(IFolder.class);
        when(platformFolder.exists()).thenReturn(false);
        when(platformContainer.getFolder(any(org.eclipse.core.runtime.Path.class))).thenReturn(platformFolder);

        IFile platformFile = mock(IFile.class);
        when(platformFile.getFullPath()).thenReturn(mock(IPath.class));
        when(platformContainer.getFile(any(org.eclipse.core.runtime.Path.class))).thenReturn(platformFile);

        EclipseResourceContainer container = new DummyEclipseResourceContainer(platformContainer);
        File file = container.getFile(new InMemoryPath("path/to/file"));

        assertTrue(file instanceof EclipseFile);
    }

    @Test
    public void getFolder_should_throw_when_file_already_exists()
    {
        IContainer platformContainer = mock(IContainer.class);
        when(platformContainer.getFullPath()).thenReturn(mock(IPath.class));

        IFile platformFile = mock(IFile.class);
        when(platformFile.exists()).thenReturn(true);
        when(platformContainer.getFile(any(org.eclipse.core.runtime.Path.class))).thenReturn(platformFile);

        EclipseResourceContainer container = new DummyEclipseResourceContainer(platformContainer);

        assertThrows(ResourceException.class, () -> container.getFolder(new InMemoryPath("path/to/folder")));
    }

    @Test
    public void getFolder_should_return_eclipse_folder_when_file_does_not_exist()
    {
        IContainer platformContainer = mock(IContainer.class);
        when(platformContainer.getFullPath()).thenReturn(mock(IPath.class));

        IFile platformFile = mock(IFile.class);
        when(platformFile.exists()).thenReturn(false);
        when(platformContainer.getFile(any(org.eclipse.core.runtime.Path.class))).thenReturn(platformFile);

        IFolder platformFolder = mock(IFolder.class);
        when(platformFolder.getFullPath()).thenReturn(mock(IPath.class));
        when(platformContainer.getFolder(any(org.eclipse.core.runtime.Path.class))).thenReturn(platformFolder);

        EclipseResourceContainer container = new DummyEclipseResourceContainer(platformContainer);
        Folder folder = container.getFolder(new InMemoryPath("path/to/folder"));

        assertTrue(folder instanceof EclipseFolder);
    }

    @Test
    public void listFiles_should_return_files() throws Exception
    {
        IContainer platformContainer = mock(IContainer.class);
        when(platformContainer.getFullPath()).thenReturn(mock(IPath.class));

        IFile file1 = mock(IFile.class);
        when(file1.getFullPath()).thenReturn(mock(IPath.class));
        IFile file2 = mock(IFile.class);
        when(file2.getFullPath()).thenReturn(mock(IPath.class));
        IFolder folder1 = mock(IFolder.class);

        IResource[] members = new IResource[] { file1, folder1, file2 };
        when(platformContainer.members()).thenReturn(members);

        EclipseResourceContainer container = new DummyEclipseResourceContainer(platformContainer);
        List<File> files = container.listFiles();

        assertEquals(2, files.size());
    }

    @Test
    public void listFolders_should_return_folders() throws Exception
    {
        IContainer platformContainer = mock(IContainer.class);
        when(platformContainer.getFullPath()).thenReturn(mock(IPath.class));

        IFile file1 = mock(IFile.class);
        IFolder folder1 = mock(IFolder.class);
        when(folder1.getFullPath()).thenReturn(mock(IPath.class));
        IFolder folder2 = mock(IFolder.class);
        when(folder2.getFullPath()).thenReturn(mock(IPath.class));

        IResource[] members = new IResource[] { folder1, file1, folder2 };
        when(platformContainer.members()).thenReturn(members);

        EclipseResourceContainer container = new DummyEclipseResourceContainer(platformContainer);
        List<Folder> folders = container.listFolders();

        assertEquals(2, folders.size());
    }

    @Test
    public void listResourcesOfType_should_throw_resource_exception_on_error() throws Exception
    {
        IContainer platformContainer = mock(IContainer.class);
        when(platformContainer.getFullPath()).thenReturn(mock(IPath.class));

        when(platformContainer.members()).thenThrow(new CoreException(mock(org.eclipse.core.runtime.IStatus.class)));

        EclipseResourceContainer container = new DummyEclipseResourceContainer(platformContainer);

        assertThrows(ResourceException.class, container::listFiles);
    }
}
