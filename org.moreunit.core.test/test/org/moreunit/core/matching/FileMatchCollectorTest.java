package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.junit.jupiter.api.Test;

public class FileMatchCollectorTest
{
    @Test
    public void acceptFile_should_return_true_when_search_is_over() throws Exception
    {
        // given
        SourceFolderPath correspondingSrcFolder = mock(SourceFolderPath.class);
        when(correspondingSrcFolder.isResolved()).thenReturn(true);

        TestFileMatchCollector collector = new TestFileMatchCollector(correspondingSrcFolder);
        collector.setSearchIsOver(true);

        IFile file = mock(IFile.class);

        // when
        boolean result = collector.acceptFile(file);

        // then
        assertTrue(result);
        assertTrue(collector.getResults().isEmpty());
    }

    @Test
    public void acceptFile_should_return_false_and_collect_file_when_it_matches() throws Exception
    {
        // given
        SourceFolderPath correspondingSrcFolder = mock(SourceFolderPath.class);
        when(correspondingSrcFolder.isResolved()).thenReturn(false);

        TestFileMatchCollector collector = new TestFileMatchCollector(correspondingSrcFolder);
        collector.setSearchIsOver(false);

        IFile file = mock(IFile.class);
        when(correspondingSrcFolder.matches(file)).thenReturn(true);

        // when
        boolean result = collector.acceptFile(file);

        // then
        assertFalse(result);
        assertEquals(1, collector.getResults().size());
        assertTrue(collector.getResults().contains(file));
        assertTrue(collector.isMatchFoundCalled());
        verify(correspondingSrcFolder).matches(file);
    }

    @Test
    public void acceptFile_should_return_false_and_not_collect_file_when_it_does_not_match() throws Exception
    {
        // given
        SourceFolderPath correspondingSrcFolder = mock(SourceFolderPath.class);
        when(correspondingSrcFolder.isResolved()).thenReturn(false);

        TestFileMatchCollector collector = new TestFileMatchCollector(correspondingSrcFolder);
        collector.setSearchIsOver(false);

        IFile file = mock(IFile.class);
        when(correspondingSrcFolder.matches(file)).thenReturn(false);

        // when
        boolean result = collector.acceptFile(file);

        // then
        assertFalse(result);
        assertTrue(collector.getResults().isEmpty());
        assertFalse(collector.isMatchFoundCalled());
        verify(correspondingSrcFolder).matches(file);
    }

    @Test
    public void acceptFile_should_return_false_and_collect_file_when_folder_is_resolved() throws Exception
    {
        // given
        SourceFolderPath correspondingSrcFolder = mock(SourceFolderPath.class);
        when(correspondingSrcFolder.isResolved()).thenReturn(true);

        TestFileMatchCollector collector = new TestFileMatchCollector(correspondingSrcFolder);
        collector.setSearchIsOver(false);

        IFile file = mock(IFile.class);

        // when
        boolean result = collector.acceptFile(file);

        // then
        assertFalse(result);
        assertEquals(1, collector.getResults().size());
        assertTrue(collector.getResults().contains(file));
        assertTrue(collector.isMatchFoundCalled());

        // When correspondingSrcFolder is resolved, matches() shouldn't be called
        verify(correspondingSrcFolder).isResolved();
        verifyNoMoreInteractions(correspondingSrcFolder);
    }

    private static class TestFileMatchCollector extends FileMatchCollector
    {
        private boolean searchIsOver = false;
        private boolean matchFoundCalled = false;

        public TestFileMatchCollector(SourceFolderPath correspondingSrcFolder)
        {
            super(correspondingSrcFolder);
        }

        public void setSearchIsOver(boolean searchIsOver)
        {
            this.searchIsOver = searchIsOver;
        }

        @Override
        protected boolean searchIsOver()
        {
            return searchIsOver;
        }

        @Override
        protected void matchFound(IFile file)
        {
            matchFoundCalled = true;
        }

        public boolean isMatchFoundCalled()
        {
            return matchFoundCalled;
        }
    }
}
