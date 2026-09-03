package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.junit.jupiter.api.Test;

public class MatchStrategyTest {

    @Test
    public void testAllMatchesStrategy() throws CoreException {
        final SourceFolderPath mockFolder = mock(SourceFolderPath.class);
        when(mockFolder.isResolved()).thenReturn(true);
        final FileMatchCollector collector = MatchStrategy.ALL_MATCHES.createMatchCollector(mockFolder);

        // acceptFile returns false to continue searching
        final boolean stopSearch1 = collector.acceptFile(mock(IFile.class));
        final boolean stopSearch2 = collector.acceptFile(mock(IFile.class));

        assertFalse(stopSearch1);
        assertFalse(stopSearch2);
        assertEquals(2, collector.getResults().size());
    }

    @Test
    public void testAnyMatchStrategy() throws CoreException {
        final SourceFolderPath mockFolder = mock(SourceFolderPath.class);
        when(mockFolder.isResolved()).thenReturn(true);
        final FileMatchCollector collector = MatchStrategy.ANY_MATCH.createMatchCollector(mockFolder);

        // First match found, continue searching from the perspective of this call returning false,
        // but internal state marks it as found.
        final boolean stopSearch1 = collector.acceptFile(mock(IFile.class));
        assertFalse(stopSearch1);
        assertEquals(1, collector.getResults().size());

        // Second match should be ignored, and return true to stop searching
        final boolean stopSearch2 = collector.acceptFile(mock(IFile.class));
        assertTrue(stopSearch2);
        // Size remains 1
        assertEquals(1, collector.getResults().size());
    }
}
