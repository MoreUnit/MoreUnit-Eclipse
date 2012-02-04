package org.moreunit.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.moreunit.util.CollectionUtils.asSet;

import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;

@Preferences(testClassSuffixes="Test", testSrcFolder="test")
@Project(mainSrc="TestMethodCalleeFinder_class1.java.txt,TestMethodCalleeFinder_class2.java.txt", testCls="testing:HelloTest", mainSrcFolder="src", testSrcFolder="test")
public class TestMethodCalleeFinderTest extends ContextTestCase
{
    private TypeHandler cutHello1;
    private TypeHandler cutHello2;
    private TypeHandler testCase;

    @Before
    public void init()
    {
        cutHello1 = context.getPrimaryTypeHandler("testing.Hello1");
        cutHello2 = context.getPrimaryTypeHandler("testing.Hello2");
        testCase = context.getPrimaryTypeHandler("testing.HelloTest");
    }

    @Test
    public void getMatches_should_return_empty_list_when_no_method_is_called() throws JavaModelException
    {
        MethodHandler testMethod = testCase.addMethod("public int testGetNumberOne()");

        Set<IMethod> matches = new TestMethodCalleeFinder(testMethod.get(), asSet(cutHello1.get(), cutHello2.get())).getMatches(new NullProgressMonitor());
        assertThat(matches).isEmpty();
    }

    @Test
    public void getMatches_should_return_only_existing_callee() throws JavaModelException
    {
        MethodHandler testMethod = testCase.addMethod("public int testGetNumberOne()", "new Hello1().getNumber1();");

        Set<IMethod> matches = new TestMethodCalleeFinder(testMethod.get(), asSet(cutHello1.get(), cutHello2.get())).getMatches(new NullProgressMonitor());
        assertThat(matches).hasSize(1);
        assertThat(((IMethod)matches.toArray()[0]).getElementName()).isEqualTo("getNumber1");
    }

    @Test
    public void getMatches_should_not_return_testmethods_called_in_method() throws JavaModelException
    {
        String methodContent = "new Hello2().getNumberOne();\n" +
                               "new HelloTest().testGetNumberOne();";
        MethodHandler testMethod = testCase.addMethod("public int testGetNumberOne()", methodContent);

        Set<IMethod> matches = new TestMethodCalleeFinder(testMethod.get(), asSet(cutHello1.get(), cutHello2.get())).getMatches(new NullProgressMonitor());
        assertThat(matches).hasSize(1);
    }

    @Test
    public void getMatches_should_return_all_callees_when_more_than_one_exist() throws JavaModelException
    {
        String methodContent = "new Hello1().getNumber1();\n" +
                               "new Hello2().getNumberOne();\n" +
                               "new Hello2().getNumberOneAgain();";
        MethodHandler testMethod = testCase.addMethod("public int testGetNumberOne()", methodContent);

        Set<IMethod> matches = new TestMethodCalleeFinder(testMethod.get(), asSet(cutHello1.get(), cutHello2.get())).getMatches(new NullProgressMonitor());
        assertThat(matches).hasSize(3);
    }
}
