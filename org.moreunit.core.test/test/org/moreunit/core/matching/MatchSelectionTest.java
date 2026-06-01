package org.moreunit.core.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFile;
import org.junit.jupiter.api.Test;

public class MatchSelectionTest
{
    @Test
    public void none_should_return_non_existing_selection_with_null_file()
    {
        MatchSelection selection = MatchSelection.none();

        assertThat(selection.exists()).isFalse();
        assertThat(selection.get()).isNull();
    }

    @Test
    public void file_should_return_existing_selection_with_given_file()
    {
        IFile mockFile = mock(IFile.class);
        MatchSelection selection = MatchSelection.file(mockFile);

        assertThat(selection.exists()).isTrue();
        assertThat(selection.get()).isSameAs(mockFile);
    }
}
