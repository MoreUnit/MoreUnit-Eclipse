package org.moreunit.test.context;

import static org.moreunit.core.util.Preconditions.checkState;

import java.lang.annotation.Annotation;

import org.eclipse.jdt.core.ICompilationUnit;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.moreunit.test.workspace.CompilationUnitAssertions;
import org.moreunit.test.workspace.CompilationUnitHandler;
import org.moreunit.test.workspace.ProjectHandler;
import org.moreunit.test.workspace.TypeHandler;
import org.moreunit.test.workspace.WorkspaceHandler;

/**
 * A JUnit 5 extension that loads the context associated with the current test method (when
 * annotated with {@link Context}) and then:
 * <ul>
 * <li>creates the initial production compilation units if any are defined,</li>
 * <li>creates the initial test compilation units if any are defined,</li>
 * <li>provides assertions to run against those compilation units.</li>
 * </ul>
 */
public class TestContextRule implements BeforeEachCallback, AfterEachCallback
{
    private final AnnotationConfigExtractor configExtractor;
    private final ThreadLocal<ContextState> state = new ThreadLocal<>();

    public TestContextRule()
    {
        this(new AnnotationConfigExtractor());
    }

    TestContextRule(AnnotationConfigExtractor configExtractor)
    {
        this.configExtractor = configExtractor;
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception
    {
        // Extract config from method and class annotations
        WorkspaceConfiguration config = configExtractor.extractFrom(new AnnotatedElement()
        {
            @Override
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
            {
                return context.getTestMethod().map(m -> m.getAnnotation(annotationClass)).orElse(null);
            }

            @Override
            public String toString()
            {
                return "method " + context.getTestMethod().map(m -> m.getName()).orElse("unknown");
            }
        }, new AnnotatedElement()
        {
            @Override
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
            {
                Class<?> testClass = context.getTestClass().orElse(null);
                if (testClass == null)
                {
                    return null;
                }

                T annotation = null;
                Class<?> cls = testClass;
                while (annotation == null && cls != null)
                {
                    annotation = cls.getAnnotation(annotationClass);
                    cls = cls.getSuperclass();
                }
                return annotation;
            }
        });

        if (config == null)
        {
            return;
        }

        // Abbreviate to prevent reaching file name size limit on some file systems
        String projectPrefix = abbreviate(context.getTestClass().map(c -> c.getName()).orElse("")) + "."
                + abbreviate(context.getTestMethod().map(m -> m.getName()).orElse("")) + "-";

        ContextState newState = new ContextState(config, context.getTestClass().map(c -> c).orElse(null), projectPrefix);
        state.set(newState);
        newState.initWorkspace();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception
    {
        ContextState current = state.get();
        if (current != null)
        {
            try
            {
                current.clearWorkspace();
            }
            finally
            {
                state.remove();
            }
        }
    }

    private static String abbreviate(String javaIdentifier)
    {
        StringBuilder b = new StringBuilder();
        for (String part : javaIdentifier.split("((?=\\p{Lu})|[\\._])"))
        {
            if(! part.isEmpty())
            {
                if(b.length() != 0)
                {
                    b.append(".");
                }
                b.append(part.substring(0, Math.min(2, part.length())));
            }
        }
        return b.toString();
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
        return getWorkspaceHandler().getCompilationUnitHandler(cuName);
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
        return getMainProjectHandler();
    }

    /**
     * Returns a handler to manipulate the project created for this test, via
     * the annotation @Context, or using the annotation @Project - assuming no
     * specific name was given to it. If a specific name was given to it, please
     * use {@link #getProjectHandler(String)}.
     *
     * @return a handler for the project
     */
    public ProjectHandler getMainProjectHandler()
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
        return getWorkspaceHandler().getProjectHandler(projectName);
    }

    public WorkspaceHandler getWorkspaceHandler()
    {
        return currentState().workspaceHandler;
    }

    private ContextState currentState()
    {
        ContextState s = state.get();
        checkState(s != null, "No context defined. Are you accessing this extension from outside a test method? or from one that has no Context annotation?");
        return s;
    }

    public boolean isDefined()
    {
        return state.get() != null;
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

    private static class ContextState
    {
        final WorkspaceHandler workspaceHandler;

        ContextState(WorkspaceConfiguration config, Class<?> testClass, String projectPrefix)
        {
            this.workspaceHandler = config.initWorkspace(testClass, projectPrefix);
        }

        void initWorkspace()
        {
            // Workspace is already initialized in constructor
        }

        void clearWorkspace()
        {
            workspaceHandler.clearWorkspace();
        }
    }
}
