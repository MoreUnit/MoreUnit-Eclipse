package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:22:53
 */

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;
import org.moreunit.SimpleProjectTestCase;
import org.moreunit.WorkspaceHelper;
import org.moreunit.util.StringConstants;
import static org.junit.Assert.*;

public class ClassTypeFacadeTest extends SimpleProjectTestCase
{

    @Test
    public void testGetOneCorrespondingTestCase() throws CoreException
    {
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
        IType testcaseType = WorkspaceHelper.createJavaClass(testPackage, "HelloTest");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        IType oneCorrespondingTestCase = classTypeFacade.getOneCorrespondingTestCase(false);

        assertEquals(testcaseType, oneCorrespondingTestCase);
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType, testcaseType});
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
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
        IType testcaseType = WorkspaceHelper.createJavaClass(testPackage, "HelloTest");

        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1");
        IMethod getNumberOneTestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne()", "");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        IMethod correspondingTestMethod = classTypeFacade.getCorrespondingTestMethod(getNumberOneMethod, testcaseType);
        assertEquals(getNumberOneTestMethod, correspondingTestMethod);
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType, testcaseType});
    }

    @Test
    public void testHasTestMethodWithTestMethod() throws CoreException
    {
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
        IType testcaseType = WorkspaceHelper.createJavaClass(testPackage, "HelloTest");

        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1");
        WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne()", "");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        assertTrue(classTypeFacade.hasTestMethod(getNumberOneMethod));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType, testcaseType});
    }

    @Test
    public void testGetCorrespondingTestMethodWithoutTestMethod() throws JavaModelException
    {
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
        IType testcaseType = WorkspaceHelper.createJavaClass(testPackage, "HelloTest");

        IMethod methodWithoutCorrespondingTestMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberTwo()", "");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        assertNull(classTypeFacade.getCorrespondingTestMethod(methodWithoutCorrespondingTestMethod, testcaseType));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType, testcaseType});
    }

    @Test
    public void testHasTestMethodWithoutTestMethod() throws CoreException
    {
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
        IType testcaseType = WorkspaceHelper.createJavaClass(testPackage, "HelloTest");

        IMethod methodWithoutCorrespondingTestMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberTwo()", "");
        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        assertFalse(classTypeFacade.hasTestMethod(methodWithoutCorrespondingTestMethod));
        
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType, testcaseType});
    }

    @Test
    public void testGetCorrespondingTestMethods() throws CoreException
    {
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
        IType testcaseType = WorkspaceHelper.createJavaClass(testPackage, "HelloTest");

        IMethod getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1");
        IMethod getNumberOneTestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne()", "");
        IMethod getNumberOneTestMethod2 = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne2()", "");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
        List<IMethod> correspondingTestMethods = classTypeFacade.getCorrespondingTestMethods(getNumberOneMethod);
        assertEquals(2, correspondingTestMethods.size());
        assertTrue(correspondingTestMethods.contains(getNumberOneTestMethod));
        assertTrue(correspondingTestMethods.contains(getNumberOneTestMethod2));
        
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType, testcaseType});
    }
}
