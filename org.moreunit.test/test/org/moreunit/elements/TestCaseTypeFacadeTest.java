package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:13:50
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.SimpleProjectTestCase;
import org.moreunit.WorkspaceHelper;

public class TestCaseTypeFacadeTest extends SimpleProjectTestCase
{

    private IType cutType;
    private IType testcaseType;

    @Before
    public void setUp() throws JavaModelException
    {
        cutType = createJavaClass("Hello", true);
        testcaseType = createTestCase("HelloTest", true);
    }

    @Test
    public void testGetCorrespondingTestedMethod() throws CoreException
    {
        IMethod testedMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
        IMethod testMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public void testGetNumberOne()", "");
        IMethod testMethodWithNoCorrespondingTestedMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public void testAnything()", "");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseType.getCompilationUnit());
        assertEquals(testedMethod, testCaseTypeFacade.getCorrespondingTestedMethod(testMethod, cutType));
        assertNull(testCaseTypeFacade.getCorrespondingTestedMethod(testMethodWithNoCorrespondingTestedMethod, cutType));
    }

    @Test
    public void testGetCorrespondingTestedMethods() throws CoreException
    {
        IMethod testedMethod1 = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
        IType cutType2 = createJavaClass("Hello2", true);
        IMethod testedMethod2 = WorkspaceHelper.createMethodInJavaType(cutType2, "public int getNumberOne()", "return 1;");
        IMethod testMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public void testGetNumberOne()", "");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseType.getCompilationUnit());

        Set<IType> classesUnderTest = new LinkedHashSet<IType>(Arrays.asList(cutType, cutType2));
        List<IMethod> correspondingTestMethods = testCaseTypeFacade.getCorrespondingTestedMethods(testMethod, classesUnderTest);
        assertEquals(2, correspondingTestMethods.size());
        assertTrue(correspondingTestMethods.contains(testedMethod1));
        assertTrue(correspondingTestMethods.contains(testedMethod2));
    }

    @Test
    public void testgetOneCorrespondingMemberWithoutTestMethod() throws CoreException
    {
        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseType.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(null, false, false, null);

        assertEquals(cutType, oneCorrespondingMemberUnderTest);
    }

    @Test
    public void testgetOneCorrespondingMemberWithTestMethod() throws CoreException
    {
        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
        IMethod getNumberOneTestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne()", "");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseType.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(getNumberOneTestMethod, false, false, null);

        assertEquals(getNumberOneMethod, oneCorrespondingMemberUnderTest);
    }

    @Test
    public void testgetOneCorrespondingMemberWithExtendedSearchAndTestMethodFollowingNamingPattern() throws CoreException
    {
        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
        IMethod getNumberOneTestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne()", "");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseType.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(getNumberOneTestMethod, false, true, null);

        assertEquals(getNumberOneMethod, oneCorrespondingMemberUnderTest);
    }

    @Test
    public void testgetOneCorrespondingMemberWithExtendedSearchAndTestMethodCallingMethodUnderTest() throws CoreException
    {
        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
        IMethod giveMe1TestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGiveMe1()", "new Hello().getNumberOne();");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseType.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(giveMe1TestMethod, false, true, null);

        assertEquals(getNumberOneMethod, oneCorrespondingMemberUnderTest);
    }

    @Test
    public void testgetOneCorrespondingMemberWithExtendedSearchAndTestMethodFollowingNamingPatternAndCallingMethodUnderTest() throws CoreException
    {
        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
        IMethod getNumberOneTestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne()", "new Hello().getNumberOne();");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseType.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(getNumberOneTestMethod, false, true, null);

        assertEquals(getNumberOneMethod, oneCorrespondingMemberUnderTest);
    }
}
