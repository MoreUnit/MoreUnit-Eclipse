package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.junit.jupiter.api.Test;

public class InMemoryWorkspaceTest {

    @Test
    public void testCreateDeleteExists() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();

        // Exists is always true
        assertTrue(workspace.exists());

        // These don't change exists status
        workspace.create();
        assertTrue(workspace.exists());

        workspace.delete();
        assertTrue(workspace.exists());
    }

    @Test
    public void testGetPreferences() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();
        assertNull(workspace.getPreferences());
    }

    @Test
    public void testToFile() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();

        final IPath mockPath = mock(IPath.class);
        when(mockPath.toString()).thenReturn("/project/folder/file.txt");

        final IFile mockPlatformFile = mock(IFile.class);
        when(mockPlatformFile.getFullPath()).thenReturn(mockPath);

        final File file = workspace.toFile(mockPlatformFile);

        assertNotNull(file);
        assertEquals(file.getPath().toString(), "/project/folder/file.txt");
    }

    @Test
    public void testToSrcFile() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();

        final IPath mockPath = mock(IPath.class);
        when(mockPath.toString()).thenReturn("/project/folder/file.txt");

        final IFile mockPlatformFile = mock(IFile.class);
        when(mockPlatformFile.getFullPath()).thenReturn(mockPath);

        final SrcFile srcFile = workspace.toSrcFile(mockPlatformFile);

        assertNotNull(srcFile);
        assertEquals(srcFile.getPath().toString(), "/project/folder/file.txt");
    }
}