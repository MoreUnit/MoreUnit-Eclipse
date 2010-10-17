package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:22:53
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.SimpleProjectTestCase;
import org.moreunit.WorkspaceHelper;
import org.moreunit.util.StringConstants;

public class ClassTypeFacadeTest extends SimpleProjectTestCase
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
    public void testGetOneCorrespondingTestCase() throws CoreException
    {
        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        IType oneCorrespondingTestCase = classTypeFacade.getOneCorrespondingTestCase(false);

        assertEquals(testcaseType, oneCorrespondingTestCase);
    }

    @Test
    public void testGetOneCorrespondingTestCaseWithEnum() throws CoreException
    {
        String sourceCode = getEnumSourceFile();
        ICompilationUnit compilationUnit = sourcesPackage.createCompilationUnit("MyEnum.java", sourceCode, false, null);

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(compilationUnit);
        assertEquals(0, classTypeFacade.getCorrespondingTestCaseList().size());

        // cleanup
        compilationUnit.delete(true, null);
    }

    private String getEnumSourceFile()
    {
        StringBuffer source = new StringBuffer();
        source.append("package com;").append(StringConstants.NEWLINE);
        source.append("public enum MyEnum {").append(StringConstants.NEWLINE);
        source.append("}").append(StringConstants.NEWLINE);

        return source.toString();
    }

    @Test
    public void testGetCorrespondingTestMethodWithTestMethod() throws CoreException
    {
        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
        IMethod getNumberOneTestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne()", "");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        IMethod correspondingTestMethod = classTypeFacade.getCorrespondingTestMethod(getNumberOneMethod, testcaseType);
        assertEquals(getNumberOneTestMethod, correspondingTestMethod);
    }

    @Test
    public void testHasTestMethodWithTestMethod() throws CoreException
    {
        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
        WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne()", "");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        assertTrue(classTypeFacade.hasTestMethod(getNumberOneMethod));
    }

    @Test
    public void testGetCorrespondingTestMethodWithoutTestMethod() throws JavaModelException
    {
        IMethod methodWithoutCorrespondingTestMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberTwo()", "");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        assertNull(classTypeFacade.getCorrespondingTestMethod(methodWithoutCorrespondingTestMethod, testcaseType));
    }

    @Test
    public void testHasTestMethodWithoutTestMethod() throws CoreException
    {
        IMethod methodWithoutCorrespondingTestMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberTwo()", "");
        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        assertFalse(classTypeFacade.hasTestMethod(methodWithoutCorrespondingTestMethod));
    }

    @Test
    public void testGetCorrespondingTestMethods() throws CoreException
    {
        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
        IMethod getNumberOneTestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne()", "");
        IMethod getNumberOneTestMethod2 = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne2()", "");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        List<IMethod> correspondingTestMethods = classTypeFacade.getCorrespondingTestMethods(getNumberOneMethod);
        assertEquals(2, correspondingTestMethods.size());
        assertTrue(correspondingTestMethods.contains(getNumberOneTestMethod));
        assertTrue(correspondingTestMethods.contains(getNumberOneTestMethod2));
    }

    @Test
    public void testGetOneCorrespondingMemberWithoutTestMethod() throws CoreException
    {
        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(null, false, false, null);

        assertEquals(testcaseType, oneCorrespondingTestMember);
    }

    @Test
    public void testGetOneCorrespondingMemberWithTestMethod() throws CoreException
    {
        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
        IMethod getNumberOneTestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne()", "");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(getNumberOneMethod, false, false, null);

        assertEquals(getNumberOneTestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void testGetOneCorrespondingMemberWithExtendedSearchAndTestMethodFollowingNamingPattern() throws CoreException
    {
        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
        IMethod getNumberOneTestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne()", "");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(getNumberOneMethod, false, true, null);

        assertEquals(getNumberOneTestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void testGetOneCorrespondingMemberWithExtendedSearchAndTestMethodCallingMethodUnderTest() throws CoreException
    {
        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
        IMethod giveMe1TestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGiveMe1()", "new Hello().getNumberOne();");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(getNumberOneMethod, false, true, null);

        assertEquals(giveMe1TestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void testGetOneCorrespondingMemberWithExtendedSearchAndTestMethodFollowingNamingPatternAndCallingMethodUnderTest() throws CoreException
    {
        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
        IMethod getNumberOneTestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne()", "new Hello().getNumberOne();");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(getNumberOneMethod, false, true, null);

        assertEquals(getNumberOneTestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void testGetOneCorrespondingMemberWithMethodUnderTestOverridingAnotherMethodUnderTest() throws JavaModelException
    {
        IType type = createJavaClass("AType", true);
        WorkspaceHelper.createMethodInJavaType(type, "public void doIt()", "// does it");
        IType typeTest = createTestCase("ATypeTest", true);
        WorkspaceHelper.createMethodInJavaType(typeTest, "public void testDoIt()", "new AType().doIt();");

        IType subType = deleteAfterTest(WorkspaceHelper.createJavaClassExtending(sourcesPackage, "ASubType", "AType"));
        IMethod overridingMethod = WorkspaceHelper.createMethodInJavaType(subType, "public void doIt()", "// does it in another way");
        IType subTypeTest = createTestCase("ASubTypeTest", true);
        IMethod overridingMethodTest = WorkspaceHelper.createMethodInJavaType(subTypeTest, "public void testDoIt()", "new ASubType().doIt();");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(subType.getCompilationUnit());
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(overridingMethod, false, true, null);

        assertEquals(overridingMethodTest, oneCorrespondingTestMember);
    }
}
