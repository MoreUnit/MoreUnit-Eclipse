package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.junit.jupiter.api.Test;

public class MatchingFileTest
{
    @Test
    public void found_should_initialize_properties_correctly()
    {
        final IFile file = mock(IFile.class);
        when(file.toString()).thenReturn("L/mock/file");

        final MatchingFile matchingFile = MatchingFile.found(file);

        assertTrue(matchingFile.isFound());
        assertFalse(matchingFile.isSearchCancelled());
        assertSame(matchingFile.get(), file);
        assertNull(matchingFile.getSrcFolderToCreate());
        assertNull(matchingFile.getFileToCreate());
        assertEquals(matchingFile.toString(), "MatchingFile(found: L/mock/file)");
    }

    @Test
    public void notFound_should_initialize_properties_correctly()
    {
        final SourceFolderPath srcFolder = mock(SourceFolderPath.class);
        when(srcFolder.toString()).thenReturn("src/folder");
        final String fileToCreate = "NewFile.java";

        final MatchingFile matchingFile = MatchingFile.notFound(srcFolder, fileToCreate);

        assertFalse(matchingFile.isFound());
        assertFalse(matchingFile.isSearchCancelled());
        assertNull(matchingFile.get());
        assertSame(matchingFile.getSrcFolderToCreate(), srcFolder);
        assertEquals(matchingFile.getFileToCreate(), fileToCreate);
        assertEquals(matchingFile.toString(), "MatchingFile(to create: src/folder/NewFile.java)");
    }

    @Test
    public void searchCancelled_should_initialize_properties_correctly()
    {
        final MatchingFile matchingFile = MatchingFile.searchCancelled();

        assertFalse(matchingFile.isFound());
        assertTrue(matchingFile.isSearchCancelled());
        assertNull(matchingFile.get());
        assertNull(matchingFile.getSrcFolderToCreate());
        assertNull(matchingFile.getFileToCreate());
        assertEquals(matchingFile.toString(), "MatchingFile(search cancelled)");
    }
}
