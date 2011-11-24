package org.moreunit.test.context;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

public class TestContextRuleTest
{
    @Rule
    public TestContextRule context = new TestContextRule(new ConfigExtractorThatDoesNotInteractWithWorkspace());

    @Test
    public void should_complain_when_there_is_no_context() throws Exception
    {
        List< ? extends Runnable> runnables = asList(new Runnable()
        {
            public void run()
            {
                context.getCompilationUnit("irrelevant");
            }
        }, new Runnable()
        {
            public void run()
            {
                context.assertCompilationUnit("irrelevant");
            }
        }, new Runnable()
        {
            public void run()
            {
                context.assertCompilationUnit("irrelevant");
            }
        });

        for (Runnable runnable : runnables)
        {
            try
            {
                runnable.run();
                fail();
            }
            catch (IllegalStateException e)
            {
                assertThat(e.getMessage()).isEqualTo("No context defined. Are you accessing this rule from outside a test method? or from one that has no Context annotation?");
            }
        }
    }

    @Test
    @Context(mainSrc = "", testSrc = "")
    public void should_complain_when_source_is_undefined() throws Exception
    {
        try
        {
            context.getCompilationUnit("AClass");
        }
        catch (RuntimeException e)
        {
            assertThat(e.getMessage()).isEqualTo("No compilation unit defined with name: AClass");
        }
    }

    @Test
    @Context(mainSrc = "ClassUnderTest.txt, ClassUnderTest2.txt")
    public void should_create_production_sources() throws Exception
    {
        assertThat(context.getCompilationUnitHandler("ClassUnderTest").getInitialSource()).isEqualTo("Content of ClassUnderTest.txt");
        assertThat(context.getCompilationUnitHandler("ClassUnderTest2").getInitialSource()).isEqualTo("Content of ClassUnderTest2.txt");
    }

    @Test
    @Context(mainSrc = "TestCase.txt, TestCase2.txt")
    public void should_create_test_sources() throws Exception
    {
        assertThat(context.getCompilationUnitHandler("TestCase").getInitialSource()).isEqualTo("Content of TestCase.txt");
        assertThat(context.getCompilationUnitHandler("TestCase2").getInitialSource()).isEqualTo("Content of TestCase2.txt");
    }
}
