package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.junit.jupiter.api.Test;

public class EclipseFolderTest
{
    @Test
    public void should_create_folder_from_ifolder()
    {
        IFolder ifolder = mock(IFolder.class);
        IPath path = mock(IPath.class);
        when(path.removeTrailingSeparator()).thenReturn(path);
        when(ifolder.getFullPath()).thenReturn(path);

        IProject project = mock(IProject.class);
        when(ifolder.getProject()).thenReturn(project);

        EclipseFolder folder = new EclipseFolder(ifolder);

        assertNotNull(folder.getPath());
    }

    @Test
    public void get_project_should_return_eclipse_project()
    {
        IFolder ifolder = mock(IFolder.class);
        IPath path = mock(IPath.class);
        when(path.removeTrailingSeparator()).thenReturn(path);
        when(ifolder.getFullPath()).thenReturn(path);

        IProject project = mock(IProject.class);
        when(project.getFullPath()).thenReturn(path);
        when(ifolder.getProject()).thenReturn(project);

        EclipseFolder folder = new EclipseFolder(ifolder);

        assertNotNull(folder.getProject());
    }
}
