package org.moreunit.core.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class InMemoryProjectTest {

    @Test
    public void testAddToParentAndGetName() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryProject project = new InMemoryProject("my-project", workspace);

        assertThat(project.getName()).isEqualTo("my-project");
        assertThat(project.getParent()).isSameAs(workspace);

        // Ensure it's in the workspace's projects map
        assertThat(workspace.getProject("my-project")).isSameAs(project);
    }
}