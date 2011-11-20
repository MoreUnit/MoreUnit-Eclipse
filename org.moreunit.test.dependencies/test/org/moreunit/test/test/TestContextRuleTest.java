package org.moreunit.test.test;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.moreunit.test.Context;
import org.moreunit.test.ManagedClass;
import org.moreunit.test.SourceFolderConfiguration;
import org.moreunit.test.TestContextRule;

public class TestContextRuleTest
{
    @Rule
    public TestContextRule context = new TestContextRule()
    {
        // prevents the creation of the underlying element during this unit test
        protected ManagedClass newManagedClass(SourceFolderConfiguration projectConfiguration, String definitionLocation, Class< ? > loadingClass)
        {
            return new UncreatableManagedClass(projectConfiguration, loadingClass, definitionLocation);
        }
    };

    @Test
    public void should_complain_when_there_is_no_context() throws Exception
    {
        List< ? extends Runnable> runnables = asList(new Runnable()
        {
            public void run()
            {
                context.getClassUnderTest();
            }
        }, new Runnable()
        {
            public void run()
            {
                context.getExpectedTestCaseSource();
            }
        }, new Runnable()
        {
            public void run()
            {
                context.getTestCase();
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
    @Context(cutDefinition = "")
    public void should_complain_when_cut_definition_is_undefined() throws Exception
    {
        try
        {
            context.getClassUnderTest();
        }
        catch (RuntimeException e)
        {
            assertThat(e.getMessage()).isEqualTo("No class under test (cutDefinition) defined");
        }
    }

    @Test
    @Context(expectedTestCase = "")
    public void should_complain_when_expected_test_case_is_undefined() throws Exception
    {
        try
        {
            context.getExpectedTestCaseSource();
        }
        catch (RuntimeException e)
        {
            assertThat(e.getMessage()).isEqualTo("No expected test case (expectedTestCase) defined");
        }
    }

    @Test
    @Context(testCaseDefinition = "")
    public void should_complain_when_test_case_is_undefined() throws Exception
    {
        try
        {
            context.getTestCase();
        }
        catch (RuntimeException e)
        {
            assertThat(e.getMessage()).isEqualTo("No test case (testCaseDefinition) defined");
        }
    }

    @Test
    @Context(cutDefinition = "doesNotExist1.txt")
    public void should_complain_when_cut_definition_does_not_exist() throws Exception
    {
        try
        {
            context.getClassUnderTest();
        }
        catch (RuntimeException e)
        {
            assertThat(e.getMessage()).isEqualTo("Resource not found: 'doesNotExist1.txt'");
        }
    }

    @Test
    @Context(expectedTestCase = "doesNotExist2.txt")
    public void should_complain_when_expected_test_case_does_not_exist() throws Exception
    {
        try
        {
            context.getExpectedTestCaseSource();
        }
        catch (RuntimeException e)
        {
            assertThat(e.getMessage()).isEqualTo("Resource not found: 'doesNotExist2.txt'");
        }
    }

    @Test
    @Context(testCaseDefinition = "doesNotExist3.txt")
    public void should_complain_when_test_case_definition_does_not_exist() throws Exception
    {
        try
        {
            context.getTestCase();
        }
        catch (RuntimeException e)
        {
            assertThat(e.getMessage()).isEqualTo("Resource not found: 'doesNotExist3.txt'");
        }
    }

    @Test
    @Context(cutDefinition = "classUnderTest.txt")
    public void should_find_cut_definition() throws Exception
    {
        assertThat(context.getClassUnderTestSource()).isEqualTo("Content of classUnderTest.txt");
    }

    @Test
    @Context(expectedTestCase = "expectedTestCase.txt")
    public void should_find_expected_test_case() throws Exception
    {
        assertThat(context.getExpectedTestCaseSource()).isEqualTo("Content of expectedTestCase.txt");
    }

    @Test
    @Context(testCaseDefinition = "testCase.txt")
    public void should_find_test_case_definition() throws Exception
    {
        assertThat(context.getTestCaseSource()).isEqualTo("Content of testCase.txt");
    }

    /**
     * A ManagedClass that cannot be created.
     */
    private static class UncreatableManagedClass extends ManagedClass
    {
        public UncreatableManagedClass(SourceFolderConfiguration projectConfiguration, Class< ? > loadingClass, String location)
        {
            super(projectConfiguration, location, loadingClass);
        }

        protected ManagedClass create()
        {
            // does nothing
            return this;
        }
    }
}
