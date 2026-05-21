package org.moreunit.core.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.junit.Test;

public class InMemoryWorkspaceTest {

    @Test
    public void testCreateDeleteExists() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();

        // Exists is always true
        assertThat(workspace.exists()).isTrue();

        // These don't change exists status
        workspace.create();
        assertThat(workspace.exists()).isTrue();

        workspace.delete();
        assertThat(workspace.exists()).isTrue();
    }

    @Test
    public void testGetPreferences() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        assertThat(workspace.getPreferences()).isNull();
    }

    @Test
    public void testToFile() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();

        IPath mockPath = mock(IPath.class);
        when(mockPath.toString()).thenReturn("/project/folder/file.txt");

        IFile mockPlatformFile = mock(IFile.class);
        when(mockPlatformFile.getFullPath()).thenReturn(mockPath);

        File file = workspace.toFile(mockPlatformFile);

        assertThat(file).isNotNull();
        assertThat(file.getPath().toString()).isEqualTo("/project/folder/file.txt");
    }

    @Test
    public void testToSrcFile() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();

        IPath mockPath = mock(IPath.class);
        when(mockPath.toString()).thenReturn("/project/folder/file.txt");

        IFile mockPlatformFile = mock(IFile.class);
        when(mockPlatformFile.getFullPath()).thenReturn(mockPath);

        SrcFile srcFile = workspace.toSrcFile(mockPlatformFile);

        assertThat(srcFile).isNotNull();
        assertThat(srcFile.getPath().toString()).isEqualTo("/project/folder/file.txt");
    }
}