package org.moreunit.test.context;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@Context(mainSrc = "DefaultClassUnderTest.txt",
        testSrc = "DefaultTestCase.txt")
public class TestContextRuleWithClassAnnotationTest extends AnnotatedSuperClass
{
    @RegisterExtension
    public TestContextRule context = new TestContextRule(new ConfigExtractorThatDoesNotInteractWithWorkspace());

    @Test
    @Context(mainSrc = "ClassUnderTest.txt",
            testSrc = "TestCase.txt")
    public void should_load_method_context_when_present() throws Exception
    {
        assertEquals(context.getCompilationUnitHandler("ClassUnderTest").getInitialSource(), "Content of ClassUnderTest.txt");
        assertEquals(context.getCompilationUnitHandler("TestCase").getInitialSource(), "Content of TestCase.txt");
    }

    @Test
    // (also verifies that superclass context is not used)
    public void should_load_class_context_when_no_method_context() throws Exception
    {
        assertEquals(context.getCompilationUnitHandler("DefaultClassUnderTest").getInitialSource(), "Content of DefaultClassUnderTest.txt");
        assertEquals(context.getCompilationUnitHandler("DefaultTestCase").getInitialSource(), "Content of DefaultTestCase.txt");
    }
}
