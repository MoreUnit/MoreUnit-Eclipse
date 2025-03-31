package org.moreunit.core.ui;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

import org.eclipse.core.resources.IFile;
import org.junit.Test;
import org.moreunit.core.decorators.TestedFileDecorator;
import org.moreunit.core.matching.MatchingFile;
import org.moreunit.core.resources.SrcFile;

public class MarkCorrespondingFileAsTestedIfRequiredTest
{
    static final MatchingFile NO_MATCHING_FILE = MatchingFile.notFound(null, null);

    SrcFile createdFile = mock(SrcFile.class);
    TestedFileDecorator decoratorChangeNotifier = mock(TestedFileDecorator.class);
    IFile correspondingFile = mock(IFile.class);

    FileCreationListener listener = new MarkCorrespondingFileAsTestedIfRequired(decoratorChangeNotifier);

    @Test
    public void should_do_nothing_when_created_file_is_not_a_test_file() throws Exception
    {
        given(createdFile.isTestFile()).willReturn(false);

        // when
        listener.fileCreated(createdFile);

        // then
        verifyNoInteractions(decoratorChangeNotifier);
    }

    @Test
    public void should_do_nothing_when_created_file_is_a_test_file_but_no_corresponding_file_is_found() throws Exception
    {
        given(createdFile.isTestFile()).willReturn(true);
        given(createdFile.findUniqueMatch()).willReturn(NO_MATCHING_FILE);

        // when
        listener.fileCreated(createdFile);

        // then
        verifyNoInteractions(decoratorChangeNotifier);
    }

    @Test
    public void should_notify_decorator_when_created_file_is_a_test_file_and_a_corresponding_file_is_found() throws Exception
    {
        given(createdFile.isTestFile()).willReturn(true);
        given(createdFile.findUniqueMatch()).willReturn(MatchingFile.found(correspondingFile));

        // when
        listener.fileCreated(createdFile);

        // then
        verify(decoratorChangeNotifier).refreshIndicatorFor(correspondingFile);
    }
}
