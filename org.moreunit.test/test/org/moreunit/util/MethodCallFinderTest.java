package org.moreunit.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.configs.SimpleJUnit3Preferences;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;

@Context(mainCls = "testing:Hello", testCls = "testing:HelloTest", preferences = @Preferences(SimpleJUnit3Preferences.class))
public class MethodCallFinderTest extends ContextTestCase
{
    private TypeHandler cutType;
    private TypeHandler testcaseType;
    private MethodHandler getNumberOneMethod;

    @BeforeEach
    public void setUp() throws JavaModelException
    {
        cutType = context.getCompilationUnitHandler("testing.Hello").getPrimaryTypeHandler();
        testcaseType = context.getCompilationUnitHandler("testing.HelloTest").getPrimaryTypeHandler();
        getNumberOneMethod = cutType.addMethod("public int getNumberOne()", "return 1;");
    }

    @Test
    public void getMatches_should_find_callers_when_methodMatch_is_true() throws JavaModelException
    {
        MethodHandler callerMethod = testcaseType.addMethod("public void someMethod()", "new Hello().getNumberOne();");

        MethodCallFinder finder = new MethodCallFinder(getNumberOneMethod.get(), Set.of(testcaseType.get())) {
            @Override
            protected boolean methodMatch(IMethod method) {
                return true;
            }
        };

        Set<IMethod> matches = finder.getMatches(new NullProgressMonitor());
        assertThat(matches).contains(callerMethod.get());
    }

    @Test
    public void getMatches_should_not_find_callers_when_methodMatch_is_false() throws JavaModelException
    {
        MethodHandler callerMethod = testcaseType.addMethod("public void someMethod()", "new Hello().getNumberOne();");

        MethodCallFinder finder = new MethodCallFinder(getNumberOneMethod.get(), Set.of(testcaseType.get())) {
            @Override
            protected boolean methodMatch(IMethod method) {
                return false;
            }
        };

        Set<IMethod> matches = finder.getMatches(new NullProgressMonitor());
        assertThat(matches).isEmpty();
    }
}
