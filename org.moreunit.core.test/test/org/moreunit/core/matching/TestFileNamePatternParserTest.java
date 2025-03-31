package org.moreunit.core.matching;

import static org.assertj.core.api.Assertions.*;

import java.util.NoSuchElementException;

import org.junit.Test;

public class TestFileNamePatternParserTest
{
    private final NameTokenizer camelCaseTokenizer = new CamelCaseNameTokenizer();
    private final NameTokenizer underscoreTokenizer = new SeparatorNameTokenizer("_");

    @Test(expected = NullPointerException.class)
    public void should_complain_when_given_pattern_is_null() throws Exception
    {
        new TestFileNamePatternParser(null, underscoreTokenizer);
    }

    @Test(expected = NullPointerException.class)
    public void should_complain_when_given_tokenizer_is_null() throws Exception
    {
        new TestFileNamePatternParser("${srcFile}Test", null);
    }

    @Test
    public void should_fail_when_src_file_variable_is_missing()
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("some_name", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.success()).isFalse();
        assertThat(result).isEqualTo(TestFileNamePatternParser.Failure.MISSING_SRC_FILE_VARIABLE);
    }

    @Test
    public void should_fail_when_only_src_file_variable_is_present() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.success()).isFalse();
        assertThat(result).isEqualTo(TestFileNamePatternParser.Failure.TEST_FILE_NAME_IS_EQUAL_TO_SRC_FILE_NAME);
    }

    @Test
    public void should_parse_pattern_with_one_suffix() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}suf", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.success()).isTrue();
        assertThat(result.get().separator()).isEqualTo(underscoreTokenizer.separator());
        assertThat(result.get().prefix().hasAlternatives()).isFalse();
        assertThat(result.get().suffix().alternatives()).containsExactly("suf");
        assertThat(result.get().suffix().raw()).isEqualTo("suf");

        // given
        parser = new TestFileNamePatternParser("${srcFile}_suf", underscoreTokenizer);

        // when
        result = parser.parse();

        // then
        assertThat(result.success()).isTrue();
        assertThat(result.get().separator()).isEqualTo(underscoreTokenizer.separator());
        assertThat(result.get().prefix().hasAlternatives()).isFalse();
        assertThat(result.get().suffix().alternatives()).containsExactly("suf");
        assertThat(result.get().suffix().raw()).isEqualTo("_suf");
    }

    @Test
    public void should_parse_pattern_with_one_prefix() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("pre${srcFile}", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.success()).isTrue();
        assertThat(result.get().separator()).isEqualTo(underscoreTokenizer.separator());
        assertThat(result.get().prefix().alternatives()).containsExactly("pre");
        assertThat(result.get().prefix().raw()).isEqualTo("pre");
        assertThat(result.get().suffix().hasAlternatives()).isFalse();

        // given
        parser = new TestFileNamePatternParser("pre_${srcFile}", underscoreTokenizer);

        // when
        result = parser.parse();

        // then
        assertThat(result.success()).isTrue();
        assertThat(result.get().separator()).isEqualTo(underscoreTokenizer.separator());
        assertThat(result.get().prefix().alternatives()).containsExactly("pre");
        assertThat(result.get().prefix().raw()).isEqualTo("pre_");
        assertThat(result.get().suffix().hasAlternatives()).isFalse();
    }

    @Test
    public void should_handle_empty_separator() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("Pre${srcFile}Suf", camelCaseTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.success()).isTrue();
        assertThat(result.get().separator()).isEmpty();
        assertThat(result.get().prefix().alternatives()).containsExactly("Pre");
        assertThat(result.get().suffix().alternatives()).containsExactly("Suf");
    }

    @Test
    public void should_report_wildcard_found_before_alternatives() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("*alt${srcFile}", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.get().prefix().hasWildcardBefore()).isTrue();
        assertThat(result.get().prefix().hasWildcardAfter()).isFalse();

        // given
        parser = new TestFileNamePatternParser("${srcFile}*alt", underscoreTokenizer);

        // when
        result = parser.parse();

        // then
        assertThat(result.get().suffix().hasWildcardBefore()).isTrue();
        assertThat(result.get().suffix().hasWildcardAfter()).isFalse();
    }

    @Test
    public void should_report_wildcard_found_after_alternatives() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("alt*${srcFile}", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.get().prefix().hasWildcardBefore()).isFalse();
        assertThat(result.get().prefix().hasWildcardAfter()).isTrue();

        // given
        parser = new TestFileNamePatternParser("${srcFile}alt*", underscoreTokenizer);

        // when
        result = parser.parse();

        // then
        assertThat(result.get().suffix().hasWildcardBefore()).isFalse();
        assertThat(result.get().suffix().hasWildcardAfter()).isTrue();
    }

    @Test
    public void should_parse_pattern_with_several_suffixes() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}_(suf1|suf2|suf3)", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.get().suffix().alternatives()).containsExactly("suf1", "suf2", "suf3");
    }

    @Test
    public void should_parse_pattern_with_several_prefixes() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("(pre1|pre2|pre3)_${srcFile}", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.get().prefix().alternatives()).containsExactly("pre1", "pre2", "pre3");
    }

    @Test
    public void should_parse_escaped_backslash() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}_\\\\test", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.get().suffix().alternatives()).containsExactly("\\test");
    }

    @Test
    public void should_parse_escaped_star() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("\\*pre\\*_${srcFile}_\\*suf\\*", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.get().prefix().hasWildcardBefore()).isFalse();
        assertThat(result.get().prefix().hasWildcardAfter()).isFalse();
        assertThat(result.get().prefix().alternatives()).containsExactly("*pre*");

        assertThat(result.get().suffix().hasWildcardBefore()).isFalse();
        assertThat(result.get().suffix().hasWildcardAfter()).isFalse();
        assertThat(result.get().suffix().alternatives()).containsExactly("*suf*");
    }

    @Test
    public void should_parse_unescaped_star_followed_by_escaped_star() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}*\\*suf", new SeparatorNameTokenizer("*"));

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.get().suffix().hasWildcardBefore()).isTrue();
        assertThat(result.get().suffix().hasWildcardAfter()).isFalse();
        assertThat(result.get().suffix().alternatives()).containsExactly("*suf");
    }

    @Test
    public void should_parse_escaped_brackets_and_pipes() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("\\(pr\\|e\\)fix_${srcFile}_\\|suf\\(ix", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.get().prefix().alternatives()).containsExactly("(pr|e)fix");
        assertThat(result.get().suffix().alternatives()).containsExactly("|suf(ix");
    }

    @Test
    public void should_order_alternatives_by_desc_length() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}(Test|Tests)", camelCaseTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.get().suffix().alternatives()).containsExactly("Tests", "Test");
    }

    @Test
    public void should_keep_track_of_first_alternative() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("(Sh|Long)${srcFile}(Test|Tests)", camelCaseTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertThat(result.get().prefix().firstAlternative()).isEqualTo("Sh");
        assertThat(result.get().suffix().firstAlternative()).isEqualTo("Test");
    }

    @Test
    public void should_complain_when_there_is_no_first_alternative() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}Test", camelCaseTokenizer);
        TestFileNamePatternParser.Result result = parser.parse();

        assertThatThrownBy(() -> result.get().prefix().firstAlternative()).isInstanceOf(NoSuchElementException.class);
    }
}
