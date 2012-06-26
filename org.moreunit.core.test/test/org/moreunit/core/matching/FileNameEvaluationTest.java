package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.Test;

public class FileNameEvaluationTest
{
    @Test
    public void should_return_first_preferred_pattern_as_preferred_file_name() throws Exception
    {
        // given
        FileNameEvaluation eval = new FileNameEvaluation(false, asList("One", "Two2", "three"), asList("whatever"));

        // then
        assertThat(eval.getPreferredCorrespondingFileName()).isEqualTo("One");
    }

    @Test
    public void should_remove_variable_parts_from_preferred_file_name() throws Exception
    {
        // given
        FileNameEvaluation eval = new FileNameEvaluation(false, asList(".*Pre.*Source.*Suf.*"), new ArrayList<String>());

        // then
        assertThat(eval.getPreferredCorrespondingFileName()).isEqualTo("PreSourceSuf");
    }
}
