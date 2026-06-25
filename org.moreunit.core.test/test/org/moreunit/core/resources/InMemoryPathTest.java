package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

public class InMemoryPathTest {

    @Test
    public void testGetBaseNameWithoutExtension() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertEquals(path.getBaseNameWithoutExtension(), "file");

        InMemoryPath pathNoExt = new InMemoryPath("/project/folder/file");
        assertEquals(pathNoExt.getBaseNameWithoutExtension(), "file");

        InMemoryPath emptyPath = new InMemoryPath("");
        assertEquals(emptyPath.getBaseNameWithoutExtension(), "");
    }

    @Test
    public void testGetExtension() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertEquals(path.getExtension(), "txt");

        InMemoryPath pathNoExt = new InMemoryPath("/project/folder/file");
        assertEquals(pathNoExt.getExtension(), "");

        InMemoryPath emptyPath = new InMemoryPath("");
        assertEquals(emptyPath.getExtension(), "");
    }

    @Test
    public void testGetProjectName() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertEquals(path.getProjectName(), "project");

        InMemoryPath emptyPath = new InMemoryPath("");
        assertEquals(emptyPath.getProjectName(), "");
    }

    @Test
    public void testHasExtension() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertTrue(path.hasExtension());

        InMemoryPath pathNoExt = new InMemoryPath("/project/folder/file");
        assertFalse(pathNoExt.hasExtension());
    }

    @Test
    public void testIterator() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        Iterator<String> iterator = path.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(iterator.next(), "project");
        assertEquals(iterator.next(), "folder");
        assertEquals(iterator.next(), "file.txt");
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testRelativeToProject() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertEquals(path.relativeToProject().toString(), "folder/file.txt");
    }

    @Test
    public void testEqualsAndHashCode() {
        InMemoryPath path1 = new InMemoryPath("/project/folder/file.txt");
        InMemoryPath path2 = new InMemoryPath("/project/folder/file.txt");
        InMemoryPath path3 = new InMemoryPath("/project/folder/other.txt");

        assertEquals(path1, path2);
        assertNotEquals(path1, path3);
        assertNotEquals(path1, null);
        assertNotEquals(path1, new Object());
        assertEquals(path1, path1);

        assertEquals(path1.hashCode(), path2.hashCode());
        assertNotEquals(path1.hashCode(), path3.hashCode());
    }

    @Test
    public void testWithoutLastSegment() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertEquals(path.withoutLastSegment().toString(), "/project/folder");

        InMemoryPath pathRoot = new InMemoryPath("/project");
        assertEquals(pathRoot.withoutLastSegment().toString(), "/");

        InMemoryPath emptyPath = new InMemoryPath("");
        assertEquals(emptyPath.withoutLastSegment().toString(), "");
    }

    @Test
    public void testUptoSegment() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertEquals(path.uptoSegment(1).toString(), "/project");
        assertEquals(path.uptoSegment(2).toString(), "/project/folder");
        assertEquals(path.uptoSegment(3).toString(), "/project/folder/file.txt");

        try {
            path.uptoSegment(4);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals(e.getMessage(), "No segment at index: 4");
        }
    }

    @Test
    public void testWithRelativePath() {
        InMemoryPath path = new InMemoryPath("/project/folder");
        InMemoryPath relativePath = new InMemoryPath("file.txt");

        assertEquals(path.withRelativePath(relativePath).toString(), "/project/folder/file.txt");

        InMemoryPath absolutePath = new InMemoryPath("/file.txt");
        try {
            path.withRelativePath(absolutePath);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "not a relative path");
        }
    }
}