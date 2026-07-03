package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.junit.jupiter.api.Test;

public class ConcreteSrcFileTest
{
    @Test
    public void should_create_from_ifile()
    {
        IFile ifile = mock(IFile.class);
        IPath path = mock(IPath.class);
        when(path.removeTrailingSeparator()).thenReturn(path);
        when(ifile.getFullPath()).thenReturn(path);

        ConcreteSrcFile srcFile = new ConcreteSrcFile(new EclipseFile(ifile));
        assertNotNull(srcFile);
    }
}
