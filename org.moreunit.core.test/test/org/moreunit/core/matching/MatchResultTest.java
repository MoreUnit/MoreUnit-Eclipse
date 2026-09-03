package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MatchResultTest
{
    private FileMatchCollector matchCollector;
    private FileMatchSelector matchSelector;
    private SourceFolderPath correspondingSrcFolder;
    private String preferredFileName;

    @BeforeEach
    public void setUp()
    {
        matchCollector = mock(FileMatchCollector.class);
        matchSelector = mock(FileMatchSelector.class);
        correspondingSrcFolder = mock(SourceFolderPath.class);
        preferredFileName = "PreferredName.java";
    }

    @Test
    public void getUniqueMatchingFile_should_return_notFound_when_collector_has_no_results()
    {
        when(matchCollector.getResults()).thenReturn(Collections.emptySet());

        final MatchResult result = new MatchResult(matchCollector, preferredFileName, correspondingSrcFolder, matchSelector);
        final MatchingFile matchingFile = result.getUniqueMatchingFile();

        assertFalse(matchingFile.isFound());
        assertFalse(matchingFile.isSearchCancelled());
        assertSame(matchingFile.getSrcFolderToCreate(), correspondingSrcFolder);
        assertEquals(matchingFile.getFileToCreate(), preferredFileName);
    }

    @Test
    public void getUniqueMatchingFile_should_return_found_when_collector_has_exactly_one_result()
    {
        final IFile file = mock(IFile.class);
        when(matchCollector.getResults()).thenReturn(Collections.singleton(file));

        final MatchResult result = new MatchResult(matchCollector, preferredFileName, correspondingSrcFolder, matchSelector);
        final MatchingFile matchingFile = result.getUniqueMatchingFile();

        assertTrue(matchingFile.isFound());
        assertFalse(matchingFile.isSearchCancelled());
        assertSame(matchingFile.get(), file);
    }

    @Test
    public void getUniqueMatchingFile_should_return_found_when_selector_returns_valid_selection_for_multiple_results()
    {
        final IFile file1 = mock(IFile.class);
        final IFile file2 = mock(IFile.class);
        final IFile selectedFile = mock(IFile.class);
        final Set<IFile> results = new HashSet<>(Arrays.asList(file1, file2));

        when(matchCollector.getResults()).thenReturn(results);
        when(matchSelector.select(any(), isNull())).thenReturn(MatchSelection.file(selectedFile));

        final MatchResult result = new MatchResult(matchCollector, preferredFileName, correspondingSrcFolder, matchSelector);
        final MatchingFile matchingFile = result.getUniqueMatchingFile();

        assertTrue(matchingFile.isFound());
        assertFalse(matchingFile.isSearchCancelled());
        assertSame(matchingFile.get(), selectedFile);
    }

    @Test
    public void getUniqueMatchingFile_should_return_searchCancelled_when_selector_returns_none_for_multiple_results()
    {
        final IFile file1 = mock(IFile.class);
        final IFile file2 = mock(IFile.class);
        final Set<IFile> results = new HashSet<>(Arrays.asList(file1, file2));

        when(matchCollector.getResults()).thenReturn(results);
        when(matchSelector.select(any(), isNull())).thenReturn(MatchSelection.none());

        final MatchResult result = new MatchResult(matchCollector, preferredFileName, correspondingSrcFolder, matchSelector);
        final MatchingFile matchingFile = result.getUniqueMatchingFile();

        assertFalse(matchingFile.isFound());
        assertTrue(matchingFile.isSearchCancelled());
        assertNull(matchingFile.get());
    }

    @Test
    public void matchFound_should_return_false_when_results_are_empty()
    {
        when(matchCollector.getResults()).thenReturn(Collections.emptySet());

        final MatchResult result = new MatchResult(matchCollector, preferredFileName, correspondingSrcFolder, matchSelector);

        assertFalse(result.matchFound());
    }

    @Test
    public void matchFound_should_return_true_when_results_are_not_empty()
    {
        final IFile file = mock(IFile.class);
        when(matchCollector.getResults()).thenReturn(Collections.singleton(file));

        final MatchResult result = new MatchResult(matchCollector, preferredFileName, correspondingSrcFolder, matchSelector);

        assertTrue(result.matchFound());
    }
}
