package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class ExtendedSafeRunnerTest {

    @Test
    void testApplyToSingle() {
        ExtendedSafeRunner runner = new ExtendedSafeRunner();
        String result = runner.applyTo("input", new ExtendedSafeRunner.GenericRunnable<String, String>() {
            @Override
            public void handleException(Throwable throwable, String element) {
            }

            @Override
            public String run(String element) throws Exception {
                return element + "-processed";
            }
        });

        assertThat(result).isEqualTo("input-processed");
    }

    @Test
    void testApplyToIterable() {
        ExtendedSafeRunner runner = new ExtendedSafeRunner();
        List<String> inputs = Arrays.asList("a", "b", "c");

        Iterable<String> results = runner.applyTo(inputs, new ExtendedSafeRunner.GenericRunnable<String, String>() {
            @Override
            public void handleException(Throwable throwable, String element) {
            }

            @Override
            public String run(String element) throws Exception {
                return element.toUpperCase();
            }
        });

        assertThat(results).containsExactly("A", "B", "C");
    }

    @Test
    void testApplyToExceptionHandled() {
        ExtendedSafeRunner runner = new ExtendedSafeRunner();

        final boolean[] handled = new boolean[1];

        String result = runner.applyTo("error", new ExtendedSafeRunner.GenericRunnable<String, String>() {
            @Override
            public void handleException(Throwable throwable, String element) {
                handled[0] = true;
                assertThat(element).isEqualTo("error");
            }

            @Override
            public String run(String element) throws Exception {
                throw new RuntimeException("test exception");
            }
        });

        assertThat(result).isNull(); // run failed, result not set
        assertThat(handled[0]).isTrue();
    }
}
