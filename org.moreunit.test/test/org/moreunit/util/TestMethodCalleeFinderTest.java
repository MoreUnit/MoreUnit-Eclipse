package org.moreunit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.moreunit.util.CollectionUtils.asSet;

import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.test.SimpleProjectTestCase;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;
import org.moreunit.test.workspace.WorkspaceHelper;

@Preferences(testClassSuffixes="Test", testSourcefolder="test")
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
    public void testGetMatchesWithoutMethodUnderTest() throws JavaModelException
    {
        MethodHandler testMethod = testCase.addMethod("public int testGetNumberOne()");
        //IMethod testMethod = createTestMethodWithContent("");

        Set<IMethod> matches = new TestMethodCalleeFinder(testMethod.get(), asSet(cutHello1.get(), cutHello2.get())).getMatches(new NullProgressMonitor());
        assertTrue(matches.isEmpty());
    }

    @Test
    public void testGetMatchesWithOneMethodUnderTest() throws JavaModelException
    {
        MethodHandler testMethod = testCase.addMethod("public int testGetNumberOne()", "new Hello1().getNumber1();");

        Set<IMethod> matches = new TestMethodCalleeFinder(testMethod.get(), asSet(cutHello1.get(), cutHello2.get())).getMatches(new NullProgressMonitor());
        assertEquals(1, matches.size());
        assertEquals("getNumber1", ((IMethod)matches.toArray()[0]).getElementName());
    }

    @Test
    public void testGetMatchesWithBothMethodsUnderTestAndMethodsNotUnderTest() throws JavaModelException
    {
        String methodContent = "new Hello2().getNumberOne();\n" +
                               "new HelloTest().testGetNumberOne();";
        MethodHandler testMethod = testCase.addMethod("public int testGetNumberOne()", methodContent);

        Set<IMethod> matches = new TestMethodCalleeFinder(testMethod.get(), asSet(cutHello1.get(), cutHello2.get())).getMatches(new NullProgressMonitor());
        assertEquals(1, matches.size());
    }

    @Test
    public void testGetMatchesWithSeveralMethodsUnderTest() throws JavaModelException
    {
        String methodContent = "new Hello1().getNumber1();\n" +
                               "new Hello2().getNumberOne();\n" +
                               "new Hello2().getNumberOneAgain();";
        MethodHandler testMethod = testCase.addMethod("public int testGetNumberOne()", methodContent);

        Set<IMethod> matches = new TestMethodCalleeFinder(testMethod.get(), asSet(cutHello1.get(), cutHello2.get())).getMatches(new NullProgressMonitor());
        assertEquals(3, matches.size());
    }
}
