package org.moreunit.matching;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.moreunit.test.model.Types.typeWithPackage;

import org.junit.Test;

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
        assertThat(evaluation.isTestCase()).isFalse();

        assertThat(evaluation.getAllCorrespondingClassPatterns(false)).hasSize(8) //
            // preferred patterns
            .startsWith("Pre1*Source*Suf1", "Pre1*Source*Suf2", "Pre2*Source*Suf1", "Pre2*Source*Suf2")
            // other patterns
            .contains("Pre1*Source*", "Pre2*Source*", "*Source*Suf1", "*Source*Suf2");
    }

    @Test
    public void should_add_package_to_test_case_patterns_when_so_requested__prefix() throws Exception
    {
        // given
        TestClassNamePattern pattern = new TestClassNamePattern("(Pre1|Pre2)*${srcFile}*(Suf1|Suf2)", "pp", null);

        // when
        ClassNameEvaluation evaluation = pattern.evaluate(typeWithPackage("Source", "pack.age"));

        // then
        assertThat(evaluation.getAllCorrespondingClassPatterns(true)).hasSize(8) //
            // preferred patterns
            .startsWith("pp.pack.age.Pre1*Source*Suf1", //
                        "pp.pack.age.Pre1*Source*Suf2", //
                        "pp.pack.age.Pre2*Source*Suf1", //
                        "pp.pack.age.Pre2*Source*Suf2")
            // other patterns
            .contains("pp.pack.age.Pre1*Source*", //
                      "pp.pack.age.Pre2*Source*", //
                      "pp.pack.age.*Source*Suf1", //
                      "pp.pack.age.*Source*Suf2");
    }

    @Test
    public void should_add_package_to_test_case_patterns_when_so_requested__suffix() throws Exception
    {
        // given
        TestClassNamePattern pattern = new TestClassNamePattern("(Pre1|Pre2)*${srcFile}*(Suf1|Suf2)", null, "ps");

        // when
        ClassNameEvaluation evaluation = pattern.evaluate(typeWithPackage("Source", "pack.age"));

        // then
        assertThat(evaluation.getAllCorrespondingClassPatterns(true)).hasSize(8) //
            // preferred patterns
            .startsWith("pack.age.ps.Pre1*Source*Suf1", //
                        "pack.age.ps.Pre1*Source*Suf2", //
                        "pack.age.ps.Pre2*Source*Suf1", //
                        "pack.age.ps.Pre2*Source*Suf2")
            // other patterns
            .contains("pack.age.ps.Pre1*Source*", //
                      "pack.age.ps.Pre2*Source*", //
                      "pack.age.ps.*Source*Suf1", //
                      "pack.age.ps.*Source*Suf2");
    }

    @Test
    public void should_add_package_to_test_case_patterns_when_so_requested__prefix_and_suffix() throws Exception
    {
        // given
        TestClassNamePattern pattern = new TestClassNamePattern("(Pre1|Pre2)*${srcFile}*(Suf1|Suf2)", "pp", "ps");

        // when
        ClassNameEvaluation evaluation = pattern.evaluate(typeWithPackage("Source", "pack.age"));

        // then
        assertThat(evaluation.getAllCorrespondingClassPatterns(true)).hasSize(8) //
            // preferred patterns
            .startsWith("pp.pack.age.ps.Pre1*Source*Suf1", //
                        "pp.pack.age.ps.Pre1*Source*Suf2", //
                        "pp.pack.age.ps.Pre2*Source*Suf1", //
                        "pp.pack.age.ps.Pre2*Source*Suf2")
            // other patterns
            .contains("pp.pack.age.ps.Pre1*Source*", //
                      "pp.pack.age.ps.Pre2*Source*", //
                      "pp.pack.age.ps.*Source*Suf1", //
                      "pp.pack.age.ps.*Source*Suf2");
    }

    @Test
    public void should_add_package_to_cut_patterns_when_so_requested() throws Exception
    {
        // given
        TestClassNamePattern pattern = new TestClassNamePattern("(Pre1|Pre2)*${srcFile}(Suf1|Suf2)", "pp", "ps");

        // when
        ClassNameEvaluation evaluation = pattern.evaluate(typeWithPackage("Pre2MyFileSuf1", "pp.pack.age.ps"));

        // then
        assertThat(evaluation.getAllCorrespondingClassPatterns(true)).hasSize(2) //
            .startsWith("pack.age.MyFile") // preferred patterns
            .contains("pack.age.File"); // other patterns
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
            assertThat(evaluation.isTestCase()).isTrue();
            assertThat(evaluation.getAllCorrespondingClassPatterns(false)).hasSize(2) //
                .startsWith("MyFile") // preferred patterns
                .contains("File"); // other patterns
        }

        // when
        ClassNameEvaluation  evaluation = pattern.evaluate(typeWithPackage("Pre2FooMyFileSuf2Bar", "pp.pack.age.ps"));

        // then
        assertThat(evaluation.isTestCase()).isTrue();
        assertThat(evaluation.getAllCorrespondingClassPatterns(false)).hasSize(3) //
            .startsWith("FooMyFile") // preferred patterns
            .contains("MyFile", "File"); // other patterns
    }

    @Test
    public void should_not_evaluate_to_test_case_when_package_name_does_not_match_test_preferences() throws Exception
    {
        // given
        TestClassNamePattern pattern = new TestClassNamePattern("${srcFile}Test", null, "test");

        // when: file name suggest a test, but package does not
        ClassNameEvaluation evaluation = pattern.evaluate(typeWithPackage("SourceTest", "pack.age"));

        // then
        assertThat(evaluation.isTestCase()).isFalse();
    }
}
