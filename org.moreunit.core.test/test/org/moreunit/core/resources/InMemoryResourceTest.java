package org.moreunit.core.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class InMemoryResourceTest {

    @Test
    public void testEqualsAndHashCode() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file1 = workspace.getProject("project").getFile("folder/file.txt");
        InMemoryFile file2 = workspace.getProject("project").getFile("folder/file.txt");
        InMemoryFile file3 = workspace.getProject("project").getFile("folder/other.txt");

        assertThat(file1).isEqualTo(file2);
        assertThat(file1).isNotEqualTo(file3);
        assertThat(file1).isNotEqualTo(null);
        assertThat(file1).isNotEqualTo(new Object());
        assertThat(file1).isEqualTo(file1);

        assertThat(file1.hashCode()).isEqualTo(file2.hashCode());
        assertThat(file1.hashCode()).isNotEqualTo(file3.hashCode());
    }

    @Test
    public void testToString() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertThat(file.toString()).isEqualTo("/project/folder/file.txt");
    }

    @Test
    public void testGetUnderlyingPlatformResource() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertThat(file.getUnderlyingPlatformResource()).isNull();
    }

    @Test
    public void testExistsCreateDelete() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");

        assertThat(file.exists()).isFalse();

        file.create();
        assertThat(file.exists()).isTrue();

        file.delete();
        assertThat(file.exists()).isFalse();
    }

    @Test
    public void testGetName() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertThat(file.getName()).isEqualTo("file.txt");
    }

    @Test
    public void testGetPathAndParent() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryProject project = workspace.getProject("project");
        InMemoryFolder folder = project.getFolder("folder");
        InMemoryFile file = folder.getFile("file.txt");

        assertThat(file.getPath().toString()).isEqualTo("/project/folder/file.txt");
        assertThat(file.getParent()).isSameAs(folder);
        assertThat(folder.getParent()).isSameAs(project);
    }
}