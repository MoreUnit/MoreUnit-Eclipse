package org.moreunit.core.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.junit.jupiter.api.Test;

public class FileContentProviderTest
{
    @Test
    public void should_provide_files_as_elements()
    {
        final IFile a = mock(IFile.class);
        final IFile b = mock(IFile.class);

        final FileContentProvider provider = new FileContentProvider(List.of(a, b), null);

        final Object[] elements = provider.getElements(null);
        assertEquals(2, elements.length);
    }

    @Test
    public void should_use_preferred_file_as_default_selection()
    {
        final IFile a = mock(IFile.class);
        final IFile preferred = mock(IFile.class);

        final FileContentProvider provider = new FileContentProvider(List.of(a, preferred), preferred);

        assertNotNull(provider.getDefaultSelection());
    }

    @Test
    public void should_have_no_children()
    {
        final IFile file = mock(IFile.class);

        final FileContentProvider provider = new FileContentProvider(List.of(file), null);

        assertNull(provider.getChildren(file));
        assertFalse(provider.hasChildren(file));
    }

    @Test
    public void should_have_no_parent()
    {
        final IFile file = mock(IFile.class);

        final FileContentProvider provider = new FileContentProvider(List.of(file), null);

        assertNull(provider.getParent(file));
    }
}
