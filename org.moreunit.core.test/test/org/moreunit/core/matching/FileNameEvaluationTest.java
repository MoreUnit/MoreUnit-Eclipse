package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

public class FileNameEvaluationTest
{
    private static final Collection<String> NO_PATTERNS = new ArrayList<String>();

    @Test
    public void should_return_first_preferred_pattern_as_preferred_file_name() throws Exception
    {
        // given
        FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, asList("One", "Two2", "three"), asList("whatever"));

        // then
        assertThat(eval.getPreferredCorrespondingFileName()).isEqualTo("One");
    }

    @Test
    public void should_remove_variable_parts_from_preferred_file_name() throws Exception
    {
        // given
        FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, asList(".*Pre.*Source.*Suf.*"), NO_PATTERNS);

        // then
        assertThat(eval.getPreferredCorrespondingFileName()).isEqualTo("PreSourceSuf");
    }

    @Test
    public void should_return_all_corresponding_file_patterns__preferred_first() throws Exception
    {
        // given
        FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, asList("preferred1", "preferred2"), asList("other1", "other2"));

        // when
        assertThat(eval.getAllCorrespondingFilePatterns()).isEqualTo(asList("preferred1", "preferred2", "other1", "other2"));
    }

    @Test
    public void should_return_all_corresponding_file_patterns__preferred_patterns_only() throws Exception
    {
        // given
        FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, asList("preferred1", "preferred2"), NO_PATTERNS);

        // when
        assertThat(eval.getAllCorrespondingFilePatterns()).isEqualTo(asList("preferred1", "preferred2"));
    }
}
