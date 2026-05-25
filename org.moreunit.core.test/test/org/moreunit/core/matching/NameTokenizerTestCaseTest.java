package org.moreunit.core.matching;

import org.junit.jupiter.api.Test;

public class NameTokenizerTestCaseTest {

    private final NameTokenizer dummyTokenizer = new NameTokenizer() {
        @Override
        protected java.util.List<String> getWords(String name) {
            return java.util.Collections.singletonList(name);
        }
    };

    @Test
    public void testNameTokenizerTestCase() throws Exception {
        assertThrowsIllegalArgument(() -> dummyTokenizer.tokenize(null));
        assertThrowsIllegalArgument(() -> dummyTokenizer.tokenize(""));
        assertThrowsIllegalArgument(() -> dummyTokenizer.tokenize("  "));
        assertThrowsIllegalArgument(() -> dummyTokenizer.tokenize(" name"));
        assertThrowsIllegalArgument(() -> dummyTokenizer.tokenize("name "));
    }

    private void assertThrowsIllegalArgument(Runnable r) {
        try {
            r.run();
        } catch (IllegalArgumentException e) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }
}
