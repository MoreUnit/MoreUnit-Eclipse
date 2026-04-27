package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;
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

        assertThat(result).isEqualTo("INPUT");
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

        assertThat(results).containsExactly("A", "B");
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
                assertThat(element).isEqualTo("input");
                assertThat(throwable.getMessage()).isEqualTo("failed");
            }
        });

        assertThat(exceptionHandled[0]).isTrue();
    }
}
