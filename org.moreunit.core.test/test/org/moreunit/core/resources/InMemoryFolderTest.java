package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class InMemoryFolderTest {

    @Test
    public void testGetProject() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryProject project = workspace.getProject("project");
        InMemoryFolder folder = project.getFolder("folder/subfolder");

        assertSame(folder.getProject(), project);
    }

    @Test
    public void testGetProjectPreferences() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryProject project = workspace.getProject("project");
        InMemoryFolder folder = project.getFolder("folder/subfolder");

        assertNull(folder.getProjectPreferences());
    }
}