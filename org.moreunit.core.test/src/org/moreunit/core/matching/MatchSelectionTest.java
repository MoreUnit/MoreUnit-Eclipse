package org.moreunit.core.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFile;
import org.junit.jupiter.api.Test;

class MatchSelectionTest {

    @Test
    void testFileSelection() {
        IFile mockFile = mock(IFile.class);
        MatchSelection selection = MatchSelection.file(mockFile);

        assertThat(selection.exists()).isTrue();
        assertThat(selection.get()).isSameAs(mockFile);
    }

    @Test
    void testNoneSelection() {
        MatchSelection selection = MatchSelection.none();

        assertThat(selection.exists()).isFalse();
        assertThat(selection.get()).isNull();
    }
}
