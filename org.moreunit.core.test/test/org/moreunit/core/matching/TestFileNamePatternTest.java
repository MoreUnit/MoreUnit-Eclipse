package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.moreunit.core.matching.TestFileNamePattern.isValid;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

public class TestFileNamePatternTest
{
    private final NameTokenizer camelCaseTokenizer = new CamelCaseNameTokenizer();
    private final NameTokenizer spaceTokenizer = new SeparatorNameTokenizer(" ");
    private final NameTokenizer underscoreTokenizer = new SeparatorNameTokenizer("_");

    @Test
    public void should_evaluate_test_file_with_prefix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("Pre${srcFile}", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("PreMyFile");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
    }

    @Test
    public void should_evaluate_test_file_with_suffix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}Suffix", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("SomeFileSuffix");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
    }

    @Test
    public void should_evaluate_test_file_with_prefix_and_suffix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("Prefix${srcFile}Suf", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("PrefixAFileSuf");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
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

        assertEquals(new HashSet<>(Arrays.asList("\\QBarMySource\\E")), new HashSet<>((evaluation.getPreferredCorrespondingFilePatterns())));

        Collection<String> names = evaluation.getOtherCorrespondingFilePatterns();
        assertEquals(new HashSet<>(Arrays.asList("\\QMySource\\E", "\\QSource\\E")), new HashSet<>((names)));
    }

    @Test
    public void should_plop() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("pre*__${srcFile}__suf", new SeparatorNameTokenizer("__"));

        // when
        FileNameEvaluation evaluation = pattern.evaluate("pre__bar__my__source__suf");

        // then
        assertTrue(evaluation.isTestFile());

        assertEquals(new HashSet<>(Arrays.asList("\\Qbar__my__source\\E")), new HashSet<>((evaluation.getPreferredCorrespondingFilePatterns())));

        Collection<String> names = evaluation.getOtherCorrespondingFilePatterns();
        assertEquals(new HashSet<>(Arrays.asList("\\Qmy__source\\E", "\\Qsource\\E")), new HashSet<>((names)));
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

            assertEquals(new HashSet<>(Arrays.asList("\\Qbar__my__source\\E")), new HashSet<>((evaluation.getPreferredCorrespondingFilePatterns())));

            Collection<String> names = evaluation.getOtherCorrespondingFilePatterns();
            assertEquals(new HashSet<>(Arrays.asList("\\Qmy__source\\E", "\\Qsource\\E")), new HashSet<>((names)));
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

        assertEquals(new HashSet<>(Arrays.asList("\\QMySourceBaz\\E")), new HashSet<>((evaluation.getPreferredCorrespondingFilePatterns())));

        Collection<String> names = evaluation.getOtherCorrespondingFilePatterns();
        assertEquals(new HashSet<>(Arrays.asList("\\QMySource\\E", "\\QMy\\E")), new HashSet<>((names)));
    }

    @Test
    public void should_evaluate_test_file_with_variable_part_after_name__with_separator() throws Exception
    {
        for (String template : asList("pre_${srcFile}*suf", "pre_${srcFile}_*suf", "pre_${srcFile}*_suf", "pre_${srcFile}_*_suf"))
        {
            // given
            TestFileNamePattern pattern = new TestFileNamePattern(template, underscoreTokenizer);

            // when
            FileNameEvaluation evaluation = pattern.evaluate("pre_my_source_baz_suf");

            // then
            assertTrue(evaluation.isTestFile());

            assertEquals(new HashSet<>(Arrays.asList("\\Qmy_source_baz\\E")), new HashSet<>((evaluation.getPreferredCorrespondingFilePatterns())));

            Collection<String> names = evaluation.getOtherCorrespondingFilePatterns();
            assertEquals(new HashSet<>(Arrays.asList("\\Qmy_source\\E", "\\Qmy\\E")), new HashSet<>((names)));
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
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
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
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
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

        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());

        assertEquals(6, evaluation.getOtherCorrespondingFilePatterns().size());
    }

    @Test
    public void should_evaluate_test_file_with_several_possible_prefixes() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("(Pre1|Pre2)${srcFile}", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("Pre1MyFile");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());

        evaluation = pattern.evaluate("Pre2MyFile");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
    }

    @Test
    public void should_evaluate_test_file_with_several_possible_suffixes() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}_(suf1|suf2)", underscoreTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("some_file_suf1");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());

        evaluation = pattern.evaluate("some_file_suf2");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
    }

    @Test
    public void should_evaluate_test_file_with_several_possible_prefixes_and_suffixes() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("(pre1|pre2)${srcFile}_(suf1|suf2)", underscoreTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("some_file_suf1");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());

        evaluation = pattern.evaluate("pre2_some_file");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());

        evaluation = pattern.evaluate("pre1_some_file_suf2");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());

        assertFalse(pattern.evaluate("some_file").isTestFile());
    }

    @Test
    public void should_evaluate_test_file_with_several_prefixes_and_suffixes_and_variable_parts() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("(Pre1|Pre2)*${srcFile}(Suf1|Suf2)*", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("Pre1MyFile");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertEquals(new HashSet<>(Arrays.asList("\\QFile\\E")), new HashSet<>((evaluation.getOtherCorrespondingFilePatterns())));

        evaluation = pattern.evaluate("MyFileSuf1");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertEquals(new HashSet<>(Arrays.asList("\\QFile\\E")), new HashSet<>((evaluation.getOtherCorrespondingFilePatterns())));

        evaluation = pattern.evaluate("Pre2MyFile");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertEquals(new HashSet<>(Arrays.asList("\\QFile\\E")), new HashSet<>((evaluation.getOtherCorrespondingFilePatterns())));

        evaluation = pattern.evaluate("Pre1MyFileSuf2");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertEquals(new HashSet<>(Arrays.asList("\\QFile\\E")), new HashSet<>((evaluation.getOtherCorrespondingFilePatterns())));

        evaluation = pattern.evaluate("Pre2FooMyFileSuf2Bar");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertEquals(new HashSet<>(Arrays.asList("\\QMyFile\\E", "\\QFile\\E")), new HashSet<>((evaluation.getOtherCorrespondingFilePatterns())));
    }

    @Test
    public void should_not_try_to_build_exhaustive_list_of_src_file_patterns_for_complex_template() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("(Pre1|Pre2)*${srcFile}(Suf1|Suf2)*", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("Pre1FooMyFileBarSuf2");

        assertTrue(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertFalse(evaluation.getOtherCorrespondingFilePatterns().contains("\\QMyFile\\E"));
    }

    @Test
    public void should_evaluate_test_file_with_regex_symbols() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}_*_test", underscoreTokenizer);

        assertEquals(2, pattern.evaluate("[some]*_(fi|le)_test").getAllCorrespondingFilePatterns().size());
    }

    @Test
    public void should_evaluate_test_file_with_regex_range_like() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile} test", spaceTokenizer);

        assertEquals(new HashSet<>(Arrays.asList("\\Qmyfile [rangelike-123]\\E")), new HashSet<>((pattern.evaluate("myfile [rangelike-123] test").getAllCorrespondingFilePatterns())));
    }

    @Test
    public void should_evaluate_src_file_with_prefix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("Prefix${srcFile}", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("MyFile");

        assertFalse(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
    }

    @Test
    public void should_evaluate_src_file_with_suffix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}Suf", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("SomeFile");

        assertFalse(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
    }

    @Test
    public void should_evaluate_src_file_with_prefix_and_suffix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("Pre${srcFile}Suffix", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("AFile");

        assertFalse(evaluation.isTestFile());
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertEquals(2, evaluation.getOtherCorrespondingFilePatterns().size());
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
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertEquals(2, evaluation.getOtherCorrespondingFilePatterns().size());
    }

    @Test
    public void should_evaluate_src_file_with_several_possible_prefixes() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("(Pre1|Pre2)${srcFile}", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Source");

        // then
        assertFalse(evaluation.isTestFile());
        assertEquals(2, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
    }

    @Test
    public void should_evaluate_src_file_with_several_possible_suffixes() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}(Suf1|Suf2)", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Source");

        // then
        assertFalse(evaluation.isTestFile());
        assertEquals(2, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
    }

    @Test
    public void should_evaluate_src_file_with_several_possible_prefixes_and_suffixes() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("(Pre1|Pre2)${srcFile}(Suf1|Suf2)", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Source");

        // then
        assertFalse(evaluation.isTestFile());
        assertEquals(4, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertEquals(4, evaluation.getOtherCorrespondingFilePatterns().size());
    }

    @Test
    public void should_evaluate_src_file_with_several_prefixes_and_suffixes_and_variable_parts() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("*(Pre1|Pre2)*${srcFile}*(Suf1|Suf2)*", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Source");

        // then
        assertFalse(evaluation.isTestFile());
        assertEquals(4, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertEquals(4, evaluation.getOtherCorrespondingFilePatterns().size());
    }

    @Test
    public void should_evaluate_src_file_with_regex_symbols() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("(a|b)_${srcFile}_(c|d)", underscoreTokenizer);

        assertEquals(8, pattern.evaluate("[some]*_(fi|le)").getAllCorrespondingFilePatterns().size());
    }

    @Test
    public void should_evaluate_src_file_with_regex_range_like() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile} test", spaceTokenizer);

        assertEquals(new HashSet<>(Arrays.asList("\\Qmyfile [rangelike-123] test\\E")), new HashSet<>((pattern.evaluate("myfile [rangelike-123]").getAllCorrespondingFilePatterns())));
    }

    @Test
    public void should_validate_expressions() throws Exception
    {
        String[] withoutSeparator = { "${srcFile}", "*${srcFile}", "${srcFile}*", "*${srcFile}*" //
        , "Pre${srcFile}", "${srcFile}Suf", "Pre${srcFile}Suf" //
        , "*Pre${srcFile}", "${srcFile}Suf*", "*Pre${srcFile}Suf*" //
        , "*Pre*${srcFile}", "${srcFile}*Suf*", "*Pre*${srcFile}*Suf*" //
        , "(P1|P2)${srcFile}", "${srcFile}(S1|S2)", "*(P1|P2)*${srcFile}*(S1|S2|S3)*" };

        for (String template : withoutSeparator)
        {
            assertTrue(isValid(template, ""));
        }

        String[] withSeparator = { "${srcFile}", "*${srcFile}", "${srcFile}*", "*${srcFile}*" //
        , "pre_${srcFile}", "${srcFile}_suf", "pre-${srcFile}_suf" //
        , "*pre${srcFile}", "${srcFile}_suf*", "*pre_${srcFile}_suf*" //
        , "*_pre*${srcFile}", "${srcFile}*_suf_*", "*pre*_${srcFile}*_suf*" //
        , "(p1|p2)_${srcFile}", "${srcFile}_(s1|s2)", "*(p1|p2)_*${srcFile}*_(s1|s2|s3)*" //
        , "${srcFile}_\\(test\\)", "${srcFile}_(\\(foo\\)|\\(bar\\))" };

        for (String template : withSeparator)
        {
            assertTrue(isValid(template, "_"));
        }
    }

    @Test
    public void should_invalidate_expressions() throws Exception
    {
        String[] withoutSeparator = { "*P*re*${srcFile}*Suf*", "*P1|P2*${srcFile}*(S1|S2)*", "*(P1|P2)*${srcFile}*S1|S2*" //
        , "(${srcFile})", "${something}" };

        for (String template : withoutSeparator)
        {
            assertFalse(isValid(template, ""));
        }

        String[] withSeparator = { "*pre*_${srcFile}*_s*uf*" };

        for (String template : withSeparator)
        {
            assertFalse(isValid(template, "_"));
        }
    }

    @Test
    public void should_allow_for_forcing_file_type_to_source() throws Exception
    {
        // given a pattern built with file type forced to "source"
        TestFileNamePattern pattern = TestFileNamePattern.forceEvaluationAsSourceFile("${srcFile}Test", "");

        // when evaluating the pattern:
        // (if only trusting the name template, this file should be a test file)
        FileNameEvaluation evaluation = pattern.evaluate("MyFileTest");

        // then: file is considered as source file
        assertFalse(evaluation.isTestFile());
        // and: test file patterns are proposed
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
    }

    @Test
    public void should_allow_for_forcing_file_type_to_test() throws Exception
    {
        // given a pattern built with file type forced to "test"
        TestFileNamePattern pattern = TestFileNamePattern.forceEvaluationAsTestFile("${srcFile}Test", "");

        // when evaluating the pattern:
        // (if only trusting the name template, this file should be a source
        // file)
        FileNameEvaluation evaluation = pattern.evaluate("MyFile");

        // then: file is considered as source file
        assertTrue(evaluation.isTestFile());
        // and: test file patterns are proposed
        assertEquals(1, evaluation.getPreferredCorrespondingFilePatterns().size());
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
    }

    @Test
    public void should_treat_longest_prefix_first_when_one_prefix_is_contained_in_another_one_s_start() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("*(Test|Tests)${srcFile}", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("TestsConcept");

        // then
        assertTrue(evaluation.isTestFile());
        assertEquals(new HashSet<>(Arrays.asList("\\QConcept\\E")), new HashSet<>((evaluation.getPreferredCorrespondingFilePatterns())));
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
    }

    @Test
    public void should_treat_longest_suffix_first_when_one_suffix_is_contained_in_another_one_s_start() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}(Test|Tests)", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("ConceptTests");

        // then
        assertTrue(evaluation.isTestFile());
        assertEquals(new HashSet<>(Arrays.asList("\\QConcept\\E")), new HashSet<>((evaluation.getPreferredCorrespondingFilePatterns())));
        assertTrue(evaluation.getOtherCorrespondingFilePatterns().isEmpty());
    }

    @Test
    public void should_use_first_prefix_to_generate_preferred_test_file_name() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("(Test|Tests)${srcFile}", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Concept");

        // then
        assertFalse(evaluation.isTestFile());
        assertEquals(evaluation.getPreferredCorrespondingFileName(), "TestConcept");
    }

    @Test
    public void should_use_first_suffix_to_generate_preferred_test_file_name() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}(Test|Tests)", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Concept");

        // then
        assertFalse(evaluation.isTestFile());
        assertEquals(evaluation.getPreferredCorrespondingFileName(), "ConceptTest");
    }

    @Test
    public void should_support_protected_regex_symbols_in_template() throws Exception
    {
        SeparatorNameTokenizer spaceTokenizer = new SeparatorNameTokenizer(" ");

        // protected brackets
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile} \\(test\\)", spaceTokenizer);

        assertEquals(new HashSet<>(Arrays.asList("\\Qsome file (test)\\E")), new HashSet<>((pattern.evaluate("some file").getAllCorrespondingFilePatterns())));

        // protected star
        pattern = new TestFileNamePattern("${srcFile} \\*", spaceTokenizer);

        assertEquals(new HashSet<>(Arrays.asList("\\Qsome file *\\E")), new HashSet<>((pattern.evaluate("some file").getAllCorrespondingFilePatterns())));

        // dollar (does not require protection)
        pattern = new TestFileNamePattern("$ ${srcFile}", spaceTokenizer);

        assertEquals(new HashSet<>(Arrays.asList("\\Q$ some file\\E")), new HashSet<>((pattern.evaluate("some file").getAllCorrespondingFilePatterns())));
    }

    @Test
    public void should_support_regex_symbols_in_separator() throws Exception
    {
        // given
        SeparatorNameTokenizer dollarTokenizer = new SeparatorNameTokenizer("*");

        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}*\\*test", dollarTokenizer);

        // when
        FileNameEvaluation result = pattern.evaluate("some*file");

        // then
        assertFalse(result.isTestFile());
        assertEquals(new HashSet<>(Arrays.asList("\\Qsome*file\\E.*\\Q*test\\E")), new HashSet<>((result.getAllCorrespondingFilePatterns())));

        // given
        result = pattern.evaluate("some*file*foo*test");

        // then
        assertTrue(result.isTestFile());
        assertEquals(1, result.getPreferredCorrespondingFilePatterns().size());
        assertEquals(2, result.getOtherCorrespondingFilePatterns().size());
    }
}
