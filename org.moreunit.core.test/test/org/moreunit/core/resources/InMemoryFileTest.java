package org.moreunit.core.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class InMemoryFileTest {

    @Test
    public void testGetBaseNameWithoutExtension() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertThat(file.getBaseNameWithoutExtension()).isEqualTo("file");

        InMemoryFile fileNoExt = workspace.getProject("project").getFile("folder/file");
        assertThat(fileNoExt.getBaseNameWithoutExtension()).isEqualTo("file");
    }

    @Test
    public void testGetExtension() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertThat(file.getExtension()).isEqualTo("txt");

        InMemoryFile fileNoExt = workspace.getProject("project").getFile("folder/file");
        assertThat(fileNoExt.getExtension()).isEqualTo("");
    }

    @Test
    public void testHasExtension() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertThat(file.hasExtension()).isTrue();

        InMemoryFile fileNoExt = workspace.getProject("project").getFile("folder/file");
        assertThat(fileNoExt.hasExtension()).isFalse();
    }

    @Test
    public void testGetProject() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryProject project = workspace.getProject("project");
        InMemoryFile file = project.getFile("folder/file.txt");

        assertThat(file.getProject()).isSameAs(project);
    }

    @Test
    public void testGetProjectPreferences() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertThat(file.getProjectPreferences()).isNull();
    }

    @Test
    public void testGetUnderlyingPlatformFile() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertThat(file.getUnderlyingPlatformFile()).isNull();
    }
}