package org.moreunit.core.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.junit.jupiter.api.Test;

class FileMatchCollectorTest {

    @Test
    void testAcceptFile_SearchNotOver_Matches() throws CoreException {
        SourceFolderPath srcFolder = mock(SourceFolderPath.class);
        when(srcFolder.isResolved()).thenReturn(true);
        when(srcFolder.matches(any(IFile.class))).thenReturn(true);

        TestFileMatchCollector collector = new TestFileMatchCollector(srcFolder, false);
        IFile mockFile = mock(IFile.class);

        boolean result = collector.acceptFile(mockFile);

        assertThat(result).isFalse();
        assertThat(collector.getResults()).containsExactly(mockFile);
        assertThat(collector.isMatchFoundCalled()).isTrue();
    }

    @Test
    void testAcceptFile_SearchNotOver_DoesNotMatch() throws CoreException {
        SourceFolderPath srcFolder = mock(SourceFolderPath.class);
        when(srcFolder.isResolved()).thenReturn(true);
        when(srcFolder.matches(any(IFile.class))).thenReturn(false);

        TestFileMatchCollector collector = new TestFileMatchCollector(srcFolder, false);
        IFile mockFile = mock(IFile.class);

        boolean result = collector.acceptFile(mockFile);

        assertThat(result).isFalse();
        assertThat(collector.getResults()).isEmpty();
        assertThat(collector.isMatchFoundCalled()).isFalse();
    }

    @Test
    void testAcceptFile_SearchIsOver() throws CoreException {
        SourceFolderPath srcFolder = mock(SourceFolderPath.class);
        when(srcFolder.isResolved()).thenReturn(true);

        TestFileMatchCollector collector = new TestFileMatchCollector(srcFolder, true);
        IFile mockFile = mock(IFile.class);

        boolean result = collector.acceptFile(mockFile);

        assertThat(result).isTrue();
        assertThat(collector.getResults()).isEmpty();
        assertThat(collector.isMatchFoundCalled()).isFalse();
    }

    @Test
    void testAcceptFile_UnresolvedFolder_Matches() throws CoreException {
        SourceFolderPath srcFolder = mock(SourceFolderPath.class);
        when(srcFolder.isResolved()).thenReturn(false);
        // when folder is not resolved, checkFolder is true, so it checks srcFolder.matches(file)
        when(srcFolder.matches(any(IFile.class))).thenReturn(true);

        TestFileMatchCollector collector = new TestFileMatchCollector(srcFolder, false);
        IFile mockFile = mock(IFile.class);

        boolean result = collector.acceptFile(mockFile);

        assertThat(result).isFalse();
        assertThat(collector.getResults()).containsExactly(mockFile);
        assertThat(collector.isMatchFoundCalled()).isTrue();
    }

    private static class TestFileMatchCollector extends FileMatchCollector {
        private final boolean searchOver;
        private boolean matchFoundCalled = false;

        protected TestFileMatchCollector(SourceFolderPath correspondingSrcFolder, boolean searchOver) {
            super(correspondingSrcFolder);
            this.searchOver = searchOver;
        }

        @Override
        protected boolean searchIsOver() {
            return searchOver;
        }

        @Override
        protected void matchFound(IFile file) {
            matchFoundCalled = true;
        }

        public boolean isMatchFoundCalled() {
            return matchFoundCalled;
        }
    }
}
