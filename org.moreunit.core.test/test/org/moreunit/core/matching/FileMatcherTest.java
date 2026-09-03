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
        final SrcFile srcFile = mock(SrcFile.class);
        final SearchEngine searchEngine = mock(SearchEngine.class);
        final FileMatchSelector matchSelector = mock(FileMatchSelector.class);

        final FileMatcher matcher = new FileMatcher(srcFile, searchEngine, matchSelector);

        assertNotNull(matcher);
    }
}
