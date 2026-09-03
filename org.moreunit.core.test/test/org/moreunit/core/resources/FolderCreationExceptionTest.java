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
        final CoreException cause = mock(CoreException.class);
        final IFolder folder = mock(IFolder.class);

        final FolderCreationException exception = new FolderCreationException(cause, folder);

        assertSame(folder, exception.getFolder());
        assertNotNull(exception.getCause());
    }

    @Test
    public void should_create_exception_without_folder()
    {
        final CoreException cause = mock(CoreException.class);

        final FolderCreationException exception = new FolderCreationException(cause, null);

        assertNotNull(exception);
    }
}
