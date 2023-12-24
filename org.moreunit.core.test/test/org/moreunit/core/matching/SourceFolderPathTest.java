package org.moreunit.core.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.junit.Test;
import org.moreunit.core.resources.InMemoryWorkspace;

public class SourceFolderPathTest
{
    @Test
    public void should_not_be_resolved_when_containing_variable_segment() throws Exception
    {
        assertThat(sourceFolderPath("project/src/variable[^/]*segment/other-segment").isResolved()).isFalse();
    }

    @Test
    public void should_not_be_resolved_when_containing_variable_path() throws Exception
    {
        assertThat(sourceFolderPath("project/src/.*/other-segment").isResolved()).isFalse();
    }

    @Test
    public void should_be_resolved_otherwise() throws Exception
    {
        assertThat(sourceFolderPath("project/src/path/to/the/code").isResolved());
    }

    @Test
    public void should_return_resolved_part_when_containing_variable_segment() throws Exception
    {
        // given
        SourceFolderPath p = sourceFolderPath("project/src/variable[^/]*segment/other-segment/othervariable[^/]*segment");

        // then
        assertThat(p.getResolvedPart().toString()).isEqualTo("project/src");
    }

    @Test
    public void should_return_resolved_part_when_containing_variable_path() throws Exception
    {
        // given
        SourceFolderPath p = sourceFolderPath("project/src/main/.*/segment/.*/other-segment");

        // then
        assertThat(p.getResolvedPart().toString()).isEqualTo("project/src/main");
    }

    @Test
    public void should_return_whole_path_otherwise() throws Exception
    {
        // given
        SourceFolderPath p = sourceFolderPath("project/src/main/.*/segment/.*/other-segment");

        // then
        assertThat(p.getResolvedPart().toString()).isEqualTo("project/src/main");
    }

    @Test
    public void should_match_file_with_same_folder() throws Exception
    {
        // given
        SourceFolderPath p = sourceFolderPath("project/src/.*/variable[^/]*segment/.*/other-segment");

        IFile f = mock(IFile.class);
        when(f.getFullPath()).thenReturn(new Path("project/src/java/variable-segment/path/to/other-segment/SomeClass.java"));

        // then
        assertThat(p.matches(f)).withFailMessage("variable path part can contain any number of segments").isTrue();

        when(f.getFullPath()).thenReturn(new Path("project/src/java/variable-segment/path/other-segment/SomeClass.java"));

        // then
        assertThat(p.matches(f)).isTrue();

        when(f.getFullPath()).thenReturn(new Path("project/src/java/variable_segment/path/to/other-segment/SomeClass.java"));

        // then
        assertThat(p.matches(f)).isTrue();

        when(f.getFullPath()).thenReturn(new Path("project/src/java/variable/segment/path/to/other-segment/SomeClass.java"));

        // then
        assertThat(p.matches(f)).withFailMessage("variable segment musn't contain path separator").isFalse();

        when(f.getFullPath()).thenReturn(new Path("project/src/java/variable-segment/other-segment/SomeClass.java"));

        // then
        assertThat(p.matches(f)).withFailMessage("variable path part must contain at least one segment").isFalse();

        // given
        p = sourceFolderPath("project/src/.*/variable[^/]*segment/.*/other-segment");

        when(f.getFullPath()).thenReturn(new Path("/project/src/java/variable-segment/path/to/other-segment/SomeClass.java"));

        assertThat(p.matches(f)).withFailMessage("should ignore leading separator").isTrue();
    }

    private SourceFolderPath sourceFolderPath(String path)
    {
        return new SourceFolderPath(path, new InMemoryWorkspace());
    }
}
