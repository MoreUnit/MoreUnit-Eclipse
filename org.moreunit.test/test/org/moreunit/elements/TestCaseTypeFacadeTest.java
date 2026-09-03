package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:13:50
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.moreunit.elements.CorrespondingMemberRequest.newCorrespondingMemberRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.CorrespondingMemberRequest.MemberType;
import org.moreunit.preferences.Preferences.MethodSearchMode;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;

@Preferences(testClassNameTemplate="${srcFile}Test", testMethodPrefix=true)
@Project(mainCls = "org:Hello", testCls = "org:HelloTest", mainSrcFolder="src", testSrcFolder="test")
public class TestCaseTypeFacadeTest extends ContextTestCase
{

    private TypeHandler cutTypeHandler;
    private TypeHandler testcaseTypeHandler;

    @BeforeEach
    public void setUp() throws JavaModelException
    {
        cutTypeHandler = context.getPrimaryTypeHandler("org.Hello");
        testcaseTypeHandler = context.getPrimaryTypeHandler("org.HelloTest");
    }

    @Test
    public void getCorrespondingTestedMethods_should_return_one_exisiting_match() throws CoreException
    {
        final MethodHandler testedMethod = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        final MethodHandler testMethod = testcaseTypeHandler.addMethod("public void testGetNumberOne()");

        final TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        final List<IMethod> expectedTestedMethods = new ArrayList<>();
        expectedTestedMethods.add(testedMethod.get());
        assertEquals(testCaseTypeFacade.getCorrespondingTestedMethods(testMethod.get(), cutTypeHandler.get()), expectedTestedMethods);
    }

    @Test
    public void getCorrespondingTestedMethods_should_return_empty_list_when_no_method_exists()
    {
        final MethodHandler testMethodWithNoCorrespondingTestedMethod = testcaseTypeHandler.addMethod("public void testAnything()");
        final TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());
        assertTrue(testCaseTypeFacade.getCorrespondingTestedMethods(testMethodWithNoCorrespondingTestedMethod.get(), cutTypeHandler.get()).isEmpty());
    }

    @Test
    public void getCorrespondingTestedMethods_should_return_all_possible_methods_under_test()
    {
        final MethodHandler possiblyTestedMethod = cutTypeHandler.addMethod("public int getNumber()", "return 9;");
        final MethodHandler possiblyTestedMethod2 = cutTypeHandler.addMethod("public int getNumberTwo()", "return 2;");
        final MethodHandler testMethod = testcaseTypeHandler.addMethod("public void testGetNumberTwoAndNine()");

        final TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        final List<IMethod> expectedTestedMethods = new ArrayList<>();
        expectedTestedMethods.add(possiblyTestedMethod.get());
        expectedTestedMethods.add(possiblyTestedMethod2.get());
        assertEquals(testCaseTypeFacade.getCorrespondingTestedMethods(testMethod.get(), cutTypeHandler.get()), expectedTestedMethods);
    }

    @Test
    public void getCorrespondingTestedMethods_should_return_perfect_match_when_more_than_one_methods_exist() throws CoreException
    {
        // not perfect match
        cutTypeHandler.addMethod("public int getNumber()", "return 1;");
        final MethodHandler perfectMatch = cutTypeHandler.addMethod("public int getNumberTwo()", "return 2;");
        final MethodHandler testMethod = testcaseTypeHandler.addMethod("public void testGetNumberTwo()");
        final MethodHandler testMethodWithNoCorrespondingTestedMethod = testcaseTypeHandler.addMethod("public void testAnything()");

        final TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        final List<IMethod> expectedTestedMethods = new ArrayList<>();
        expectedTestedMethods.add(perfectMatch.get());
        assertEquals(testCaseTypeFacade.getCorrespondingTestedMethods(testMethod.get(), cutTypeHandler.get()), expectedTestedMethods);

        assertTrue(testCaseTypeFacade.getCorrespondingTestedMethods(testMethodWithNoCorrespondingTestedMethod.get(), cutTypeHandler.get()).isEmpty());
    }

    @Test
    public void getCorrespondingTestedMethods_should_return_matches_from_more_than_one_cut() throws CoreException
    {
        final MethodHandler testedMethod1 = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        final TypeHandler cutType2 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.Hello2");
        final MethodHandler testedMethod2 = cutType2.addMethod("public int getNumberOne()", "return 1;");
        final MethodHandler testMethod = cutTypeHandler.addMethod("public void testGetNumberOne()");

        final TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        final Set<IType> classesUnderTest = new LinkedHashSet<>(Arrays.asList(cutTypeHandler.get(), cutType2.get()));
        final List<IMethod> correspondingTestMethods = testCaseTypeFacade.getCorrespondingTestedMethods(testMethod.get(), classesUnderTest);
        assertEquals(new java.util.HashSet<>(Arrays.asList(testedMethod1.get(), testedMethod2.get())), new java.util.HashSet<>((correspondingTestMethods)));
        cutType2.getCompilationUnit().delete(true, null);
    }

    @Test
    public void getOneCorrespondingMember_should_return_cut_when_no_testmethod_given() throws CoreException
    {
        final TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        final CorrespondingMemberRequest request = newCorrespondingMemberRequest().withExpectedResultType(MemberType.TYPE_OR_METHOD).build();

        final IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(request);

        assertEquals(oneCorrespondingMemberUnderTest, cutTypeHandler.get());
    }

    @Test
    public void getOneCorrespondingMember_should_return_method_under_test_when_called_with_testmethod() throws CoreException
    {
        final MethodHandler getNumberOneMethod = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        final MethodHandler getNumberOneTestMethod = testcaseTypeHandler.addMethod("public void testGetNumberOne()");

        final TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        final CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneTestMethod.get()) //
                .methodSearchMode(MethodSearchMode.BY_NAME) //
                .build();

        final IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(request);

        assertEquals(oneCorrespondingMemberUnderTest, getNumberOneMethod.get());
    }

    @Test
    public void getOneCorrespondingMember_should_not_return_method_under_test_with_naming_pattern_when_called_with_extended_search() throws CoreException
    {
        cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        final MethodHandler getNumberOneTestMethod = testcaseTypeHandler.addMethod("public void testGetNumberOne()");

        final TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        final CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneTestMethod.get()) //
                .methodSearchMode(MethodSearchMode.BY_CALL) //
                .build();

        final IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(request);

        assertEquals(oneCorrespondingMemberUnderTest, cutTypeHandler.get());
    }

    @Test
    public void getOneCorrespondingMember_should_return_method_under_test_with_naming_pattern_when_called_with_both_search_modes() throws CoreException
    {
        final MethodHandler getNumberOneMethod = cutTypeHandler.addMethod("public int getNumberOne()", "return 1;");
        final MethodHandler getNumberOneTestMethod = testcaseTypeHandler.addMethod("public void testGetNumberOne()");

        final TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseTypeHandler.getCompilationUnit());

        final CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneTestMethod.get()) //
                .methodSearchMode(MethodSearchMode.BY_CALL_AND_BY_NAME) //
                .build();

        final IMember oneCorrespondingMemberUnderTest = testCaseTypeFacade.getOneCorrespondingMember(request);

        assertEquals(oneCorrespondingMemberUnderTest, getNumberOneMethod.get());
    }

    @Preferences(testClassNameTemplate="${srcFile}*Test", testSrcFolder="test")
    @Test
    public void getCorrespondingClasses_should_return_more_than_one_test_when_flexible_testcase_naming_is_used() throws Exception
    {
        final TypeHandler class1 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.One");
        final TypeHandler class2 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.OneTwo");
        final TypeHandler testClass = context.getProjectHandler().getTestSrcFolderHandler().createClass("org.OneTwoTest");

        final TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testClass.getCompilationUnit());

        final Collection<IType> classes = testCaseTypeFacade.getCorrespondingClasses(false);
        assertEquals(2, classes.size());

        class1.getCompilationUnit().delete(true, null);
        class2.getCompilationUnit().delete(true, null);
        testClass.getCompilationUnit().delete(true, null);
    }

    @Preferences(testClassNameTemplate="${srcFile}Test", testSrcFolder="test")
    @Test
    public void getCorrespondingClasses_should_return_only_one_test_when_flexible_testcase_naming_is_not_used() throws Exception
    {
        final TypeHandler class1 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.One");
        final TypeHandler class2 = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.OneTwo");
        final TypeHandler testClass = context.getProjectHandler().getTestSrcFolderHandler().createClass("org.OneTwoTest");

        final TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testClass.getCompilationUnit());

        final Collection<IType> classes = testCaseTypeFacade.getCorrespondingClasses(false);
        assertEquals(1, classes.size());
        assertEquals(classes.iterator().next().getElementName(), "OneTwo");

        class1.getCompilationUnit().delete(true, null);
        class2.getCompilationUnit().delete(true, null);
        testClass.getCompilationUnit().delete(true, null);
    }
}
