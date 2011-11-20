package org.moreunit.test;

import static com.google.common.base.Preconditions.checkState;
import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.jdt.core.JavaModelException;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.google.common.base.Strings;

/**
 * A rule that loads the context associated with the current test method (when
 * annotated with {@link Context}) and then:
 * <ul>
 * <li>creates the initial class under test if any is defined,</li>
 * <li>creates the initial test case if any is defined,</li>
 * <li>allows for easily comparing the result with the expected result.</li>
 * </ul>
 */
public class TestContextRule implements MethodRule
{
    private StatementInContext currentStatement;

    public final Statement apply(Statement statement, FrameworkMethod method, Object testCase)
    {
        Context context = method.getAnnotation(Context.class);
        if(context == null)
        {
            return statement;
        }

        return new StatementInContext(statement, this, context, testCase.getClass());
    }

    public final ManagedClass getClassUnderTest()
    {
        return currentStatement().getClassUnderTest();
    }

    private StatementInContext currentStatement()
    {
        checkState(currentStatement != null, "No context defined. Are you accessing this rule from outside a test method? or from one that has no Context annotation?");
        return currentStatement;
    }

    public final String getClassUnderTestSource()
    {
        return currentStatement().getClassUnderTest().getInitialSource();
    }

    public final String getExpectedTestCaseSource()
    {
        return currentStatement().getExpectedTestCaseSource();
    }

    public final ManagedClass getTestCase()
    {
        return currentStatement().getTestCase();
    }

    public final String getTestCaseSource()
    {
        return currentStatement().getTestCase().getInitialSource();
    }

    protected ManagedClass newManagedClass(SourceFolderConfiguration projectConfiguration, String definitionLocation, Class< ? > loadingClass)
    {
        return new ManagedClass(projectConfiguration, definitionLocation, loadingClass);
    }

    public void assertExpectedTestCase()
    {
        try
        {
            String source = getTestCase().getCompilationUnit().getSource();
            assertThat(normalizeSpaces(source)).isEqualTo(normalizeSpaces(getExpectedTestCaseSource()));
        }
        catch (JavaModelException e)
        {
            throw new RuntimeException("Could not load test case source", e);
        }
    }

    private String normalizeSpaces(String source)
    {
        return source.replaceAll("\\s+", " ");
    }

    private static class StatementInContext extends Statement
    {
        private final Statement statement;
        private final TestContextRule rule;
        private final Context context;
        private final Class< ? > testClass;

        private final WorkspaceConfiguration workspaceConfiguration = new WorkspaceConfiguration();
        private final ManagedClass classUnderTest;
        private final ManagedClass testCase;

        public StatementInContext(Statement statement, TestContextRule rule, Context context, Class< ? > testClass)
        {
            this.statement = statement;
            this.rule = rule;
            this.context = context;
            this.testClass = testClass;

            classUnderTest = createElement(workspaceConfiguration.getProductionSourceConfig(), context.cutDefinition());
            testCase = createElement(workspaceConfiguration.getTestSourceConfig(), context.testCaseDefinition());
        }

        private ManagedClass createElement(SourceFolderConfiguration projectConfiguration, String definitionLocation)
        {
            if(! Strings.isNullOrEmpty(definitionLocation))
            {
                try
                {
                    return rule.newManagedClass(projectConfiguration, definitionLocation, testClass).create();
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Could not create type defined at " + definitionLocation, e);
                }
            }
            return null;
        }

        @Override
        public void evaluate() throws Throwable
        {
            rule.currentStatement = this;
            try
            {
                statement.evaluate();
            }
            finally
            {
                rule.currentStatement = null;
            }
        }

        public ManagedClass getClassUnderTest()
        {
            checkDefined(classUnderTest, "class under test (cutDefinition)");
            return classUnderTest;
        }

        private void checkDefined(ManagedClass managedClass, String locationDescription)
        {
            if(managedClass == null)
            {
                throwUndefinedException(locationDescription);
            }
        }

        private void throwUndefinedException(String locationDescription)
        {
            throw new RuntimeException(String.format("No %s defined", locationDescription));
        }

        public String getExpectedTestCaseSource()
        {
            if(Strings.isNullOrEmpty(context.expectedTestCase()))
            {
                throwUndefinedException("expected test case (expectedTestCase)");
            }
            return new ManagedClass(workspaceConfiguration.getTestSourceConfig(), context.expectedTestCase(), testClass).getInitialSource();
        }

        public ManagedClass getTestCase()
        {
            checkDefined(testCase, "test case (testCaseDefinition)");
            return testCase;
        }
    }
}
