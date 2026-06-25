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
        InMemoryWorkspace workspace = new InMemoryWorkspace();

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
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        assertNull(workspace.getPreferences());
    }

    @Test
    public void testToFile() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();

        IPath mockPath = mock(IPath.class);
        when(mockPath.toString()).thenReturn("/project/folder/file.txt");

        IFile mockPlatformFile = mock(IFile.class);
        when(mockPlatformFile.getFullPath()).thenReturn(mockPath);

        File file = workspace.toFile(mockPlatformFile);

        assertNotNull(file);
        assertEquals(file.getPath().toString(), "/project/folder/file.txt");
    }

    @Test
    public void testToSrcFile() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();

        IPath mockPath = mock(IPath.class);
        when(mockPath.toString()).thenReturn("/project/folder/file.txt");

        IFile mockPlatformFile = mock(IFile.class);
        when(mockPlatformFile.getFullPath()).thenReturn(mockPath);

        SrcFile srcFile = workspace.toSrcFile(mockPlatformFile);

        assertNotNull(srcFile);
        assertEquals(srcFile.getPath().toString(), "/project/folder/file.txt");
    }
}