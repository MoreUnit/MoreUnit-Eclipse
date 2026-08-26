package org.moreunit.core.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.junit.jupiter.api.Test;

class MatchResultTest {

    @Test
    void testGetUniqueMatchingFile_NoResults() {
        FileMatchCollector collector = mock(FileMatchCollector.class);
        when(collector.getResults()).thenReturn(Collections.emptySet());
        SourceFolderPath srcFolder = mock(SourceFolderPath.class);
        FileMatchSelector selector = mock(FileMatchSelector.class);

        MatchResult result = new MatchResult(collector, "PreferredFile", srcFolder, selector);

        MatchingFile matchingFile = result.getUniqueMatchingFile();
        assertThat(matchingFile.isFound()).isFalse();
        assertThat(matchingFile.getCorrespondingSrcFolder()).isSameAs(srcFolder);
        assertThat(matchingFile.getExpectedFileName()).isEqualTo("PreferredFile");
    }

    @Test
    void testGetUniqueMatchingFile_OneResult() {
        FileMatchCollector collector = mock(FileMatchCollector.class);
        IFile mockFile = mock(IFile.class);
        when(collector.getResults()).thenReturn(Collections.singleton(mockFile));
        SourceFolderPath srcFolder = mock(SourceFolderPath.class);
        FileMatchSelector selector = mock(FileMatchSelector.class);

        MatchResult result = new MatchResult(collector, "PreferredFile", srcFolder, selector);

        MatchingFile matchingFile = result.getUniqueMatchingFile();
        assertThat(matchingFile.isFound()).isTrue();
        assertThat(matchingFile.getFile()).isSameAs(mockFile);
    }

    @Test
    void testGetUniqueMatchingFile_MultipleResults_SelectionExists() {
        FileMatchCollector collector = mock(FileMatchCollector.class);
        Set<IFile> results = new HashSet<>();
        IFile mockFile1 = mock(IFile.class);
        IFile mockFile2 = mock(IFile.class);
        results.add(mockFile1);
        results.add(mockFile2);
        when(collector.getResults()).thenReturn(results);

        SourceFolderPath srcFolder = mock(SourceFolderPath.class);
        FileMatchSelector selector = mock(FileMatchSelector.class);
        MatchSelection selection = MatchSelection.file(mockFile1);
        when(selector.select(results, null)).thenReturn(selection);

        MatchResult result = new MatchResult(collector, "PreferredFile", srcFolder, selector);

        MatchingFile matchingFile = result.getUniqueMatchingFile();
        assertThat(matchingFile.isFound()).isTrue();
        assertThat(matchingFile.getFile()).isSameAs(mockFile1);
    }

    @Test
    void testGetUniqueMatchingFile_MultipleResults_NoSelection() {
        FileMatchCollector collector = mock(FileMatchCollector.class);
        Set<IFile> results = new HashSet<>();
        IFile mockFile1 = mock(IFile.class);
        IFile mockFile2 = mock(IFile.class);
        results.add(mockFile1);
        results.add(mockFile2);
        when(collector.getResults()).thenReturn(results);

        SourceFolderPath srcFolder = mock(SourceFolderPath.class);
        FileMatchSelector selector = mock(FileMatchSelector.class);
        MatchSelection selection = MatchSelection.none();
        when(selector.select(results, null)).thenReturn(selection);

        MatchResult result = new MatchResult(collector, "PreferredFile", srcFolder, selector);

        MatchingFile matchingFile = result.getUniqueMatchingFile();
        assertThat(matchingFile.isFound()).isFalse();
        assertThat(matchingFile.isSearchCancelled()).isTrue();
    }

    @Test
    void testMatchFound() {
        FileMatchCollector collector = mock(FileMatchCollector.class);
        SourceFolderPath srcFolder = mock(SourceFolderPath.class);
        FileMatchSelector selector = mock(FileMatchSelector.class);
        MatchResult result = new MatchResult(collector, "PreferredFile", srcFolder, selector);

        when(collector.getResults()).thenReturn(Collections.emptySet());
        assertThat(result.matchFound()).isFalse();

        when(collector.getResults()).thenReturn(Collections.singleton(mock(IFile.class)));
        assertThat(result.matchFound()).isTrue();
    }
}
