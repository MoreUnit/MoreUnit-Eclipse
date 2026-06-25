package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class TestFileNamePatternParserTest
{
    private final NameTokenizer camelCaseTokenizer = new CamelCaseNameTokenizer();
    private final NameTokenizer underscoreTokenizer = new SeparatorNameTokenizer("_");

    @Test
    public void should_complain_when_given_pattern_is_null() throws Exception
    {
        assertThrows(NullPointerException.class, () -> new TestFileNamePatternParser(null, underscoreTokenizer));
    }

    @Test
    public void should_complain_when_given_tokenizer_is_null() throws Exception
    {
        assertThrows(NullPointerException.class, () -> new TestFileNamePatternParser("${srcFile}Test", null));
    }

    @Test
    public void should_fail_when_src_file_variable_is_missing()
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("some_name", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertFalse(result.success());
        assertEquals(result, TestFileNamePatternParser.Failure.MISSING_SRC_FILE_VARIABLE);
    }

    @Test
    public void should_fail_when_only_src_file_variable_is_present() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertFalse(result.success());
        assertEquals(result, TestFileNamePatternParser.Failure.TEST_FILE_NAME_IS_EQUAL_TO_SRC_FILE_NAME);
    }

    @Test
    public void should_parse_pattern_with_one_suffix() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}suf", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertTrue(result.success());
        assertEquals(result.get().separator(), underscoreTokenizer.separator());
        assertFalse(result.get().prefix().hasAlternatives());
        assertEquals(Arrays.asList("suf"), result.get().suffix().alternatives());
        assertEquals(result.get().suffix().raw(), "suf");

        // given
        parser = new TestFileNamePatternParser("${srcFile}_suf", underscoreTokenizer);

        // when
        result = parser.parse();

        // then
        assertTrue(result.success());
        assertEquals(result.get().separator(), underscoreTokenizer.separator());
        assertFalse(result.get().prefix().hasAlternatives());
        assertEquals(Arrays.asList("suf"), result.get().suffix().alternatives());
        assertEquals(result.get().suffix().raw(), "_suf");
    }

    @Test
    public void should_parse_pattern_with_one_prefix() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("pre${srcFile}", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertTrue(result.success());
        assertEquals(result.get().separator(), underscoreTokenizer.separator());
        assertEquals(Arrays.asList("pre"), result.get().prefix().alternatives());
        assertEquals(result.get().prefix().raw(), "pre");
        assertFalse(result.get().suffix().hasAlternatives());

        // given
        parser = new TestFileNamePatternParser("pre_${srcFile}", underscoreTokenizer);

        // when
        result = parser.parse();

        // then
        assertTrue(result.success());
        assertEquals(result.get().separator(), underscoreTokenizer.separator());
        assertEquals(Arrays.asList("pre"), result.get().prefix().alternatives());
        assertEquals(result.get().prefix().raw(), "pre_");
        assertFalse(result.get().suffix().hasAlternatives());
    }

    @Test
    public void should_handle_empty_separator() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("Pre${srcFile}Suf", camelCaseTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertTrue(result.success());
        assertTrue(result.get().separator().isEmpty());
        assertEquals(Arrays.asList("Pre"), result.get().prefix().alternatives());
        assertEquals(Arrays.asList("Suf"), result.get().suffix().alternatives());
    }

    @Test
    public void should_report_wildcard_found_before_alternatives() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("*alt${srcFile}", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertTrue(result.get().prefix().hasWildcardBefore());
        assertFalse(result.get().prefix().hasWildcardAfter());

        // given
        parser = new TestFileNamePatternParser("${srcFile}*alt", underscoreTokenizer);

        // when
        result = parser.parse();

        // then
        assertTrue(result.get().suffix().hasWildcardBefore());
        assertFalse(result.get().suffix().hasWildcardAfter());
    }

    @Test
    public void should_report_wildcard_found_after_alternatives() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("alt*${srcFile}", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertFalse(result.get().prefix().hasWildcardBefore());
        assertTrue(result.get().prefix().hasWildcardAfter());

        // given
        parser = new TestFileNamePatternParser("${srcFile}alt*", underscoreTokenizer);

        // when
        result = parser.parse();

        // then
        assertFalse(result.get().suffix().hasWildcardBefore());
        assertTrue(result.get().suffix().hasWildcardAfter());
    }

    @Test
    public void should_parse_pattern_with_several_suffixes() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}_(suf1|suf2|suf3)", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertEquals(Arrays.asList("suf1", "suf2", "suf3"), result.get().suffix().alternatives());
    }

    @Test
    public void should_parse_pattern_with_several_prefixes() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("(pre1|pre2|pre3)_${srcFile}", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertEquals(Arrays.asList("pre1", "pre2", "pre3"), result.get().prefix().alternatives());
    }

    @Test
    public void should_parse_escaped_backslash() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}_\\\\test", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertEquals(Arrays.asList("\\test"), result.get().suffix().alternatives());
    }

    @Test
    public void should_parse_escaped_star() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("\\*pre\\*_${srcFile}_\\*suf\\*", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertFalse(result.get().prefix().hasWildcardBefore());
        assertFalse(result.get().prefix().hasWildcardAfter());
        assertEquals(Arrays.asList("*pre*"), result.get().prefix().alternatives());

        assertFalse(result.get().suffix().hasWildcardBefore());
        assertFalse(result.get().suffix().hasWildcardAfter());
        assertEquals(Arrays.asList("*suf*"), result.get().suffix().alternatives());
    }

    @Test
    public void should_parse_unescaped_star_followed_by_escaped_star() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}*\\*suf", new SeparatorNameTokenizer("*"));

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertTrue(result.get().suffix().hasWildcardBefore());
        assertFalse(result.get().suffix().hasWildcardAfter());
        assertEquals(Arrays.asList("*suf"), result.get().suffix().alternatives());
    }

    @Test
    public void should_parse_escaped_brackets_and_pipes() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("\\(pr\\|e\\)fix_${srcFile}_\\|suf\\(ix", underscoreTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertEquals(Arrays.asList("(pr|e)fix"), result.get().prefix().alternatives());
        assertEquals(Arrays.asList("|suf(ix"), result.get().suffix().alternatives());
    }

    @Test
    public void should_order_alternatives_by_desc_length() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}(Test|Tests)", camelCaseTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertEquals(Arrays.asList("Tests", "Test"), result.get().suffix().alternatives());
    }

    @Test
    public void should_keep_track_of_first_alternative() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("(Sh|Long)${srcFile}(Test|Tests)", camelCaseTokenizer);

        // when
        TestFileNamePatternParser.Result result = parser.parse();

        // then
        assertEquals(result.get().prefix().firstAlternative(), "Sh");
        assertEquals(result.get().suffix().firstAlternative(), "Test");
    }

    @Test
    public void should_complain_when_there_is_no_first_alternative() throws Exception
    {
        // given
        TestFileNamePatternParser parser = new TestFileNamePatternParser("${srcFile}Test", camelCaseTokenizer);
        TestFileNamePatternParser.Result result = parser.parse();

        assertThrows(NoSuchElementException.class, () -> result.get().prefix().firstAlternative());
    }
}
