package org.moreunit.core.matching;

public class NameTokenizerTestCaseTest extends NameTokenizerTestCase {

    private final NameTokenizer dummyTokenizer = new NameTokenizer() {
        @Override
        protected java.util.List<String> getWords(String name) {
            return java.util.Collections.singletonList(name);
        }
    };

    @Override
    protected NameTokenizer getTokenizer() {
        return dummyTokenizer;
    }
}
