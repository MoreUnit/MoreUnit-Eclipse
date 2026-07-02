package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.junit.jupiter.api.Test;

public class FolderCreationExceptionTest
{
    @Test
    public void should_wrap_core_exception_and_expose_folder()
    {
        CoreException cause = mock(CoreException.class);
        IFolder folder = mock(IFolder.class);

        FolderCreationException exception = new FolderCreationException(cause, folder);

        assertSame(folder, exception.getFolder());
        assertNotNull(exception.getCause());
    }

    @Test
    public void should_create_exception_without_folder()
    {
        CoreException cause = mock(CoreException.class);

        FolderCreationException exception = new FolderCreationException(cause, null);

        assertNotNull(exception);
    }
}
