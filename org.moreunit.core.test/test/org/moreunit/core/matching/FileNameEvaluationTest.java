package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

public class FileNameEvaluationTest
{
    private static final Collection<String> NO_PATTERNS = new ArrayList<>();

    @Test
    public void should_return_all_corresponding_file_patterns__preferred_first() throws Exception
    {
        // given
        final FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, "preferred1", asList("preferred1", "preferred2"), asList("other1", "other2"));

        // when
        assertEquals(eval.getAllCorrespondingFilePatterns(), asList("preferred1", "preferred2", "other1", "other2"));
    }

    @Test
    public void should_return_all_corresponding_file_patterns__preferred_patterns_only() throws Exception
    {
        // given
        final FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, "preferred1", asList("preferred1", "preferred2"), NO_PATTERNS);

        // when
        assertEquals(eval.getAllCorrespondingFilePatterns(), asList("preferred1", "preferred2"));
    }

    @Test
    public void should_return_is_test_file() throws Exception
    {
        // given
        final FileNameEvaluation eval1 = new FileNameEvaluation("Irrelevant", false, "preferred1", asList("preferred1"), NO_PATTERNS);
        final FileNameEvaluation eval2 = new FileNameEvaluation("Irrelevant", true, "preferred1", asList("preferred1"), NO_PATTERNS);

        // then
        assertFalse(eval1.isTestFile());
        assertTrue(eval2.isTestFile());
    }

    @Test
    public void should_return_to_string() throws Exception
    {
        // given
        final FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, "preferred1", asList("preferred1"), NO_PATTERNS);

        // then
        assertTrue(eval.toString().contains("FileNameEvaluation("));
    }

    @Test
    public void should_convert_regex_to_eclipse_search_pattern() throws Exception
    {
        // given
        final FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, "PreFileSuf", asList("\\QPre\\E.*\\QFile\\E.*\\QSuf\\E"), asList("\\QPre\\E.*\\QFile\\E", "\\QFile\\E.*\\QSuf\\E"));

        // then
        assertEquals(Arrays.asList("Pre*File*Suf", "Pre*File", "File*Suf"), eval.getAllCorrespondingFileEclipsePatterns());
    }
}
