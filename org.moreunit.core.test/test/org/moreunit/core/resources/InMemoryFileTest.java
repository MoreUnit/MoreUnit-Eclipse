package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InMemoryFileTest {

    @Test
    public void testGetBaseNameWithoutExtension() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertEquals(file.getBaseNameWithoutExtension(), "file");

        InMemoryFile fileNoExt = workspace.getProject("project").getFile("folder/file");
        assertEquals(fileNoExt.getBaseNameWithoutExtension(), "file");
    }

    @Test
    public void testGetExtension() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertEquals(file.getExtension(), "txt");

        InMemoryFile fileNoExt = workspace.getProject("project").getFile("folder/file");
        assertEquals(fileNoExt.getExtension(), "");
    }

    @Test
    public void testHasExtension() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertTrue(file.hasExtension());

        InMemoryFile fileNoExt = workspace.getProject("project").getFile("folder/file");
        assertFalse(fileNoExt.hasExtension());
    }

    @Test
    public void testGetProject() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryProject project = workspace.getProject("project");
        InMemoryFile file = project.getFile("folder/file.txt");

        assertSame(file.getProject(), project);
    }

    @Test
    public void testGetProjectPreferences() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertNull(file.getProjectPreferences());
    }

    @Test
    public void testGetUnderlyingPlatformFile() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertNull(file.getUnderlyingPlatformFile());
    }
}