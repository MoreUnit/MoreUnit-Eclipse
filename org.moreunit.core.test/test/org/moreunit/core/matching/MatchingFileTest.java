package org.moreunit.core.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.junit.jupiter.api.Test;

public class MatchingFileTest
{
    @Test
    public void found_should_initialize_properties_correctly()
    {
        IFile file = mock(IFile.class);
        when(file.toString()).thenReturn("L/mock/file");

        MatchingFile matchingFile = MatchingFile.found(file);

        assertThat(matchingFile.isFound()).isTrue();
        assertThat(matchingFile.isSearchCancelled()).isFalse();
        assertThat(matchingFile.get()).isSameAs(file);
        assertThat(matchingFile.getSrcFolderToCreate()).isNull();
        assertThat(matchingFile.getFileToCreate()).isNull();
        assertThat(matchingFile.toString()).isEqualTo("MatchingFile(found: L/mock/file)");
    }

    @Test
    public void notFound_should_initialize_properties_correctly()
    {
        SourceFolderPath srcFolder = mock(SourceFolderPath.class);
        when(srcFolder.toString()).thenReturn("src/folder");
        String fileToCreate = "NewFile.java";

        MatchingFile matchingFile = MatchingFile.notFound(srcFolder, fileToCreate);

        assertThat(matchingFile.isFound()).isFalse();
        assertThat(matchingFile.isSearchCancelled()).isFalse();
        assertThat(matchingFile.get()).isNull();
        assertThat(matchingFile.getSrcFolderToCreate()).isSameAs(srcFolder);
        assertThat(matchingFile.getFileToCreate()).isEqualTo(fileToCreate);
        assertThat(matchingFile.toString()).isEqualTo("MatchingFile(to create: src/folder/NewFile.java)");
    }

    @Test
    public void searchCancelled_should_initialize_properties_correctly()
    {
        MatchingFile matchingFile = MatchingFile.searchCancelled();

        assertThat(matchingFile.isFound()).isFalse();
        assertThat(matchingFile.isSearchCancelled()).isTrue();
        assertThat(matchingFile.get()).isNull();
        assertThat(matchingFile.getSrcFolderToCreate()).isNull();
        assertThat(matchingFile.getFileToCreate()).isNull();
        assertThat(matchingFile.toString()).isEqualTo("MatchingFile(search cancelled)");
    }
}
