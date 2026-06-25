package org.moreunit.matching;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.moreunit.test.model.Types.typeWithPackage;

import org.junit.jupiter.api.Test;

public class TestClassNamePatternTest
{
    @Test
    public void should_evaluate_cut_with_several_prefixes_and_suffixes_and_variable_parts() throws Exception
    {
        // given
        TestClassNamePattern pattern = new TestClassNamePattern("(Pre1|Pre2)*${srcFile}*(Suf1|Suf2)", "pp", "ps");

        // when
        ClassNameEvaluation evaluation = pattern.evaluate(typeWithPackage("Source", "pack.age"));

        // then
        assertFalse(evaluation.isTestCase());

        assertEquals(8, evaluation.getAllCorrespondingClassPatterns(false).size());
    }

    @Test
    public void should_add_package_to_test_case_patterns_when_so_requested__prefix() throws Exception
    {
        // given
        TestClassNamePattern pattern = new TestClassNamePattern("(Pre1|Pre2)*${srcFile}*(Suf1|Suf2)", "pp", null);

        // when
        ClassNameEvaluation evaluation = pattern.evaluate(typeWithPackage("Source", "pack.age"));

        // then
        assertEquals(8, evaluation.getAllCorrespondingClassPatterns(true).size());
    }

    @Test
    public void should_add_package_to_test_case_patterns_when_so_requested__suffix() throws Exception
    {
        // given
        TestClassNamePattern pattern = new TestClassNamePattern("(Pre1|Pre2)*${srcFile}*(Suf1|Suf2)", null, "ps");

        // when
        ClassNameEvaluation evaluation = pattern.evaluate(typeWithPackage("Source", "pack.age"));

        // then
        assertEquals(8, evaluation.getAllCorrespondingClassPatterns(true).size());
    }

    @Test
    public void should_add_package_to_test_case_patterns_when_so_requested__prefix_and_suffix() throws Exception
    {
        // given
        TestClassNamePattern pattern = new TestClassNamePattern("(Pre1|Pre2)*${srcFile}*(Suf1|Suf2)", "pp", "ps");

        // when
        ClassNameEvaluation evaluation = pattern.evaluate(typeWithPackage("Source", "pack.age"));

        // then
        assertEquals(8, evaluation.getAllCorrespondingClassPatterns(true).size());
    }

    @Test
    public void should_add_package_to_cut_patterns_when_so_requested() throws Exception
    {
        // given
        TestClassNamePattern pattern = new TestClassNamePattern("(Pre1|Pre2)*${srcFile}(Suf1|Suf2)", "pp", "ps");

        // when
        ClassNameEvaluation evaluation = pattern.evaluate(typeWithPackage("Pre2MyFileSuf1", "pp.pack.age.ps"));

        // then
        assertEquals(2, evaluation.getAllCorrespondingClassPatterns(true).size()); // other patterns
    }

    @Test
    public void should_evaluate_test_case_with_several_prefixes_and_suffixes_and_variable_parts() throws Exception
    {
        // given
        TestClassNamePattern pattern = new TestClassNamePattern("(Pre1|Pre2)*${srcFile}(Suf1|Suf2)*", "pp", "ps");

        for (String testCase : asList("Pre1MyFile", "MyFileSuf1", "Pre2MyFile", "Pre1MyFileSuf2"))
        {
            // when
            ClassNameEvaluation evaluation = pattern.evaluate(typeWithPackage(testCase, "pp.pack.age.ps"));

            // then
            assertTrue(evaluation.isTestCase());
            assertEquals(2, evaluation.getAllCorrespondingClassPatterns(false).size()); // other patterns
        }

        // when
        ClassNameEvaluation  evaluation = pattern.evaluate(typeWithPackage("Pre2FooMyFileSuf2Bar", "pp.pack.age.ps"));

        // then
        assertTrue(evaluation.isTestCase());
        assertEquals(3, evaluation.getAllCorrespondingClassPatterns(false).size()); // other patterns
    }

    @Test
    public void should_not_evaluate_to_test_case_when_package_name_does_not_match_test_preferences() throws Exception
    {
        // given
        TestClassNamePattern pattern = new TestClassNamePattern("${srcFile}Test", null, "test");

        // when: file name suggest a test, but package does not
        ClassNameEvaluation evaluation = pattern.evaluate(typeWithPackage("SourceTest", "pack.age"));

        // then
        assertFalse(evaluation.isTestCase());
    }
}
