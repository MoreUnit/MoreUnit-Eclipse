package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class InMemoryProjectTest {

    @Test
    public void testAddToParentAndGetName() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryProject project = new InMemoryProject("my-project", workspace);

        assertEquals(project.getName(), "my-project");
        assertSame(project.getParent(), workspace);

        // Ensure it's in the workspace's projects map
        assertSame(workspace.getProject("my-project"), project);
    }
}