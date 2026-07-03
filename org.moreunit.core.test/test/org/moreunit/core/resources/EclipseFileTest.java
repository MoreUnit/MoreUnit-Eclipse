package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.junit.jupiter.api.Test;

public class EclipseFileTest
{
    @Test
    public void should_create_file_from_ifile()
    {
        IFile ifile = mock(IFile.class);
        IPath path = mock(IPath.class);
        when(path.removeTrailingSeparator()).thenReturn(path);
        when(path.toString()).thenReturn("/proj/src/Foo.java");
        when(ifile.getFullPath()).thenReturn(path);

        EclipseFile file = new EclipseFile(ifile);
        assertNotNull(file);
    }

    @Test
    public void get_extension_should_return_file_extension()
    {
        IFile ifile = mock(IFile.class);
        IPath path = mock(IPath.class);
        when(path.removeTrailingSeparator()).thenReturn(path);
        when(ifile.getFullPath()).thenReturn(path);
        when(ifile.getFileExtension()).thenReturn("java");

        EclipseFile file = new EclipseFile(ifile);
        assertEquals("java", file.getExtension());
    }

    @Test
    public void has_extension_should_check_file_extension()
    {
        IFile ifile = mock(IFile.class);
        IPath path = mock(IPath.class);
        when(path.removeTrailingSeparator()).thenReturn(path);
        when(ifile.getFullPath()).thenReturn(path);
        when(ifile.getFileExtension()).thenReturn("java");

        assertTrue(new EclipseFile(ifile).hasExtension());

        when(ifile.getFileExtension()).thenReturn(null);
        assertFalse(new EclipseFile(ifile).hasExtension());
    }
}
