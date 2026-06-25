package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InMemoryResourceTest {

    @Test
    public void testEqualsAndHashCode() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file1 = workspace.getProject("project").getFile("folder/file.txt");
        InMemoryFile file2 = workspace.getProject("project").getFile("folder/file.txt");
        InMemoryFile file3 = workspace.getProject("project").getFile("folder/other.txt");

        assertEquals(file1, file2);
        assertNotEquals(file1, file3);
        assertNotEquals(file1, null);
        assertNotEquals(file1, new Object());
        assertEquals(file1, file1);

        assertEquals(file1.hashCode(), file2.hashCode());
        assertNotEquals(file1.hashCode(), file3.hashCode());
    }

    @Test
    public void testToString() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertEquals(file.toString(), "/project/folder/file.txt");
    }

    @Test
    public void testGetUnderlyingPlatformResource() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertNull(file.getUnderlyingPlatformResource());
    }

    @Test
    public void testExistsCreateDelete() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");

        assertFalse(file.exists());

        file.create();
        assertTrue(file.exists());

        file.delete();
        assertFalse(file.exists());
    }

    @Test
    public void testGetName() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryFile file = workspace.getProject("project").getFile("folder/file.txt");
        assertEquals(file.getName(), "file.txt");
    }

    @Test
    public void testGetPathAndParent() {
        InMemoryWorkspace workspace = new InMemoryWorkspace();
        InMemoryProject project = workspace.getProject("project");
        InMemoryFolder folder = project.getFolder("folder");
        InMemoryFile file = folder.getFile("file.txt");

        assertEquals(file.getPath().toString(), "/project/folder/file.txt");
        assertSame(file.getParent(), folder);
        assertSame(folder.getParent(), project);
    }
}