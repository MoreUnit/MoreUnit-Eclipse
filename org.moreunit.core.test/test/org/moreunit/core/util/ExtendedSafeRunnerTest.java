package org.moreunit.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.moreunit.core.util.ExtendedSafeRunner.GenericRunnable;

public class ExtendedSafeRunnerTest
{
    @Test
    public void applyTo_should_execute_code_and_return_result()
    {
        ExtendedSafeRunner runner = new ExtendedSafeRunner();
        String result = runner.applyTo("input", new GenericRunnable<String, String>()
        {
            @Override
            public String run(String element) throws Exception
            {
                return element.toUpperCase();
            }

            @Override
            public void handleException(Throwable throwable, String element)
            {
            }
        });

        assertEquals(result, "INPUT");
    }

    @Test
    public void applyTo_iterable_should_execute_code_for_each_element()
    {
        ExtendedSafeRunner runner = new ExtendedSafeRunner();
        Iterable<String> results = runner.applyTo(Arrays.asList("a", "b"), new GenericRunnable<String, String>()
        {
            @Override
            public String run(String element) throws Exception
            {
                return element.toUpperCase();
            }

            @Override
            public void handleException(Throwable throwable, String element)
            {
            }
        });

        assertEquals(Arrays.asList("A", "B"), results);
    }

    @Test
    public void applyTo_should_handle_exception()
    {
        ExtendedSafeRunner runner = new ExtendedSafeRunner();
        final boolean[] exceptionHandled = { false };

        runner.applyTo("input", new GenericRunnable<String, String>()
        {
            @Override
            public String run(String element) throws Exception
            {
                throw new RuntimeException("failed");
            }

            @Override
            public void handleException(Throwable throwable, String element)
            {
                exceptionHandled[0] = true;
                assertEquals(element, "input");
                assertEquals(throwable.getMessage(), "failed");
            }
        });

        assertTrue(exceptionHandled[0]);
    }
}
