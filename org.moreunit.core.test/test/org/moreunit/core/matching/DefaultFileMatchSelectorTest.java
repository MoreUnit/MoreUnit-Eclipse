package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.moreunit.core.log.Logger;

public class DefaultFileMatchSelectorTest
{
    @Test
    public void should_create_from_logger()
    {
        assertNotNull(new DefaultFileMatchSelector(mock(Logger.class)));
    }
}
