package org.moreunit.log;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class LogHandlerTest
{
    @Test
    public void should_get_instance()
    {
        assertNotNull(LogHandler.getInstance());
    }
}
