package org.moreunit.test.context;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class TestContextRuleTest
{
    @RegisterExtension
    public TestContextRule context = new TestContextRule(new ConfigExtractorThatDoesNotInteractWithWorkspace());

    @Test
    public void should_complain_when_there_is_no_context() throws Exception
    {
        List< ? extends Runnable> runnables = asList(new Runnable()
        {
            @Override
            public void run()
            {
                context.getCompilationUnit("irrelevant");
            }
        }, new Runnable()
        {
            @Override
            public void run()
            {
                context.assertCompilationUnit("irrelevant");
            }
        }, new Runnable()
        {
            @Override
            public void run()
            {
                context.assertCompilationUnit("irrelevant");
            }
        });

        for (Runnable runnable : runnables)
        {
            {
                IllegalStateException e = assertThrows(IllegalStateException.class, () -> runnable.run());
                assertEquals("No context defined. Are you accessing this extension from outside a test method? or from one that has no Context annotation?", e.getMessage());
            }
        }
    }

    @Test
    @Context(mainSrc = "", testSrc = "")
    public void should_complain_when_source_is_undefined() throws Exception
    {
        {
            Exception e = assertThrows(Exception.class, () -> context.getCompilationUnit("AClass"));
            assertEquals("No compilation unit defined with name: AClass", e.getMessage());
        }
    }

    @Test
    @Context(mainSrc = "ClassUnderTest.txt, ClassUnderTest2.txt")
    public void should_create_production_sources() throws Exception
    {
        assertEquals(context.getCompilationUnitHandler("ClassUnderTest").getInitialSource(), "Content of ClassUnderTest.txt");
        assertEquals(context.getCompilationUnitHandler("ClassUnderTest2").getInitialSource(), "Content of ClassUnderTest2.txt");
    }

    @Test
    @Context(mainSrc = "TestCase.txt, TestCase2.txt")
    public void should_create_test_sources() throws Exception
    {
        assertEquals(context.getCompilationUnitHandler("TestCase").getInitialSource(), "Content of TestCase.txt");
        assertEquals(context.getCompilationUnitHandler("TestCase2").getInitialSource(), "Content of TestCase2.txt");
    }
}
