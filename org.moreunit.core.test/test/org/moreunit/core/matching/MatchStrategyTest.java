package org.moreunit.core.matching;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

public class MatchStrategyTest {

    @Test
    public void testAllMatchesStrategy() throws CoreException {
        SourceFolderPath mockFolder = mock(SourceFolderPath.class);
        when(mockFolder.isResolved()).thenReturn(true);
        FileMatchCollector collector = MatchStrategy.ALL_MATCHES.createMatchCollector(mockFolder);

        assertFalse(collector.searchIsOver());
        collector.acceptFile(mock(IFile.class));
        assertFalse(collector.searchIsOver());
    }

    @Test
    public void testAnyMatchStrategy() throws CoreException {
        SourceFolderPath mockFolder = mock(SourceFolderPath.class);
        when(mockFolder.isResolved()).thenReturn(true);
        FileMatchCollector collector = MatchStrategy.ANY_MATCH.createMatchCollector(mockFolder);

        assertFalse(collector.searchIsOver());

        collector.acceptFile(mock(IFile.class));
        assertTrue(collector.searchIsOver());
    }
}
