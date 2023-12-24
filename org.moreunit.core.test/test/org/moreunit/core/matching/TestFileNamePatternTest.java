package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.moreunit.core.matching.TestFileNamePattern.isValid;

import java.util.Collection;

import org.junit.Test;

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

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QMyFile\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_evaluate_test_file_with_suffix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}Suffix", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("SomeFileSuffix");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QSomeFile\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_evaluate_test_file_with_prefix_and_suffix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("Prefix${srcFile}Suf", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("PrefixAFileSuf");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QAFile\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_evaluate_test_file_with_variable_part_before_name() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("Pre*${srcFile}Suf", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("PreBarMySourceSuf");

        // then
        assertThat(evaluation.isTestFile()).isTrue();

        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).containsOnly("\\QBarMySource\\E");

        Collection<String> names = evaluation.getOtherCorrespondingFilePatterns();
        assertThat(names).containsOnly("\\QMySource\\E", "\\QSource\\E");
    }

    @Test
    public void should_plop() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("pre*__${srcFile}__suf", new SeparatorNameTokenizer("__"));

        // when
        FileNameEvaluation evaluation = pattern.evaluate("pre__bar__my__source__suf");

        // then
        assertThat(evaluation.isTestFile()).isTrue();

        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).containsOnly("\\Qbar__my__source\\E");

        Collection<String> names = evaluation.getOtherCorrespondingFilePatterns();
        assertThat(names).containsOnly("\\Qmy__source\\E", "\\Qsource\\E");
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
            assertThat(evaluation.isTestFile()).isTrue();

            assertThat(evaluation.getPreferredCorrespondingFilePatterns()).containsOnly("\\Qbar__my__source\\E");

            Collection<String> names = evaluation.getOtherCorrespondingFilePatterns();
            assertThat(names).containsOnly("\\Qmy__source\\E", "\\Qsource\\E");
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
        assertThat(evaluation.isTestFile()).isTrue();

        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).containsOnly("\\QMySourceBaz\\E");

        Collection<String> names = evaluation.getOtherCorrespondingFilePatterns();
        assertThat(names).containsOnly("\\QMySource\\E", "\\QMy\\E");
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
            assertThat(evaluation.isTestFile()).isTrue();

            assertThat(evaluation.getPreferredCorrespondingFilePatterns()).containsOnly("\\Qmy_source_baz\\E");

            Collection<String> names = evaluation.getOtherCorrespondingFilePatterns();
            assertThat(names).containsOnly("\\Qmy_source\\E", "\\Qmy\\E");
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
        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QMySource\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_evaluate_test_file_with_variable_part_after_suffix() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("Pre${srcFile}Suf*", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("PreMySourceSufQix");

        // then
        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QMySource\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_evaluate_test_file_with_variable_parts__extreme_case() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("*Pre*${srcFile}*Suf*", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("FooPreBarMySourceBazSufQix");

        // then
        assertThat(evaluation.isTestFile()).isTrue();

        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QBarMySourceBaz\\E");

        assertThat(evaluation.getOtherCorrespondingFilePatterns()) //
        .hasSize(6) //
        .contains("\\QBarMySource\\E", "\\QMySourceBaz\\E", "\\QSourceBaz\\E", "\\QBarMy\\E", "\\QBar\\E", "\\QBaz\\E");
    }

    @Test
    public void should_evaluate_test_file_with_several_possible_prefixes() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("(Pre1|Pre2)${srcFile}", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("Pre1MyFile");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QMyFile\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();

        evaluation = pattern.evaluate("Pre2MyFile");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QMyFile\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_evaluate_test_file_with_several_possible_suffixes() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}_(suf1|suf2)", underscoreTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("some_file_suf1");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\Qsome_file\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();

        evaluation = pattern.evaluate("some_file_suf2");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\Qsome_file\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_evaluate_test_file_with_several_possible_prefixes_and_suffixes() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("(pre1|pre2)${srcFile}_(suf1|suf2)", underscoreTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("some_file_suf1");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\Qsome_file\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();

        evaluation = pattern.evaluate("pre2_some_file");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\Qsome_file\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();

        evaluation = pattern.evaluate("pre1_some_file_suf2");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\Qsome_file\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();

        assertThat(pattern.evaluate("some_file").isTestFile()).isFalse();
    }

    @Test
    public void should_evaluate_test_file_with_several_prefixes_and_suffixes_and_variable_parts() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("(Pre1|Pre2)*${srcFile}(Suf1|Suf2)*", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("Pre1MyFile");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QMyFile\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).containsOnly("\\QFile\\E");

        evaluation = pattern.evaluate("MyFileSuf1");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QMyFile\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).containsOnly("\\QFile\\E");

        evaluation = pattern.evaluate("Pre2MyFile");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QMyFile\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).containsOnly("\\QFile\\E");

        evaluation = pattern.evaluate("Pre1MyFileSuf2");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QMyFile\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).containsOnly("\\QFile\\E");

        evaluation = pattern.evaluate("Pre2FooMyFileSuf2Bar");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QFooMyFile\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).containsOnly("\\QMyFile\\E", "\\QFile\\E");
    }

    @Test
    public void should_not_try_to_build_exhaustive_list_of_src_file_patterns_for_complex_template() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("(Pre1|Pre2)*${srcFile}(Suf1|Suf2)*", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("Pre1FooMyFileBarSuf2");

        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QFooMyFileBar\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns().contains("\\QMyFile\\E")).isFalse();
    }

    @Test
    public void should_evaluate_test_file_with_regex_symbols() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}_*_test", underscoreTokenizer);

        assertThat(pattern.evaluate("[some]*_(fi|le)_test").getAllCorrespondingFilePatterns()) //
        .hasSize(2).contains("\\Q[some]*_(fi|le)\\E", "\\Q[some]*\\E");
    }

    @Test
    public void should_evaluate_test_file_with_regex_range_like() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile} test", spaceTokenizer);

        assertThat(pattern.evaluate("myfile [rangelike-123] test").getAllCorrespondingFilePatterns()) //
            .containsOnly("\\Qmyfile [rangelike-123]\\E");
    }

    @Test
    public void should_evaluate_src_file_with_prefix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("Prefix${srcFile}", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("MyFile");

        assertThat(evaluation.isTestFile()).isFalse();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QPrefixMyFile\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_evaluate_src_file_with_suffix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}Suf", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("SomeFile");

        assertThat(evaluation.isTestFile()).isFalse();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QSomeFileSuf\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_evaluate_src_file_with_prefix_and_suffix() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("Pre${srcFile}Suffix", camelCaseTokenizer);

        FileNameEvaluation evaluation = pattern.evaluate("AFile");

        assertThat(evaluation.isTestFile()).isFalse();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QPreAFileSuffix\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).hasSize(2).contains("\\QPreAFile\\E", "\\QAFileSuffix\\E");
    }

    @Test
    public void should_evaluate_src_file_with_variable_parts() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("*Pre*${srcFile}*Suf*", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Source");

        // then
        assertThat(evaluation.isTestFile()).isFalse();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains(".*\\QPre\\E.*\\QSource\\E.*\\QSuf\\E.*");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).hasSize(2).contains(".*\\QPre\\E.*\\QSource\\E.*", ".*\\QSource\\E.*\\QSuf\\E.*");
    }

    @Test
    public void should_evaluate_src_file_with_several_possible_prefixes() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("(Pre1|Pre2)${srcFile}", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Source");

        // then
        assertThat(evaluation.isTestFile()).isFalse();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(2).contains("\\QPre1Source\\E", "\\QPre2Source\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_evaluate_src_file_with_several_possible_suffixes() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}(Suf1|Suf2)", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Source");

        // then
        assertThat(evaluation.isTestFile()).isFalse();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(2).contains("\\QSourceSuf1\\E", "\\QSourceSuf2\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_evaluate_src_file_with_several_possible_prefixes_and_suffixes() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("(Pre1|Pre2)${srcFile}(Suf1|Suf2)", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Source");

        // then
        assertThat(evaluation.isTestFile()).isFalse();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(4) //
        .contains("\\QPre1SourceSuf1\\E", "\\QPre1SourceSuf2\\E", "\\QPre2SourceSuf1\\E", "\\QPre2SourceSuf2\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).hasSize(4) //
        .contains("\\QPre1Source\\E", "\\QPre2Source\\E", "\\QSourceSuf1\\E", "\\QSourceSuf2\\E");
    }

    @Test
    public void should_evaluate_src_file_with_several_prefixes_and_suffixes_and_variable_parts() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("*(Pre1|Pre2)*${srcFile}*(Suf1|Suf2)*", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Source");

        // then
        assertThat(evaluation.isTestFile()).isFalse();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(4) //
        .contains(".*\\QPre1\\E.*\\QSource\\E.*\\QSuf1\\E.*", ".*\\QPre1\\E.*\\QSource\\E.*\\QSuf2\\E.*", ".*\\QPre2\\E.*\\QSource\\E.*\\QSuf1\\E.*", ".*\\QPre2\\E.*\\QSource\\E.*\\QSuf2\\E.*");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).hasSize(4) //
        .contains(".*\\QPre1\\E.*\\QSource\\E.*", ".*\\QPre2\\E.*\\QSource\\E.*", ".*\\QSource\\E.*\\QSuf1\\E.*", ".*\\QSource\\E.*\\QSuf2\\E.*");
    }

    @Test
    public void should_evaluate_src_file_with_regex_symbols() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("(a|b)_${srcFile}_(c|d)", underscoreTokenizer);

        assertThat(pattern.evaluate("[some]*_(fi|le)").getAllCorrespondingFilePatterns()) //
        .hasSize(8) //
        .contains("\\Qa_[some]*_(fi|le)_c\\E", "\\Qa_[some]*_(fi|le)_d\\E", "\\Qb_[some]*_(fi|le)_c\\E", "\\Qb_[some]*_(fi|le)_d\\E") //
        .contains("\\Qa_[some]*_(fi|le)\\E", "\\Qb_[some]*_(fi|le)\\E", "\\Q[some]*_(fi|le)_c\\E", "\\Q[some]*_(fi|le)_d\\E");
    }

    @Test
    public void should_evaluate_src_file_with_regex_range_like() throws Exception
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile} test", spaceTokenizer);

        assertThat(pattern.evaluate("myfile [rangelike-123]").getAllCorrespondingFilePatterns()) //
            .containsOnly("\\Qmyfile [rangelike-123] test\\E");
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
            assertThat(isValid(template, "")).withFailMessage(() -> template + " should be valid").isTrue();
        }

        String[] withSeparator = { "${srcFile}", "*${srcFile}", "${srcFile}*", "*${srcFile}*" //
        , "pre_${srcFile}", "${srcFile}_suf", "pre-${srcFile}_suf" //
        , "*pre${srcFile}", "${srcFile}_suf*", "*pre_${srcFile}_suf*" //
        , "*_pre*${srcFile}", "${srcFile}*_suf_*", "*pre*_${srcFile}*_suf*" //
        , "(p1|p2)_${srcFile}", "${srcFile}_(s1|s2)", "*(p1|p2)_*${srcFile}*_(s1|s2|s3)*" //
        , "${srcFile}_\\(test\\)", "${srcFile}_(\\(foo\\)|\\(bar\\))" };

        for (String template : withSeparator)
        {
            assertThat(isValid(template, "_")).withFailMessage(() -> template + " should be valid").isTrue();
        }
    }

    @Test
    public void should_invalidate_expressions() throws Exception
    {
        String[] withoutSeparator = { "*P*re*${srcFile}*Suf*", "*P1|P2*${srcFile}*(S1|S2)*", "*(P1|P2)*${srcFile}*S1|S2*" //
        , "(${srcFile})", "${something}" };

        for (String template : withoutSeparator)
        {
            assertThat(isValid(template, "")).isFalse();
        }

        String[] withSeparator = { "*pre*_${srcFile}*_s*uf*" };

        for (String template : withSeparator)
        {
            assertThat(isValid(template, "_")).isFalse();
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
        assertThat(evaluation.isTestFile()).isFalse();
        // and: test file patterns are proposed
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QMyFileTestTest\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
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
        assertThat(evaluation.isTestFile()).isTrue();
        // and: test file patterns are proposed
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\QMyFile\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_treat_longest_prefix_first_when_one_prefix_is_contained_in_another_one_s_start() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("*(Test|Tests)${srcFile}", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("TestsConcept");

        // then
        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).containsOnly("\\QConcept\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_treat_longest_suffix_first_when_one_suffix_is_contained_in_another_one_s_start() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}(Test|Tests)", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("ConceptTests");

        // then
        assertThat(evaluation.isTestFile()).isTrue();
        assertThat(evaluation.getPreferredCorrespondingFilePatterns()).containsOnly("\\QConcept\\E");
        assertThat(evaluation.getOtherCorrespondingFilePatterns()).isEmpty();
    }

    @Test
    public void should_use_first_prefix_to_generate_preferred_test_file_name() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("(Test|Tests)${srcFile}", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Concept");

        // then
        assertThat(evaluation.isTestFile()).isFalse();
        assertThat(evaluation.getPreferredCorrespondingFileName()).isEqualTo("TestConcept");
    }

    @Test
    public void should_use_first_suffix_to_generate_preferred_test_file_name() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}(Test|Tests)", camelCaseTokenizer);

        // when
        FileNameEvaluation evaluation = pattern.evaluate("Concept");

        // then
        assertThat(evaluation.isTestFile()).isFalse();
        assertThat(evaluation.getPreferredCorrespondingFileName()).isEqualTo("ConceptTest");
    }

    @Test
    public void should_support_protected_regex_symbols_in_template() throws Exception
    {
        SeparatorNameTokenizer spaceTokenizer = new SeparatorNameTokenizer(" ");

        // protected brackets
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile} \\(test\\)", spaceTokenizer);

        assertThat(pattern.evaluate("some file").getAllCorrespondingFilePatterns()) //
        .containsOnly("\\Qsome file (test)\\E");

        // protected star
        pattern = new TestFileNamePattern("${srcFile} \\*", spaceTokenizer);

        assertThat(pattern.evaluate("some file").getAllCorrespondingFilePatterns()) //
        .containsOnly("\\Qsome file *\\E");

        // dollar (does not require protection)
        pattern = new TestFileNamePattern("$ ${srcFile}", spaceTokenizer);

        assertThat(pattern.evaluate("some file").getAllCorrespondingFilePatterns()) //
        .containsOnly("\\Q$ some file\\E");
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
        assertThat(result.isTestFile()).isFalse();
        assertThat(result.getAllCorrespondingFilePatterns()) //
        .containsOnly("\\Qsome*file\\E.*\\Q*test\\E");

        // given
        result = pattern.evaluate("some*file*foo*test");

        // then
        assertThat(result.isTestFile()).isTrue();
        assertThat(result.getPreferredCorrespondingFilePatterns()).hasSize(1).contains("\\Qsome*file*foo\\E");
        assertThat(result.getOtherCorrespondingFilePatterns()) //
        .hasSize(2).contains("\\Qsome*file\\E", "\\Qsome\\E");
    }
}
