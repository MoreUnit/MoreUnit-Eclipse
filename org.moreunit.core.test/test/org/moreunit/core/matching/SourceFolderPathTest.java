package org.moreunit.core.matching;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

public class SourceFolderPathTest
{
    @Test
    public void should_not_be_resolved_when_containing_variable_segment() throws Exception
    {
        assertFalse(new SourceFolderPath("project/src/variable[^/]*segment/other-segment").isResolved());
    }

    @Test
    public void should_not_be_resolved_when_containing_variable_path() throws Exception
    {
        assertFalse(new SourceFolderPath("project/src/.*/other-segment").isResolved());
    }

    @Test
    public void should_be_resolved_otherwise() throws Exception
    {
        assertTrue(new SourceFolderPath("project/src/path/to/the/code").isResolved());
    }

    @Test
    public void should_return_resolved_part_when_containing_variable_segment() throws Exception
    {
        // given
        SourceFolderPath p = new SourceFolderPath("project/src/variable[^/]*segment/other-segment/othervariable[^/]*segment");

        // then
        assertThat(p.getResolvedPart().toString()).isEqualTo("project/src");
    }

    @Test
    public void should_return_resolved_part_when_containing_variable_path() throws Exception
    {
        // given
        SourceFolderPath p = new SourceFolderPath("project/src/main/.*/segment/.*/other-segment");

        // then
        assertThat(p.getResolvedPart().toString()).isEqualTo("project/src/main");
    }

    @Test
    public void should_return_whole_path_otherwise() throws Exception
    {
        // given
        SourceFolderPath p = new SourceFolderPath("project/src/main/.*/segment/.*/other-segment");

        // then
        assertThat(p.getResolvedPart().toString()).isEqualTo("project/src/main");
    }

    @Test
    public void should_match_file_with_same_folder() throws Exception
    {
        // given
        SourceFolderPath p = new SourceFolderPath("project/src/.*/variable[^/]*segment/.*/other-segment");

        IFile f = mock(IFile.class);
        when(f.getFullPath()).thenReturn(new Path("project/src/java/variable-segment/path/to/other-segment/SomeClass.java"));

        // then
        assertTrue("variable path part can contain any number of segments", p.matches(f));

        when(f.getFullPath()).thenReturn(new Path("project/src/java/variable-segment/path/other-segment/SomeClass.java"));

        // then
        assertTrue(p.matches(f));

        when(f.getFullPath()).thenReturn(new Path("project/src/java/variable_segment/path/to/other-segment/SomeClass.java"));

        // then
        assertTrue(p.matches(f));

        when(f.getFullPath()).thenReturn(new Path("project/src/java/variable/segment/path/to/other-segment/SomeClass.java"));

        // then
        assertFalse("variable segment musn't contain path separator", p.matches(f));

        when(f.getFullPath()).thenReturn(new Path("project/src/java/variable-segment/other-segment/SomeClass.java"));

        // then
        assertFalse("variable path part must contain at least one segment", p.matches(f));

        // given
        p = new SourceFolderPath("project/src/.*/variable[^/]*segment/.*/other-segment");

        when(f.getFullPath()).thenReturn(new Path("/project/src/java/variable-segment/path/to/other-segment/SomeClass.java"));

        assertTrue("should ignore leading separator", p.matches(f));
    }
}
