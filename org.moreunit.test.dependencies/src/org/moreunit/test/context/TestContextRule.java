package org.moreunit.test.context;

import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.Annotation;

import org.eclipse.jdt.core.ICompilationUnit;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.moreunit.test.workspace.CompilationUnitAssertions;
import org.moreunit.test.workspace.CompilationUnitHandler;
import org.moreunit.test.workspace.ProjectHandler;
import org.moreunit.test.workspace.TypeHandler;
import org.moreunit.test.workspace.WorkspaceHandler;

/**
 * A rule that loads the context associated with the current test method (when
 * annotated with {@link Context}) and then:
 * <ul>
 * <li>creates the initial production compilation units if any are defined,</li>
 * <li>creates the initial test compilation units if any are defined,</li>
 * <li>provides assertions to run against those compilation units.</li>
 * </ul>
 */
public class TestContextRule implements MethodRule
{
    private final AnnotationConfigExtractor configExtractor;
    private StatementInContext currentStatement;

    public TestContextRule()
    {
        this(new AnnotationConfigExtractor());
    }

    TestContextRule(AnnotationConfigExtractor configExtractor)
    {
        this.configExtractor = configExtractor;
    }

    public final Statement apply(Statement statement, final FrameworkMethod method, final Object testCase)
    {
        WorkspaceConfiguration config = configExtractor.extractFrom(new AnnotatedElement()
        {
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
            {
                return method.getAnnotation(annotationClass);
            }

            @Override
            public String toString()
            {
                return "method " + method.getName();
            }
        }, new AnnotatedElement()
        {
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
            {
                return testCase.getClass().getAnnotation(annotationClass);
            }
        });

        if(config == null)
        {
            return statement;
        }

        return new StatementInContext(statement, this, config, testCase.getClass());
    }

    /**
     * Returns a handler to manipulate the compilation unit having the given
     * name.
     * 
     * @param cuName the name of the compilation unit
     * @return a handler for the compilation unit
     * @throws IllegalArgumentException if no compilation unit exists with the
     *             given name
     */
    public final CompilationUnitHandler getCompilationUnitHandler(String cuName)
    {
        return currentStatement().workspaceHandler.getCompilationUnitHandler(cuName);
    }

    /**
     * Returns a handler to manipulate the primary type of the given compilation
     * unit.
     * 
     * @param cuName the name of the compilation unit
     * @return a handler for the compilation unit's primary type
     * @throws IllegalArgumentException if no compilation unit exists with the
     *             given name
     */
    public final TypeHandler getPrimaryTypeHandler(String cuName)
    {
        return getCompilationUnitHandler(cuName).getPrimaryTypeHandler();
    }

    /**
     * Returns the compilation unit having the given name.
     * 
     * @param cuName the name of the compilation unit
     * @return the compilation unit
     * @throws IllegalArgumentException if no compilation unit exists with the
     *             given name
     */
    public final ICompilationUnit getCompilationUnit(String cuName)
    {
        return getCompilationUnitHandler(cuName).get();
    }

    /**
     * Returns a handler to manipulate the project created for this test, via
     * the annotation @Context, or using the annotation @Project - assuming no
     * specific name was given to it. If a specific name was given to it, please
     * use {@link #getProjectHandler(String)}.
     * 
     * @return a handler for the project
     */
    public ProjectHandler getProjectHandler()
    {
        return getProjectHandler(Defaults.PROJECT_NAME);
    }

    /**
     * Returns a handler to manipulate the test project created for this test,
     * using the annotation @TestProject - assuming no specific name was given
     * to it. If a specific name was given to it, please use
     * {@link #getProjectHandler(String)}.
     * 
     * @return a handler for the test project
     * @throws IllegalArgumentException if no test project exist
     */
    public ProjectHandler getTestProjectHandler()
    {
        return getProjectHandler(Defaults.TEST_PROJECT_NAME);
    }

    /**
     * Returns a handler to manipulate the project created with the given name.
     * 
     * @param projectName the project name
     * @return a handler for the project
     * @throws IllegalArgumentException if no project exists with the given name
     */
    public ProjectHandler getProjectHandler(String projectName)
    {
        return currentStatement().workspaceHandler.getProjectHandler(projectName);
    }

    private StatementInContext currentStatement()
    {
        checkState(currentStatement != null, "No context defined. Are you accessing this rule from outside a test method? or from one that has no Context annotation?");
        return currentStatement;
    }

    /**
     * Returns assertions that can be made on the compilation unit having the
     * given name.
     * 
     * @param cuName the name of the compilation unit
     * @return assertions for the compilation unit
     * @throws IllegalArgumentException if no compilation unit exists with the
     *             given name
     */
    public CompilationUnitAssertions assertCompilationUnit(String cuName)
    {
        return getCompilationUnitHandler(cuName).assertThat();
    }

    private static class StatementInContext extends Statement
    {
        private final Statement statement;
        private final TestContextRule rule;
        private final WorkspaceHandler workspaceHandler;

        public StatementInContext(Statement statement, TestContextRule rule, WorkspaceConfiguration config, Class< ? > testClass)
        {
            this.statement = statement;
            this.rule = rule;
            this.workspaceHandler = config.initWorkspace(testClass);
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
                workspaceHandler.clearWorkspace();
            }
        }
    }
}
