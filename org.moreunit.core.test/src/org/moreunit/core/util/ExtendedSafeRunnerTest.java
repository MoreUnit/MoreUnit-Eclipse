package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.moreunit.core.util.ExtendedSafeRunner.GenericRunnable;

class ExtendedSafeRunnerTest {

    @Test
    void testApplyToSingleElementSuccess() {
        ExtendedSafeRunner runner = new ExtendedSafeRunner();
        String element = "test";

        GenericRunnable<String, Integer> runnable = new GenericRunnable<String, Integer>() {
            @Override
            public void handleException(Throwable throwable, String element) {
                // not expected to be called
            }

            @Override
            public Integer run(String element) throws Exception {
                return element.length();
            }
        };

        Integer result = runner.applyTo(element, runnable);
        assertThat(result).isEqualTo(4);
    }

    @Test
    void testApplyToIterableSuccess() {
        ExtendedSafeRunner runner = new ExtendedSafeRunner();
        List<String> elements = Arrays.asList("a", "bc", "def");

        GenericRunnable<String, Integer> runnable = new GenericRunnable<String, Integer>() {
            @Override
            public void handleException(Throwable throwable, String element) {
                // not expected to be called
            }

            @Override
            public Integer run(String element) throws Exception {
                return element.length();
            }
        };

        Iterable<Integer> results = runner.applyTo(elements, runnable);

        List<Integer> resultList = new ArrayList<>();
        results.forEach(resultList::add);

        assertThat(resultList).containsExactly(1, 2, 3);
    }

    @Test
    void testApplyToExceptionHandling() {
        ExtendedSafeRunner runner = new ExtendedSafeRunner();
        String element = "test";

        List<Throwable> caughtExceptions = new ArrayList<>();
        List<String> caughtElements = new ArrayList<>();

        GenericRunnable<String, Integer> runnable = new GenericRunnable<String, Integer>() {
            @Override
            public void handleException(Throwable throwable, String element) {
                caughtExceptions.add(throwable);
                caughtElements.add(element);
            }

            @Override
            public Integer run(String element) throws Exception {
                throw new RuntimeException("Test exception");
            }
        };

        Integer result = runner.applyTo(element, runnable);

        assertThat(result).isNull(); // Since the exception was thrown, result is never set
        assertThat(caughtExceptions).hasSize(1);
        assertThat(caughtExceptions.get(0)).isInstanceOf(RuntimeException.class).hasMessage("Test exception");
        assertThat(caughtElements).containsExactly("test");
    }
}
