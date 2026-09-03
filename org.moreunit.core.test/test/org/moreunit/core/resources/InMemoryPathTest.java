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
        final InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertEquals(path.getBaseNameWithoutExtension(), "file");

        final InMemoryPath pathNoExt = new InMemoryPath("/project/folder/file");
        assertEquals(pathNoExt.getBaseNameWithoutExtension(), "file");

        final InMemoryPath emptyPath = new InMemoryPath("");
        assertEquals(emptyPath.getBaseNameWithoutExtension(), "");
    }

    @Test
    public void testGetExtension() {
        final InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertEquals(path.getExtension(), "txt");

        final InMemoryPath pathNoExt = new InMemoryPath("/project/folder/file");
        assertEquals(pathNoExt.getExtension(), "");

        final InMemoryPath emptyPath = new InMemoryPath("");
        assertEquals(emptyPath.getExtension(), "");
    }

    @Test
    public void testGetProjectName() {
        final InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertEquals(path.getProjectName(), "project");

        final InMemoryPath emptyPath = new InMemoryPath("");
        assertEquals(emptyPath.getProjectName(), "");
    }

    @Test
    public void testHasExtension() {
        final InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertTrue(path.hasExtension());

        final InMemoryPath pathNoExt = new InMemoryPath("/project/folder/file");
        assertFalse(pathNoExt.hasExtension());
    }

    @Test
    public void testIterator() {
        final InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        final Iterator<String> iterator = path.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(iterator.next(), "project");
        assertEquals(iterator.next(), "folder");
        assertEquals(iterator.next(), "file.txt");
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testRelativeToProject() {
        final InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertEquals(path.relativeToProject().toString(), "folder/file.txt");
    }

    @Test
    public void testEqualsAndHashCode() {
        final InMemoryPath path1 = new InMemoryPath("/project/folder/file.txt");
        final InMemoryPath path2 = new InMemoryPath("/project/folder/file.txt");
        final InMemoryPath path3 = new InMemoryPath("/project/folder/other.txt");

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
        final InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertEquals(path.withoutLastSegment().toString(), "/project/folder");

        final InMemoryPath pathRoot = new InMemoryPath("/project");
        assertEquals(pathRoot.withoutLastSegment().toString(), "/");

        final InMemoryPath emptyPath = new InMemoryPath("");
        assertEquals(emptyPath.withoutLastSegment().toString(), "");
    }

    @Test
    public void testUptoSegment() {
        final InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertEquals(path.uptoSegment(1).toString(), "/project");
        assertEquals(path.uptoSegment(2).toString(), "/project/folder");
        assertEquals(path.uptoSegment(3).toString(), "/project/folder/file.txt");

        try {
            path.uptoSegment(4);
            fail("Expected IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException e) {
            assertEquals(e.getMessage(), "No segment at index: 4");
        }
    }

    @Test
    public void testWithRelativePath() {
        final InMemoryPath path = new InMemoryPath("/project/folder");
        final InMemoryPath relativePath = new InMemoryPath("file.txt");

        assertEquals(path.withRelativePath(relativePath).toString(), "/project/folder/file.txt");

        final InMemoryPath absolutePath = new InMemoryPath("/file.txt");
        try {
            path.withRelativePath(absolutePath);
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            assertEquals(e.getMessage(), "not a relative path");
        }
    }

    @Test
    public void testIsAbsoluteAndRelative() {
        final InMemoryPath absolutePath = new InMemoryPath("/project/folder");
        assertTrue(absolutePath.isAbsolute());
        assertFalse(absolutePath.isRelative());

        final InMemoryPath relativePath = new InMemoryPath("folder/file.txt");
        assertFalse(relativePath.isAbsolute());
        assertTrue(relativePath.isRelative());
    }

    @Test
    public void testIsPrefixOf() {
        final InMemoryPath path1 = new InMemoryPath("/project/folder");
        final InMemoryPath path2 = new InMemoryPath("/project/folder/file.txt");
        final InMemoryPath path3 = new InMemoryPath("/other/folder");

        assertTrue(path1.isPrefixOf(path2));
        assertTrue(path1.isPrefixOf(path1));
        assertFalse(path1.isPrefixOf(path3));
    }
}
