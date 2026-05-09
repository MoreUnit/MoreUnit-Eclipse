package org.moreunit.core.matching;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

public class FileNameEvaluationTest
{
    private static final Collection<String> NO_PATTERNS = new ArrayList<String>();

    @Test
    public void should_return_all_corresponding_file_patterns__preferred_first() throws Exception
    {
        // given
        FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, "preferred1", asList("preferred1", "preferred2"), asList("other1", "other2"));

        // when
        assertThat(eval.getAllCorrespondingFilePatterns()).isEqualTo(asList("preferred1", "preferred2", "other1", "other2"));
    }

    @Test
    public void should_return_all_corresponding_file_patterns__preferred_patterns_only() throws Exception
    {
        // given
        FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, "preferred1", asList("preferred1", "preferred2"), NO_PATTERNS);

        // when
        assertThat(eval.getAllCorrespondingFilePatterns()).isEqualTo(asList("preferred1", "preferred2"));
    }

    @Test
    public void should_convert_regex_to_eclipse_search_pattern() throws Exception
    {
        // given
        FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, "PreFileSuf", asList("\\QPre\\E.*\\QFile\\E.*\\QSuf\\E"), asList("\\QPre\\E.*\\QFile\\E", "\\QFile\\E.*\\QSuf\\E"));

        // then
        assertThat(eval.getAllCorrespondingFileEclipsePatterns()).containsExactly("Pre*File*Suf", "Pre*File", "File*Suf");
    }

    @Test
    public void should_identify_as_test_file() throws Exception
    {
        // given
        FileNameEvaluation eval = new FileNameEvaluation("IrrelevantTest", true, "Irrelevant", NO_PATTERNS, NO_PATTERNS);

        // then
        assertThat(eval.isTestFile()).isTrue();
        assertThat(eval.getPreferredCorrespondingFileName()).isEqualTo("Irrelevant");
    }

    @Test
    public void should_generate_to_string() throws Exception
    {
        // given
        FileNameEvaluation eval = new FileNameEvaluation("IrrelevantTest", true, "Irrelevant", NO_PATTERNS, NO_PATTERNS);

        // then
        assertThat(eval.toString()).contains("IrrelevantTest");
    }
}
