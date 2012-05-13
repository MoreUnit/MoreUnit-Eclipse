package org.moreunit.core.matching;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
}
