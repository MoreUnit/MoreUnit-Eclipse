package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, "preferred1", asList("preferred1", "preferred2"), asList("other1", "other2"));

        // when
        assertEquals(eval.getAllCorrespondingFilePatterns(), asList("preferred1", "preferred2", "other1", "other2"));
    }

    @Test
    public void should_return_all_corresponding_file_patterns__preferred_patterns_only() throws Exception
    {
        // given
        FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, "preferred1", asList("preferred1", "preferred2"), NO_PATTERNS);

        // when
        assertEquals(eval.getAllCorrespondingFilePatterns(), asList("preferred1", "preferred2"));
    }

    @Test
    public void should_convert_regex_to_eclipse_search_pattern() throws Exception
    {
        // given
        FileNameEvaluation eval = new FileNameEvaluation("Irrelevant", false, "PreFileSuf", asList("\\QPre\\E.*\\QFile\\E.*\\QSuf\\E"), asList("\\QPre\\E.*\\QFile\\E", "\\QFile\\E.*\\QSuf\\E"));

        // then
        assertEquals(Arrays.asList("Pre*File*Suf", "Pre*File", "File*Suf"), eval.getAllCorrespondingFileEclipsePatterns());
    }
}
