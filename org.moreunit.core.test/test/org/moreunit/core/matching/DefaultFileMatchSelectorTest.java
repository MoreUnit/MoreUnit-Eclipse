package org.moreunit.core.matching;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.moreunit.core.log.Logger;

public class DefaultFileMatchSelectorTest
{
    @Test
    public void should_create_from_logger()
    {
        assertNotNull(new DefaultFileMatchSelector(mock(Logger.class)));
    }

    @Test
    public void should_fail_when_selecting_from_empty_collection()
    {
        // no dialog is opened: FileContentProvider has no default selection to propose
        final DefaultFileMatchSelector selector = new DefaultFileMatchSelector(mock(Logger.class));

        assertThrows(NoSuchElementException.class, () -> selector.select(emptyList(), null));
    }
}
