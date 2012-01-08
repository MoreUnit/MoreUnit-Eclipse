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
import org.moreunit.test.workspace.WorkspaceHelper;

public class TestMethodCalleeFinderTest extends SimpleProjectTestCase
{
    private IType cutType1;
    private IType cutType2;
    private IType testcaseType;
    private IMethod getNumber1;
    private IMethod getNumberOne;
    private IMethod getNumberOneAgain;

    @Before
    public void setUp() throws JavaModelException
    {
        cutType1 = createJavaClass("Hello1", true);
        getNumber1 = WorkspaceHelper.createMethodInJavaType(cutType1, "public void getNumber1()", "return 1;");
        cutType2 = createJavaClass("Hello2", true);
        getNumberOne = WorkspaceHelper.createMethodInJavaType(cutType2, "public void getNumberOne()", "return 1;");
        getNumberOneAgain = WorkspaceHelper.createMethodInJavaType(cutType2, "public void getNumberOneAgain()", "return 1;");
        testcaseType = createTestCase("HelloTest", true);
    }

    @Test
    public void testGetMatchesWithoutMethodUnderTest() throws JavaModelException
    {
        IMethod testMethod = createTestMethodWithContent("");

        Set<IMethod> matches = new TestMethodCalleeFinder(testMethod, asSet(cutType1, cutType2)).getMatches(new NullProgressMonitor());
        assertTrue(matches.isEmpty());
    }

    private IMethod createTestMethodWithContent(String methodSourceCode) throws JavaModelException
    {
        return WorkspaceHelper.createMethodInJavaType(testcaseType, "public int testGetNumberOne()", methodSourceCode);
    }

    @Test
    public void testGetMatchesWithOneMethodUnderTest() throws JavaModelException
    {
        IMethod testMethod = createTestMethodWithContent("new Hello1().getNumber1();");

        Set<IMethod> matches = new TestMethodCalleeFinder(testMethod, asSet(cutType1, cutType2)).getMatches(new NullProgressMonitor());
        assertEquals(asSet(getNumber1), matches);
    }

    @Test
    public void testGetMatchesWithBothMethodsUnderTestAndMethodsNotUnderTest() throws JavaModelException
    {
        IMethod testMethod = createTestMethodWithContent(
            "new Hello2().getNumberOne();"
          + "new HelloTest().testGetNumberOne();"
        );

        Set<IMethod> matches = new TestMethodCalleeFinder(testMethod, asSet(cutType1, cutType2)).getMatches(new NullProgressMonitor());
        assertEquals(asSet(getNumberOne), matches);
    }

    @Test
    public void testGetMatchesWithSeveralMethodsUnderTest() throws JavaModelException
    {
        IMethod testMethod = createTestMethodWithContent(
            "new Hello1().getNumber1();"
          + "new Hello2().getNumberOne();"
          + "new Hello2().getNumberOneAgain();"
        );

        Set<IMethod> matches = new TestMethodCalleeFinder(testMethod, asSet(cutType1, cutType2)).getMatches(new NullProgressMonitor());
        assertEquals(asSet(getNumber1, getNumberOne, getNumberOneAgain), matches);
    }
}
