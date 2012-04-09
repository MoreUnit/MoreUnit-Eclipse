package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

public class TestFileNamePatternTest
{
    private NameTokenizer camelCaseTokenizer = new CamelCaseNameTokenizer();

    @Test
    public void should_evaluate_test_file_with_prefix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("Pre${srcFile}", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("PreMyFile");

        assertTrue(evaluation.isTestFile());
        assertEquals("MyFile", evaluation.getPreferredCorrespondingFilePattern());
    }

    @Test
    public void should_evaluate_test_file_with_suffix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}_suffix", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("SomeFile_suffix");

        assertTrue(evaluation.isTestFile());
        assertEquals("SomeFile", evaluation.getPreferredCorrespondingFilePattern());
    }

    @Test
    public void should_evaluate_test_file_with_prefix_and_suffix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("prefix_${srcFile}Suf", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("prefix_aFileSuf");

        assertTrue(evaluation.isTestFile());
        assertEquals("aFile", evaluation.getPreferredCorrespondingFilePattern());
    }

    @Test
    public void should_evaluate_test_file_with_variable_part_before_name() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("Pre*${srcFile}Suf", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("PreBarMySourceSuf");

        // then
        assertTrue(evaluation.isTestFile());

        assertEquals("BarMySource", evaluation.getPreferredCorrespondingFilePattern());

        Collection<String> names = evaluation.getOtherCorrespondingFileNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("MySource"));
        assertTrue(names.contains("Source"));
    }

    @Test
    public void should_evaluate_test_file_with_variable_part_before_name__with_double_separator() throws Exception
    {
        for (String template : asList("pre*${srcFile}__suf", "pre__*${srcFile}__suf", "pre*__${srcFile}__suf", "pre__*__${srcFile}__suf"))
        {
            // given
            TestFileNamePattern pattern = new TestFileNamePattern(template, new SeparatorNameTokenizer("__"));

            // when
            FileNameEvaluation evaluation = pattern.evaluate("pre__bar__my__source__suf");

            // then
            assertTrue(evaluation.isTestFile());

            assertEquals("bar__my__source", evaluation.getPreferredCorrespondingFilePattern());

            Collection<String> names = evaluation.getOtherCorrespondingFileNames();
            assertEquals(2, names.size());
            assertTrue(names.contains("my__source"));
            assertTrue(names.contains("source"));
        }
    }

    @Test
    public void should_evaluate_test_file_with_variable_part_after_name() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("Pre${srcFile}*Suf", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("PreMySourceBazSuf");

        // then
        assertTrue(evaluation.isTestFile());

        assertEquals("MySourceBaz", evaluation.getPreferredCorrespondingFilePattern());

        Collection<String> names = evaluation.getOtherCorrespondingFileNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("MySource"));
        assertTrue(names.contains("My"));
    }

    @Test
    public void should_evaluate_test_file_with_variable_part_after_name__with_separator() throws Exception
    {
        for (String template : asList("pre_${srcFile}*suf", "pre_${srcFile}_*suf", "pre_${srcFile}*_suf", "pre_${srcFile}_*_suf"))
        {
            // given
            TestFileNamePattern pattern = new TestFileNamePattern(template, new SeparatorNameTokenizer("_"));

            // when
            FileNameEvaluation evaluation = pattern.evaluate("pre_my_source_baz_suf");

            // then
            assertTrue(evaluation.isTestFile());

            assertEquals("my_source_baz", evaluation.getPreferredCorrespondingFilePattern());

            Collection<String> names = evaluation.getOtherCorrespondingFileNames();
            assertEquals(2, names.size());
            assertTrue(names.contains("my_source"));
            assertTrue(names.contains("my"));
        }
    }

    @Test
    public void should_evaluate_test_file_with_variable_part_before_prefix() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("*Pre${srcFile}Suf", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("FooPreMySourceSuf");

        // then
        assertTrue(evaluation.isTestFile());

        assertEquals("MySource", evaluation.getPreferredCorrespondingFilePattern());

        Collection<String> names = evaluation.getOtherCorrespondingFileNames();
        assertTrue(names.isEmpty());
    }

    @Test
    public void should_evaluate_test_file_with_variable_part_after_suffix() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("Pre${srcFile}Suf*", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("PreMySourceSufQix");

        // then
        assertTrue(evaluation.isTestFile());

        assertEquals("MySource", evaluation.getPreferredCorrespondingFilePattern());

        Collection<String> names = evaluation.getOtherCorrespondingFileNames();
        assertTrue(names.isEmpty());
    }

    @Test
    public void should_evaluate_test_file_with_variable_parts__extreme_case() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("*Pre*${srcFile}*Suf*", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("FooPreBarMySourceBazSufQix");

        // then
        assertTrue(evaluation.isTestFile());

        assertEquals("BarMySourceBaz", evaluation.getPreferredCorrespondingFilePattern());

        Collection<String> names = evaluation.getOtherCorrespondingFileNames();
        assertEquals(asList("BarMySource", "MySourceBaz", "SourceBaz", "BarMy", "Bar", "Baz"), names);
    }

    @Test
    public void should_evaluate_src_file_with_prefix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("Prefix${srcFile}", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("MyFile");

        assertFalse(evaluation.isTestFile());
        assertEquals("PrefixMyFile", evaluation.getPreferredCorrespondingFilePattern());
    }

    @Test
    public void should_evaluate_src_file_with_suffix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}_suf", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("SomeFile");

        assertFalse(evaluation.isTestFile());
        assertEquals("SomeFile_suf", evaluation.getPreferredCorrespondingFilePattern());
    }

    @Test
    public void should_evaluate_src_file_with_prefix_and_suffix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("pre_${srcFile}Suffix", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("aFile");

        assertFalse(evaluation.isTestFile());
        assertEquals("pre_aFileSuffix", evaluation.getPreferredCorrespondingFilePattern());
    }

    @Test
    public void should_evaluate_src_file_with_variable_parts() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("*Pre*${srcFile}*Suf*", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Source");

        // then
        assertFalse(evaluation.isTestFile());
        assertEquals(".*Pre.*Source.*Suf.*", evaluation.getPreferredCorrespondingFilePattern());
        assertTrue(evaluation.getOtherCorrespondingFileNames().isEmpty());
    }
}
