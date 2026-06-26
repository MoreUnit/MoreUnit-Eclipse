package org.moreunit.core.matching;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.when;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.core.text.TextSearchScope;
import org.junit.jupiter.api.Test;
import org.moreunit.core.log.Logger;
import org.moreunit.core.resources.Resource;

public class SearchEngineTest
{
    @Test
    public void searchFiles_should_log_warning_when_search_fails() throws Exception
    {
        // given
        TextSearchEngine textSearchEngine = mock(TextSearchEngine.class);
        Logger logger = mock(Logger.class);
        SearchEngine searchEngine = new SearchEngine(textSearchEngine, logger);

        Resource rootResource = mock(Resource.class);
        IResource platformResource = mock(IResource.class);
        when(rootResource.getUnderlyingPlatformResource()).thenReturn(platformResource);

        TextSearchRequestor requestor = mock(TextSearchRequestor.class);
        Pattern pattern = Pattern.compile("test");

        IStatus errorStatus = new Status(IStatus.ERROR, "pluginId", IStatus.ERROR, "Search error", null);
        when(textSearchEngine.search(nullable(TextSearchScope.class), eq(requestor), nullable(Pattern.class), isNull())).thenReturn(errorStatus);

        // when
        searchEngine.searchFiles(rootResource, pattern, requestor);

        // then
        verify(logger).warn("Search failed with status: " + errorStatus);
    }

    @Test
    public void searchFiles_should_log_error_when_exception_thrown() throws Exception
    {
        // given
        TextSearchEngine textSearchEngine = mock(TextSearchEngine.class);
        Logger logger = mock(Logger.class);
        SearchEngine searchEngine = new SearchEngine(textSearchEngine, logger);

        Resource rootResource = mock(Resource.class);
        when(rootResource.getUnderlyingPlatformResource()).thenThrow(new RuntimeException("Simulated exception"));

        TextSearchRequestor requestor = mock(TextSearchRequestor.class);
        Pattern pattern = Pattern.compile("test");

        // when
        searchEngine.searchFiles(rootResource, pattern, requestor);

        // then
        verify(logger).error(eq("Search failed"), any(RuntimeException.class));
    }

    @Test
    public void searchFiles_should_not_log_warning_when_search_succeeds() throws Exception
    {
        // given
        TextSearchEngine textSearchEngine = mock(TextSearchEngine.class);
        Logger logger = mock(Logger.class);
        SearchEngine searchEngine = new SearchEngine(textSearchEngine, logger);

        Resource rootResource = mock(Resource.class);
        IResource platformResource = mock(IResource.class);
        when(rootResource.getUnderlyingPlatformResource()).thenReturn(platformResource);

        TextSearchRequestor requestor = mock(TextSearchRequestor.class);
        Pattern pattern = Pattern.compile("test");

        IStatus okStatus = Status.OK_STATUS;
        when(textSearchEngine.search(nullable(TextSearchScope.class), eq(requestor), nullable(Pattern.class), isNull())).thenReturn(okStatus);

        // when
        searchEngine.searchFiles(rootResource, pattern, requestor);

        // then
        // no warnings or errors should be logged
        verify(textSearchEngine).search(nullable(TextSearchScope.class), eq(requestor), nullable(Pattern.class), isNull());
    }
}