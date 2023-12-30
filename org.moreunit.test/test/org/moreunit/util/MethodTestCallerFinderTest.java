package org.moreunit.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.configs.SimpleJUnit3Preferences;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;

@Context(mainCls = "testing:Hello", testCls = "testing:HelloTest", preferences = @Preferences(SimpleJUnit3Preferences.class))
public class MethodTestCallerFinderTest extends ContextTestCase
{
    private TypeHandler cutType;
    private TypeHandler testcaseType;
    private MethodHandler getNumberOneMethod;

    @Before
    public void setUp() throws JavaModelException
    {
        cutType = context.getCompilationUnitHandler("testing.Hello").getPrimaryTypeHandler();
        testcaseType = context.getCompilationUnitHandler("testing.HelloTest").getPrimaryTypeHandler();
        getNumberOneMethod = cutType.addMethod("public int getNumberOne()", "return 1;");
    }

    @Test
    public void getMatches_should_return_empty_list_when_no_testmethod_exists() throws JavaModelException
    {
        Set<IMethod> matches = new MethodTestCallerFinder(getNumberOneMethod.get(), Set.of(testcaseType.get())).getMatches(new NullProgressMonitor());
        assertThat(matches).isEmpty();
    }

    @Test
    public void getMatches_should_find_only_existing_testmethod() throws JavaModelException
    {
        MethodHandler giveMe1TestMethod = testcaseType.addMethod("public void testGiveMe1()", "new Hello().getNumberOne();");

        Set<IMethod> matches = new MethodTestCallerFinder(getNumberOneMethod.get(), Set.of(testcaseType.get())).getMatches(new NullProgressMonitor());
        assertThat(matches).contains(giveMe1TestMethod.get());
    }

    @Test
    public void getMatches_should_find_all_testmethods() throws JavaModelException
    {
        MethodHandler giveMe1TestMethod = testcaseType.addMethod("public void testGiveMe1()", "new Hello().getNumberOne();");
        MethodHandler gimme1TestMethod = testcaseType.addMethod("public void testGimme1()", "new Hello().getNumberOne();");
        MethodHandler getNumber1TestMethod = testcaseType.addMethod("public void testGetNumber1()", "new Hello().getNumberOne();");

        Set<IMethod> matches = new MethodTestCallerFinder(getNumberOneMethod.get(), Set.of(testcaseType.get())).getMatches(new NullProgressMonitor());
        assertThat(matches).containsOnly(giveMe1TestMethod.get(), gimme1TestMethod.get(), getNumber1TestMethod.get());
    }
}
