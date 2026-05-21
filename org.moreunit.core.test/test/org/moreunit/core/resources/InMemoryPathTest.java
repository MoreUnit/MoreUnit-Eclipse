package org.moreunit.core.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.Test;

public class InMemoryPathTest {

    @Test
    public void testGetBaseNameWithoutExtension() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertThat(path.getBaseNameWithoutExtension()).isEqualTo("file");

        InMemoryPath pathNoExt = new InMemoryPath("/project/folder/file");
        assertThat(pathNoExt.getBaseNameWithoutExtension()).isEqualTo("file");

        InMemoryPath emptyPath = new InMemoryPath("");
        assertThat(emptyPath.getBaseNameWithoutExtension()).isEqualTo("");
    }

    @Test
    public void testGetExtension() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertThat(path.getExtension()).isEqualTo("txt");

        InMemoryPath pathNoExt = new InMemoryPath("/project/folder/file");
        assertThat(pathNoExt.getExtension()).isEqualTo("");

        InMemoryPath emptyPath = new InMemoryPath("");
        assertThat(emptyPath.getExtension()).isEqualTo("");
    }

    @Test
    public void testGetProjectName() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertThat(path.getProjectName()).isEqualTo("project");

        InMemoryPath emptyPath = new InMemoryPath("");
        assertThat(emptyPath.getProjectName()).isEqualTo("");
    }

    @Test
    public void testHasExtension() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertThat(path.hasExtension()).isTrue();

        InMemoryPath pathNoExt = new InMemoryPath("/project/folder/file");
        assertThat(pathNoExt.hasExtension()).isFalse();
    }

    @Test
    public void testIterator() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        Iterator<String> iterator = path.iterator();
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo("project");
        assertThat(iterator.next()).isEqualTo("folder");
        assertThat(iterator.next()).isEqualTo("file.txt");
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    public void testRelativeToProject() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertThat(path.relativeToProject().toString()).isEqualTo("folder/file.txt");
    }

    @Test
    public void testEqualsAndHashCode() {
        InMemoryPath path1 = new InMemoryPath("/project/folder/file.txt");
        InMemoryPath path2 = new InMemoryPath("/project/folder/file.txt");
        InMemoryPath path3 = new InMemoryPath("/project/folder/other.txt");

        assertThat(path1).isEqualTo(path2);
        assertThat(path1).isNotEqualTo(path3);
        assertThat(path1).isNotEqualTo(null);
        assertThat(path1).isNotEqualTo(new Object());
        assertThat(path1).isEqualTo(path1);

        assertThat(path1.hashCode()).isEqualTo(path2.hashCode());
        assertThat(path1.hashCode()).isNotEqualTo(path3.hashCode());
    }

    @Test
    public void testWithoutLastSegment() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertThat(path.withoutLastSegment().toString()).isEqualTo("/project/folder");

        InMemoryPath pathRoot = new InMemoryPath("/project");
        assertThat(pathRoot.withoutLastSegment().toString()).isEqualTo("/");

        InMemoryPath emptyPath = new InMemoryPath("");
        assertThat(emptyPath.withoutLastSegment().toString()).isEqualTo("");
    }

    @Test
    public void testUptoSegment() {
        InMemoryPath path = new InMemoryPath("/project/folder/file.txt");
        assertThat(path.uptoSegment(1).toString()).isEqualTo("/project");
        assertThat(path.uptoSegment(2).toString()).isEqualTo("/project/folder");
        assertThat(path.uptoSegment(3).toString()).isEqualTo("/project/folder/file.txt");

        try {
            path.uptoSegment(4);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertThat(e.getMessage()).isEqualTo("No segment at index: 4");
        }
    }

    @Test
    public void testWithRelativePath() {
        InMemoryPath path = new InMemoryPath("/project/folder");
        InMemoryPath relativePath = new InMemoryPath("file.txt");

        assertThat(path.withRelativePath(relativePath).toString()).isEqualTo("/project/folder/file.txt");

        InMemoryPath absolutePath = new InMemoryPath("/file.txt");
        try {
            path.withRelativePath(absolutePath);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("not a relative path");
        }
    }
}