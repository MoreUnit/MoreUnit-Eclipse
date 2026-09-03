package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.moreunit.core.resources.Path;

public class DoesNotMatchConfigurationExceptionTest
{
    @Test
    public void should_store_path_in_exception()
    {
        // given
        final Path mockPath = mock(Path.class);

        // when
        final DoesNotMatchConfigurationException exception = new DoesNotMatchConfigurationException(mockPath);

        // then
        assertEquals(mockPath, exception.getPath());
    }
}