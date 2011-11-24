package org.moreunit.test.context;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;

@Context(mainSrc = "DefaultClassUnderTest.txt",
        testSrc = "DefaultTestCase.txt")
public class TestContextRuleWithClassAnnotationTest
{
    @Rule
    public TestContextRule context = new TestContextRule(new ConfigExtractorThatDoesNotInteractWithWorkspace());

    @Test
    @Context(mainSrc = "ClassUnderTest.txt",
            testSrc = "TestCase.txt")
    public void should_load_method_context_when_present() throws Exception
    {
        assertThat(context.getCompilationUnitHandler("ClassUnderTest").getInitialSource()).isEqualTo("Content of ClassUnderTest.txt");
        assertThat(context.getCompilationUnitHandler("TestCase").getInitialSource()).isEqualTo("Content of TestCase.txt");
    }

    @Test
    public void should_load_class_context_when_no_method_context() throws Exception
    {
        assertThat(context.getCompilationUnitHandler("DefaultClassUnderTest").getInitialSource()).isEqualTo("Content of DefaultClassUnderTest.txt");
        assertThat(context.getCompilationUnitHandler("DefaultTestCase").getInitialSource()).isEqualTo("Content of DefaultTestCase.txt");
    }
}
