package org.moreunit.core.matching;

import org.junit.Test;

public class NameTokenizerTestCaseTest {

    @Test
    public void testNameTokenizerTestCase() throws Exception {
        NameTokenizer dummyTokenizer = new NameTokenizer() {
            @Override
            protected java.util.List<String> getWords(String name) {
                return java.util.Collections.singletonList(name);
            }
        };

        NameTokenizerTestCase testCase = new NameTokenizerTestCase() {
            @Override
            protected NameTokenizer getTokenizer() {
                return dummyTokenizer;
            }
        };

        boolean failed = false;
        try {
            testCase.should_reject_null_name();
        } catch (IllegalArgumentException e) {
            failed = true;
        }
        if (!failed) throw new AssertionError("Expected IllegalArgumentException");

        failed = false;
        try {
            testCase.should_reject_empty_name();
        } catch (IllegalArgumentException e) {
            failed = true;
        }
        if (!failed) throw new AssertionError("Expected IllegalArgumentException");

        failed = false;
        try {
            testCase.should_reject_blank_name();
        } catch (IllegalArgumentException e) {
            failed = true;
        }
        if (!failed) throw new AssertionError("Expected IllegalArgumentException");

        failed = false;
        try {
            testCase.should_reject_name_starting_with_space();
        } catch (IllegalArgumentException e) {
            failed = true;
        }
        if (!failed) throw new AssertionError("Expected IllegalArgumentException");

        failed = false;
        try {
            testCase.should_reject_name_ending_with_space();
        } catch (IllegalArgumentException e) {
            failed = true;
        }
        if (!failed) throw new AssertionError("Expected IllegalArgumentException");
    }
}
