package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:13:50
 */

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

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
    public void getCorrespondingTestedMethods_should_return_one_exisiting_match() throws CoreException
    {
        MethodHandler testedMethod = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        MethodHandler testMethod = testcaseTypeHandler.addMethod("public void testGetNumberOne()");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        List<IMethod> expectedTestedMethods = new ArrayList<IMethod>();
        expectedTestedMethods.add(testedMethod.get());
        assertThat(testCaseTypeFacade.getCorrespondingTestedMethods(testMethod.get(), cutTypeHandler.get())).isEqualTo(expectedTestedMethods);
    }
    
    @Test
    public void getCorrespondingTestedMethods_should_return_empty_list_when_no_method_exists()
    {
        MethodHandler testMethodWithNoCorrespondingTestedMethod = testcaseTypeHandler.addMethod("public void testAnything()");
        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());
        assertThat(testCaseTypeFacade.getCorrespondingTestedMethods(testMethodWithNoCorrespondingTestedMethod.get(), cutTypeHandler.get())).isEmpty();
    }

    @Test
    public void getCorrespondingTestedMethods_should_return_all_possible_methods_under_test()
    {
        MethodHandler possiblyTestedMethod = cutTypeHandler.addMethod("public int getNumber()", "return 9;");
        MethodHandler possiblyTestedMethod2 = cutTypeHandler.addMethod("public int getNumberTwo()", "return 2;");
        MethodHandler testMethod = testcaseTypeHandler.addMethod("public void testGetNumberTwoAndNine()");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        List<IMethod> expectedTestedMethods = new ArrayList<IMethod>();
        expectedTestedMethods.add(possiblyTestedMethod.get());
        expectedTestedMethods.add(possiblyTestedMethod2.get());
        assertThat(testCaseTypeFacade.getCorrespondingTestedMethods(testMethod.get(), cutTypeHandler.get())).isEqualTo(expectedTestedMethods);
    }

    @Test
    public void getCorrespondingTestedMethods_should_return_perfect_match_when_more_than_one_methods_exist() throws CoreException
    {
        // not perfect match
        cutTypeHandler.addMethod("public int getNumber()", "return 1;");
        MethodHandler perfectMatch = cutTypeHandler.addMethod("public int getNumberTwo()", "return 2;");
        MethodHandler testMethod = testcaseTypeHandler.addMethod("public void testGetNumberTwo()");
        MethodHandler testMethodWithNoCorrespondingTestedMethod = testcaseTypeHandler.addMethod("public void testAnything()");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        List<IMethod> expectedTestedMethods = new ArrayList<IMethod>();
        expectedTestedMethods.add(perfectMatch.get());
        assertThat(testCaseTypeFacade.getCorrespondingTestedMethods(testMethod.get(), cutTypeHandler.get())).isEqualTo(expectedTestedMethods);

        assertThat(testCaseTypeFacade.getCorrespondingTestedMethods(testMethodWithNoCorrespondingTestedMethod.get(), cutTypeHandler.get())).isEmpty();
    }
    
    @Test
    public void getCorrespondingTestedMethods_should_return_matches_from_more_than_one_cut() throws CoreException
    {
        MethodHandler testedMethod1 = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        TypeHandler cutType2 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.Hello2");
        MethodHandler testedMethod2 = cutType2.addMethod("public int getNumberOne()", "return 1;");
        MethodHandler testMethod = cutTypeHandler.addMethod("public void testGetNumberOne()");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        Set<IType> classesUnderTest = new LinkedHashSet<IType>(Arrays.asList(cutTypeHandler.get(), cutType2.get()));
        List<IMethod> correspondingTestMethods = testCaseTypeFacade.getCorrespondingTestedMethods(testMethod.get(), classesUnderTest);
        assertThat(correspondingTestMethods).hasSize(2);
        assertThat(correspondingTestMethods).contains(testedMethod1.get());
        assertThat(correspondingTestMethods).contains(testedMethod2.get());
        cutType2.getCompilationUnit().delete(true, null);
    }

    @Test
    public void getOneCorrespondingMember_should_return_cut_when_no_testmethod_given() throws CoreException
    {
        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(null, false, false, null);

        assertThat(oneCorrespondingMemberUnderTest).isEqualTo(cutTypeHandler.get());
    }

    @Test
    public void getOneCorrespondingMember_should_return_method_under_test_when_called_with_testmethod() throws CoreException
    {
        MethodHandler getNumberOneMethod = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        MethodHandler getNumberOneTestMethod = testcaseTypeHandler.addMethod("public void testGetNumberOne()");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(getNumberOneTestMethod.get(), false, false, null);

        assertThat(oneCorrespondingMemberUnderTest).isEqualTo(getNumberOneMethod.get());
    }

    @Test
    public void getOneCorrespondingMember_should_return_method_under_test_with_naming_pattern_when_calles_with_extended_search() throws CoreException
    {
        MethodHandler getNumberOneMethod = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        MethodHandler getNumberOneTestMethod = testcaseTypeHandler.addMethod("public void testGetNumberOne()");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(getNumberOneTestMethod.get(), false, true, null);

        assertThat(oneCorrespondingMemberUnderTest).isEqualTo(getNumberOneMethod.get());
    }

    @Test
    public void getOneCorrespondingMember_should_return_method_by_callee_when_called_with_extended_search() throws CoreException
    {
        MethodHandler getNumberOneMethod = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        MethodHandler giveMe1TestMethod = testcaseTypeHandler.addMethod("public void testGiveMe1()", "new Hello().getNumberOne();");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(giveMe1TestMethod.get(), false, true, null);

        assertEquals(getNumberOneMethod.get(), oneCorrespondingMemberUnderTest);
    }

    @Test
    public void getOneCorrespondingMember_should_return_method_when_test_follows_naming_pattern_and_calls_method() throws CoreException
    {
        MethodHandler getNumberOneMethod = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        MethodHandler getNumberOneTestMethod = testcaseTypeHandler.addMethod("public void testGetNumberOne()", "new Hello().getNumberOne();");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());
        IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(getNumberOneTestMethod.get(), false, true, null);

        assertEquals(getNumberOneMethod.get(), oneCorrespondingMemberUnderTest);
    }

    @Preferences(testClassSuffixes="Test", flexibleNaming=true, testSrcFolder="test")
    @Test
    public void getCorrespondingClassesUnderTest_should_return_more_than_one_test_when_flexible_testcase_naming_is_set() throws Exception
    {
        TypeHandler class1 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.One");
        TypeHandler class2 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.OneTwo");
        TypeHandler testClass = context.getProjectHandler().getTestSrcFolderHandler().createClass("org.OneTwoTest");
        
        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testClass.getCompilationUnit());
        List<IType> list = testCaseTypeFacade.getCorrespondingClassesUnderTest();
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getElementName()).isEqualTo("OneTwo");
        assertThat(list.get(1).getElementName()).isEqualTo("One");
        
        class1.getCompilationUnit().delete(true, null);
        class2.getCompilationUnit().delete(true, null);
        testClass.getCompilationUnit().delete(true, null);
    }
    
    @Preferences(testClassSuffixes="Test", flexibleNaming=false, testSrcFolder="test")
    @Test
    public void getCorrespondingClassesUnderTest_should_return_only_one_test_when_flexible_testcase_naming_is_not_set() throws Exception
    {
        TypeHandler class1 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.One");
        TypeHandler class2 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.OneTwo");
        TypeHandler testClass = context.getProjectHandler().getTestSrcFolderHandler().createClass("org.OneTwoTest");
        
        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testClass.getCompilationUnit());
        List<IType> list = testCaseTypeFacade.getCorrespondingClassesUnderTest();
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getElementName()).isEqualTo("OneTwo");
        
        class1.getCompilationUnit().delete(true, null);
        class2.getCompilationUnit().delete(true, null);
        testClass.getCompilationUnit().delete(true, null);
    }
}
