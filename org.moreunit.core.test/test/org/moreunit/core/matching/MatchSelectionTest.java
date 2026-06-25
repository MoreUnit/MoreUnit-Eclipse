package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFile;
import org.junit.jupiter.api.Test;

public class MatchSelectionTest
{
    @Test
    public void none_should_return_non_existing_selection_with_null_file()
    {
        MatchSelection selection = MatchSelection.none();

        assertFalse(selection.exists());
        assertNull(selection.get());
    }

    @Test
    public void file_should_return_existing_selection_with_given_file()
    {
        IFile mockFile = mock(IFile.class);
        MatchSelection selection = MatchSelection.file(mockFile);

        assertTrue(selection.exists());
        assertSame(selection.get(), mockFile);
    }
}
