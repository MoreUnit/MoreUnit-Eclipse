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
        final InMemoryWorkspace workspace = new InMemoryWorkspace();
        final InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertEquals(file.getBaseNameWithoutExtension(), "file");

        final InMemoryFile fileNoExt = workspace.getProject("project").getFile("folder/file");
        assertEquals(fileNoExt.getBaseNameWithoutExtension(), "file");
    }

    @Test
    public void testGetExtension() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();
        final InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertEquals(file.getExtension(), "txt");

        final InMemoryFile fileNoExt = workspace.getProject("project").getFile("folder/file");
        assertEquals(fileNoExt.getExtension(), "");
    }

    @Test
    public void testHasExtension() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();
        final InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertTrue(file.hasExtension());

        final InMemoryFile fileNoExt = workspace.getProject("project").getFile("folder/file");
        assertFalse(fileNoExt.hasExtension());
    }

    @Test
    public void testGetProject() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();
        final InMemoryProject project = workspace.getProject("project");
        final InMemoryFile file = project.getFile("folder/file.txt");

        assertSame(file.getProject(), project);
    }

    @Test
    public void testGetProjectPreferences() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();
        final InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertNull(file.getProjectPreferences());
    }

    @Test
    public void testGetUnderlyingPlatformFile() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();
        final InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertNull(file.getUnderlyingPlatformFile());
    }
}