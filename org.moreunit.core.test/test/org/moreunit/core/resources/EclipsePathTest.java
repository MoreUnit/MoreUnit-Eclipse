package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;

import org.eclipse.core.runtime.IPath;
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

    @Test
    public void should_wrap_ipath_and_delegate_methods() throws Exception
    {
        IPath ipath = mock(IPath.class);
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

        EclipsePath path = constructor.newInstance(ipath);

        assertEquals(2, path.getSegmentCount());
        assertEquals("proj", path.getProjectName());
        assertEquals("Foo.java", path.getBaseName());
        assertEquals("java", path.getExtension());
        assertTrue(path.hasExtension());
        assertTrue(path.isAbsolute());
        assertFalse(path.isEmpty());
        assertEquals("/proj/Foo.java", path.toString());
    }
}
