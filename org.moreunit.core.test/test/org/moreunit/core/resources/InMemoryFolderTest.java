package org.moreunit.core.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class InMemoryFolderTest {

    @Test
    public void testGetProject() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryProject project = workspace.getProject("project");
        InMemoryFolder folder = project.getFolder("folder/subfolder");

        assertThat(folder.getProject()).isSameAs(project);
    }

    @Test
    public void testGetProjectPreferences() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryProject project = workspace.getProject("project");
        InMemoryFolder folder = project.getFolder("folder/subfolder");

        assertThat(folder.getProjectPreferences()).isNull();
    }
}