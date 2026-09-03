package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class InMemoryResourceContainerTest {

    @Test
    public void testGetFileAndFolder() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();
        final InMemoryProject project = workspace.getProject("project");

        final File file = project.getFile("folder/file.txt");
        assertNotNull(file);
        assertEquals("file.txt", file.getName());
        assertEquals("/project/folder/file.txt", file.getPath().toString());

        final Folder folder = project.getFolder("folder/subfolder");
        assertNotNull(folder);
        assertEquals("subfolder", folder.getName());
        assertEquals("/project/folder/subfolder", folder.getPath().toString());
    }

    @Test
    public void testDelete() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();
        final InMemoryProject project = workspace.getProject("project");

        final File file = project.getFile("folder/file.txt");
        final Folder folder = project.getFolder("folder/subfolder");

        file.create();
        folder.create();

        assertTrue(file.exists());
        assertTrue(folder.exists());

        project.delete();

        assertFalse(file.exists());
        assertFalse(folder.exists());
    }

    @Test
    public void testListFilesAndFolders() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();
        final InMemoryProject project = workspace.getProject("project");

        final File file1 = project.getFile("file1.txt");
        file1.create();
        project.getFile("file2.txt");
        // file2 not created

        final Folder folder1 = project.getFolder("folder1");
        folder1.create();
        project.getFolder("folder2");
        // folder2 not created

        final List<File> files = project.listFiles();
        assertEquals(1, files.size());
        assertTrue(files.contains(file1));

        final List<Folder> folders = project.listFolders();
        assertEquals(1, folders.size());
        assertTrue(folders.contains(folder1));
    }

    @Test
    public void testIsParentOf() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();
        final InMemoryProject project = workspace.getProject("project");

        final File file = project.getFile("folder/file.txt");

        assertTrue(project.isParentOf(file));

        final InMemoryProject otherProject = workspace.getProject("otherProject");
        assertFalse(otherProject.isParentOf(file));
    }

    @Test
    public void testCreateWithRecord() {
        final InMemoryWorkspace workspace = new InMemoryWorkspace();
        final InMemoryProject project = workspace.getProject("project");
        final Folder folder = project.getFolder("folder1/folder2");

        final ContainerCreationRecord record = folder.createWithRecord();
        assertTrue(folder.exists());
        assertNotNull(record);
    }
}
