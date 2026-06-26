package org.moreunit.core.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IFile;
import org.junit.jupiter.api.Test;
import org.moreunit.core.resources.Folder;
import org.moreunit.core.resources.Path;
import org.moreunit.core.resources.Project;
import org.moreunit.core.resources.Workspace;

class SourceFolderPathTest {

    @Test
    void testIsResolved() {
        Workspace workspace = mock(Workspace.class);
        when(workspace.path("src/main/java")).thenReturn(new Path("src/main/java"));
        when(workspace.path("src/*/java")).thenReturn(new Path("src/*/java"));

        SourceFolderPath resolvedPath = new SourceFolderPath("src/main/java", workspace);
        assertThat(resolvedPath.isResolved()).isTrue();

        SourceFolderPath unresolvedPath = new SourceFolderPath("src/*/java", workspace);
        assertThat(unresolvedPath.isResolved()).isFalse();
    }

    @Test
    void testGetResolvedPart() {
        Workspace workspace = mock(Workspace.class);
        when(workspace.path("src/main/java")).thenReturn(new Path("src/main/java"));
        when(workspace.path("src/*/java")).thenReturn(new Path("src/*/java"));
        when(workspace.path("src/main/[^a-z]")).thenReturn(new Path("src/main/[^a-z]"));

        SourceFolderPath resolvedPath = new SourceFolderPath("src/main/java", workspace);
        assertThat(resolvedPath.getResolvedPart().toString()).isEqualTo("src/main/java");

        SourceFolderPath unresolvedPath = new SourceFolderPath("src/*/java", workspace);
        assertThat(unresolvedPath.getResolvedPart().toString()).isEqualTo("src");

        SourceFolderPath unresolvedPath2 = new SourceFolderPath("src/main/[^a-z]", workspace);
        assertThat(unresolvedPath2.getResolvedPart().toString()).isEqualTo("src/main");
    }

    @Test
    void testGetResolvedPartAsResourceProject() {
        Workspace workspace = mock(Workspace.class);
        when(workspace.path("MyProject")).thenReturn(new Path("MyProject"));

        Project mockProject = mock(Project.class);
        when(workspace.getProject("MyProject")).thenReturn(mockProject);

        SourceFolderPath path = new SourceFolderPath("MyProject", workspace);
        assertThat(path.getResolvedPartAsResource()).isSameAs(mockProject);
    }

    @Test
    void testGetResolvedPartAsResourceFolder() {
        Workspace workspace = mock(Workspace.class);
        when(workspace.path("MyProject/src")).thenReturn(new Path("MyProject/src"));

        Folder mockFolder = mock(Folder.class);
        when(workspace.getFolder(any(Path.class))).thenReturn(mockFolder);

        SourceFolderPath path = new SourceFolderPath("MyProject/src", workspace);
        assertThat(path.getResolvedPartAsResource()).isSameAs(mockFolder);
    }

    @Test
    void testMatches() {
        Workspace workspace = mock(Workspace.class);
        when(workspace.path("MyProject/src/.*")).thenReturn(new Path("MyProject/src/.*"));

        SourceFolderPath path = new SourceFolderPath("MyProject/src/.*", workspace);

        IFile mockFile = mock(IFile.class);
        org.eclipse.core.runtime.IPath mockPath = mock(org.eclipse.core.runtime.IPath.class);
        org.eclipse.core.runtime.IPath mockRemovedPath = mock(org.eclipse.core.runtime.IPath.class);
        org.eclipse.core.runtime.IPath mockFinalPath = mock(org.eclipse.core.runtime.IPath.class);

        when(mockFile.getFullPath()).thenReturn(mockPath);
        when(mockPath.removeLastSegments(1)).thenReturn(mockRemovedPath);
        when(mockRemovedPath.removeTrailingSeparator()).thenReturn(mockFinalPath);
        when(mockFinalPath.toString()).thenReturn("/MyProject/src/main");

        assertThat(path.matches(mockFile)).isTrue();
    }
}
