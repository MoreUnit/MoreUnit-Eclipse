package org.moreunit.core.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.moreunit.core.matching.CamelCaseNameTokenizer;
import org.moreunit.core.matching.SeparatorNameTokenizer;
import org.moreunit.core.matching.TestFileNamePattern;

public class FileNamePatternDemoTest
{
    @Test
    public void should_generate_simple_camelcase_source_file_name() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}Test", new CamelCaseNameTokenizer());

        // then
        assertThat(FileNamePatternDemo.generateSourceFileName(pattern)).isEqualTo("FooBar");
    }

    @Test
    public void should_generate_simple_source_file_name_with_separator() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}_test", new SeparatorNameTokenizer("_"));

        // then
        assertThat(FileNamePatternDemo.generateSourceFileName(pattern)).isEqualTo("foo_bar");
    }

    @Test
    public void should_generate_source_file_name_with_stars_and_groups() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("(bla|bli)*${srcFile}-*(plop|plip)*", new SeparatorNameTokenizer("-"));

        // then
        assertThat(FileNamePatternDemo.generateSourceFileName(pattern)).isEqualTo("foo-bar");
    }
}
