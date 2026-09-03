package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.util.Iterator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EclipsePathTest
{
    private Constructor<EclipsePath> constructor;

    @BeforeEach
    public void setUp() throws Exception
    {
        constructor = EclipsePath.class.getDeclaredConstructor(IPath.class);
        constructor.setAccessible(true);
    }

    private EclipsePath newPath(String path) throws Exception
    {
        return constructor.newInstance(new Path(path));
    }

    @Test
    public void should_wrap_ipath_and_delegate_methods() throws Exception
    {
        final IPath ipath = mock(IPath.class);
        when(ipath.removeTrailingSeparator()).thenReturn(ipath);
        when(ipath.segmentCount()).thenReturn(2);
        when(ipath.segment(0)).thenReturn("proj");
        when(ipath.lastSegment()).thenReturn("Foo.java");
        when(ipath.getFileExtension()).thenReturn("java");
        when(ipath.removeFileExtension()).thenReturn(ipath);
        when(ipath.isEmpty()).thenReturn(false);
        when(ipath.isAbsolute()).thenReturn(true);
        when(ipath.toString()).thenReturn("/proj/Foo.java");
        when(ipath.segments()).thenReturn(new String[] {"proj", "Foo.java"});

        final EclipsePath path = constructor.newInstance(ipath);

        assertEquals(2, path.getSegmentCount());
        assertEquals("proj", path.getProjectName());
        assertEquals("Foo.java", path.getBaseName());
        assertEquals("java", path.getExtension());
        assertTrue(path.hasExtension());
        assertTrue(path.isAbsolute());
        assertFalse(path.isEmpty());
        assertEquals("/proj/Foo.java", path.toString());
    }

    @Test
    public void should_remove_trailing_separator_when_wrapped() throws Exception
    {
        assertEquals("/proj", newPath("/proj/").toString());
    }

    @Test
    public void should_compare_paths_by_value() throws Exception
    {
        final EclipsePath path = newPath("/proj/Foo.java");
        final EclipsePath samePath = newPath("/proj/Foo.java");
        final EclipsePath otherPath = newPath("/proj/Bar.java");

        assertSame(path, path);
        assertEquals(path, samePath);
        assertNotEquals(path, otherPath);
        assertNotEquals(path, "not a path");
        assertEquals(path.hashCode(), samePath.hashCode());
    }

    @Test
    public void should_return_base_name_without_extension() throws Exception
    {
        assertEquals("Foo", newPath("/proj/src/Foo.java").getBaseNameWithoutExtension());
    }

    @Test
    public void should_return_empty_extension_when_path_has_none() throws Exception
    {
        final EclipsePath path = newPath("/proj/src/Foo");

        assertFalse(path.hasExtension());
        assertEquals("", path.getExtension());
    }

    @Test
    public void should_return_empty_base_name_for_empty_path() throws Exception
    {
        assertEquals("", newPath("").getBaseName());
    }

    @Test
    public void should_return_root_as_base_name_for_root_path() throws Exception
    {
        assertEquals("/", newPath("/").getBaseName());
    }

    @Test
    public void should_return_project_name_of_first_segment_or_empty() throws Exception
    {
        assertEquals("proj", newPath("/proj/src/Foo.java").getProjectName());
        assertEquals("", newPath("").getProjectName());
    }

    @Test
    public void should_iterate_over_segments() throws Exception
    {
        final Iterator<String> segments = newPath("/proj/src/Foo.java").iterator();

        assertEquals("proj", segments.next());
        assertEquals("src", segments.next());
        assertEquals("Foo.java", segments.next());
        assertFalse(segments.hasNext());
    }

    @Test
    public void should_be_relative_when_not_absolute() throws Exception
    {
        assertTrue(newPath("src/Foo.java").isRelative());
        assertFalse(newPath("src/Foo.java").isAbsolute());
        assertFalse(newPath("/proj/src/Foo.java").isRelative());
    }

    @Test
    public void should_detect_prefix_paths() throws Exception
    {
        assertTrue(newPath("/proj/src").isPrefixOf(newPath("/proj/src/Foo.java")));
        assertFalse(newPath("/proj/src").isPrefixOf(newPath("/proj/test/Foo.java")));
    }

    @Test
    public void should_return_path_relative_to_project() throws Exception
    {
        assertEquals("src/Foo.java", newPath("/proj/src/Foo.java").relativeToProject().toString());
    }

    @Test
    public void should_truncate_path_at_given_segment() throws Exception
    {
        assertEquals("/proj", newPath("/proj/src/Foo.java").uptoSegment(1).toString());
        assertEquals("/proj/src", newPath("/proj/src/Foo.java").uptoSegment(2).toString());
    }

    @Test
    public void should_fail_when_truncating_path_beyond_its_length() throws Exception
    {
        assertThrows(IndexOutOfBoundsException.class, () -> newPath("/proj/src").uptoSegment(3));
    }

    @Test
    public void should_remove_last_segment() throws Exception
    {
        assertEquals("/proj/src", newPath("/proj/src/Foo.java").withoutLastSegment().toString());
    }

    @Test
    public void should_append_relative_path() throws Exception
    {
        assertEquals("/proj/src/Foo.java", newPath("/proj").withRelativePath(newPath("src/Foo.java")).toString());
    }
}

