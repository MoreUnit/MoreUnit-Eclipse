package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:13:50
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;

@Preferences(testClassSuffixes="Test", testMethodPrefix=true)
@Project(mainCls = "org:Hello", testCls = "org:HelloTest", mainSrcFolder="src", testSrcFolder="test")
public class TestCaseTypeFacadeTest extends ContextTestCase
{

    private TypeHandler cutTypeHandler;
    private TypeHandler testcaseTypeHandler;
    
    @Before
    public void setUp() throws JavaModelException
    {
        cutTypeHandler = context.getPrimaryTypeHandler("org.Hello");
        testcaseTypeHandler = context.getPrimaryTypeHandler("org.HelloTest");
    }

    @Test
    public void testGetCorrespondingTestedMethodsForClassUnderTestWithOneMatch() throws CoreException
    {
        MethodHandler testedMethod = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        MethodHandler testMethod = testcaseTypeHandler.addMethod("public void testGetNumberOne()");
        MethodHandler testMethodWithNoCorrespondingTestedMethod = testcaseTypeHandler.addMethod("public void testAnything()");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        List<IMethod> expectedTestedMethods = new ArrayList<IMethod>();
        expectedTestedMethods.add(testedMethod.get());
        assertEquals(expectedTestedMethods, testCaseTypeFacade.getCorrespondingTestedMethods(testMethod.get(), cutTypeHandler.get()));
        assertTrue(testCaseTypeFacade.getCorrespondingTestedMethods(testMethodWithNoCorrespondingTestedMethod.get(), cutTypeHandler.get()).isEmpty());
    }

    @Test
    public void testGetCorrespondingTestedMethodsForClassUnderTestWithSeveralMatches() throws CoreException
    {
        MethodHandler possiblyTestedMethod = cutTypeHandler.addMethod("public int getNumber()", "return 9;");
        MethodHandler possiblyTestedMethod2 = cutTypeHandler.addMethod("public int getNumberTwo()", "return 2;");
        MethodHandler testMethod = testcaseTypeHandler.addMethod("public void testGetNumberTwoAndNine()");
        MethodHandler testMethodWithNoCorrespondingTestedMethod = testcaseTypeHandler.addMethod("public void testAnything()");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        List<IMethod> expectedTestedMethods = new ArrayList<IMethod>();
        expectedTestedMethods.add(possiblyTestedMethod.get());
        expectedTestedMethods.add(possiblyTestedMethod2.get());
        assertEquals(expectedTestedMethods, testCaseTypeFacade.getCorrespondingTestedMethods(testMethod.get(), cutTypeHandler.get()));

        assertTrue(testCaseTypeFacade.getCorrespondingTestedMethods(testMethodWithNoCorrespondingTestedMethod.get(), cutTypeHandler.get()).isEmpty());
    }

    @Test
    public void testGetCorrespondingTestedMethodsForClassUnderTestWhenOneMethodNameIsAPerfectMatch() throws CoreException
    {
        // not perfect match
        cutTypeHandler.addMethod("public int getNumber()", "return 1;");
        MethodHandler perfectMatch = cutTypeHandler.addMethod("public int getNumberTwo()", "return 2;");
        MethodHandler testMethod = testcaseTypeHandler.addMethod("public void testGetNumberTwo()");
        MethodHandler testMethodWithNoCorrespondingTestedMethod = testcaseTypeHandler.addMethod("public void testAnything()");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        List<IMethod> expectedTestedMethods = new ArrayList<IMethod>();
        expectedTestedMethods.add(perfectMatch.get());
        assertEquals(expectedTestedMethods, testCaseTypeFacade.getCorrespondingTestedMethods(testMethod.get(), cutTypeHandler.get()));

        assertTrue(testCaseTypeFacade.getCorrespondingTestedMethods(testMethodWithNoCorrespondingTestedMethod.get(), cutTypeHandler.get()).isEmpty());
    }
    
    @Test
    public void testGetCorrespondingTestedMethods() throws CoreException
    {
        MethodHandler testedMethod1 = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        TypeHandler cutType2 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.Hello2");
        MethodHandler testedMethod2 = cutType2.addMethod("public int getNumberOne()", "return 1;");
        MethodHandler testMethod = cutTypeHandler.addMethod("public void testGetNumberOne()");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        Set<IType> classesUnderTest = new LinkedHashSet<IType>(Arrays.asList(cutTypeHandler.get(), cutType2.get()));
        List<IMethod> correspondingTestMethods = testCaseTypeFacade.getCorrespondingTestedMethods(testMethod.get(), classesUnderTest);
        assertEquals(2, correspondingTestMethods.size());
        assertTrue(correspondingTestMethods.contains(testedMethod1.get()));
        assertTrue(correspondingTestMethods.contains(testedMethod2.get()));
        cutType2.getCompilationUnit().delete(true, null);
    }

    @Test
    public void testgetOneCorrespondingMemberWithoutTestMethod() throws CoreException
    {
        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(null, false, false, null);

        assertEquals(cutTypeHandler.get(), oneCorrespondingMemberUnderTest);
    }

    @Test
    public void testgetOneCorrespondingMemberWithTestMethod() throws CoreException
    {
        MethodHandler getNumberOneMethod = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        MethodHandler getNumberOneTestMethod = testcaseTypeHandler.addMethod("public void testGetNumberOne()");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(getNumberOneTestMethod.get(), false, false, null);

        assertEquals(getNumberOneMethod.get(), oneCorrespondingMemberUnderTest);
    }

    @Test
    public void testgetOneCorrespondingMemberWithExtendedSearchAndTestMethodFollowingNamingPattern() throws CoreException
    {
        MethodHandler getNumberOneMethod = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        MethodHandler getNumberOneTestMethod = testcaseTypeHandler.addMethod("public void testGetNumberOne()");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(getNumberOneTestMethod.get(), false, true, null);

        assertEquals(getNumberOneMethod.get(), oneCorrespondingMemberUnderTest);
    }

    @Test
    public void testgetOneCorrespondingMemberWithExtendedSearchAndTestMethodCallingMethodUnderTest() throws CoreException
    {
        MethodHandler getNumberOneMethod = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        MethodHandler giveMe1TestMethod = testcaseTypeHandler.addMethod("public void testGiveMe1()", "new Hello().getNumberOne();");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(giveMe1TestMethod.get(), false, true, null);

        assertEquals(getNumberOneMethod.get(), oneCorrespondingMemberUnderTest);
    }

    @Test
    public void testgetOneCorrespondingMemberWithExtendedSearchAndTestMethodFollowingNamingPatternAndCallingMethodUnderTest() throws CoreException
    {
        MethodHandler getNumberOneMethod = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        MethodHandler getNumberOneTestMethod = testcaseTypeHandler.addMethod("public void testGetNumberOne()", "new Hello().getNumberOne();");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(getNumberOneTestMethod.get(), false, true, null);

        assertEquals(getNumberOneMethod.get(), oneCorrespondingMemberUnderTest);
    }

    @Preferences(testClassSuffixes="Test", flexibleNaming=true, testSourcefolder="test")
    @Test
    public void testGetCorrespondingClassesUnderTestFlexibleNaming() throws Exception
    {
        TypeHandler class1 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.One");
        TypeHandler class2 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.OneTwo");
        TypeHandler testClass = context.getProjectHandler().getTestSrcFolderHandler().createClass("org.OneTwoTest");
        
        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testClass.getCompilationUnit());
        List<IType> list = testCaseTypeFacade.getCorrespondingClassesUnderTest();
        assertEquals(2, list.size());
        assertEquals("OneTwo", list.get(0).getElementName());
        assertEquals("One", list.get(1).getElementName());
        
        class1.getCompilationUnit().delete(true, null);
        class2.getCompilationUnit().delete(true, null);
        testClass.getCompilationUnit().delete(true, null);
    }
    
    @Preferences(testClassSuffixes="Test", flexibleNaming=false, testSourcefolder="test")
    @Test
    public void testGetCorrespondingClassesUnderTestNotFlexibleNaming() throws Exception
    {
        TypeHandler class1 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.One");
        TypeHandler class2 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.OneTwo");
        TypeHandler testClass = context.getProjectHandler().getTestSrcFolderHandler().createClass("org.OneTwoTest");
        
        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testClass.getCompilationUnit());
        List<IType> list = testCaseTypeFacade.getCorrespondingClassesUnderTest();
        assertEquals(1, list.size());
        assertEquals("OneTwo", list.get(0).getElementName());
        
        class1.getCompilationUnit().delete(true, null);
        class2.getCompilationUnit().delete(true, null);
        testClass.getCompilationUnit().delete(true, null);
    }
}
