package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.moreunit.core.resources.SrcFile;

public class FileMatcherTest
{
    @Test
    public void should_create_from_src_file_search_engine_and_selector()
    {
        SrcFile srcFile = mock(SrcFile.class);
        SearchEngine searchEngine = mock(SearchEngine.class);
        FileMatchSelector matchSelector = mock(FileMatchSelector.class);

        FileMatcher matcher = new FileMatcher(srcFile, searchEngine, matchSelector);

        assertNotNull(matcher);
    }
}
