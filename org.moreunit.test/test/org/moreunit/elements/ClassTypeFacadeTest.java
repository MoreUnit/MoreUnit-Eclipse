package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:22:53
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.moreunit.elements.CorrespondingMemberRequest.newCorrespondingMemberRequest;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMember;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.CorrespondingMemberRequest.MemberType;
import org.moreunit.preferences.Preferences.MethodSearchMode;
import org.moreunit.test.context.Context;
import org.moreunit.test.support.DialogHelper;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;
import org.moreunit.test.context.configs.SimpleJUnit3Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;
import org.moreunit.util.MemberJumpHistory;
import org.moreunit.wizards.NewClassyWizard;
import org.moreunit.wizards.NewTestCaseWizard;

@Context(SimpleJUnit3Project.class)
public class ClassTypeFacadeTest extends ContextTestCase
{

    @Test
    public void getOneCorrespondingTestCase_should_return_test_for_cut() throws Exception
    {
        final ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        final IType oneCorrespondingTestCase = classTypeFacade.getOneCorrespondingTestCase(false).get();

        testCaseHandler().assertThat().isEqualTo(oneCorrespondingTestCase);
    }

    @Test
    @Project(mainCls = "com: enum SomeEnum", properties = @Properties(testType = TestType.JUNIT3, testClassNameTemplate = "${srcFile}Test"))
    public void getOneCorrespondingTestCase_should_return_test_for_enum() throws Exception
    {
        final ClassTypeFacade classTypeFacade = new ClassTypeFacade(context.getCompilationUnit("com.SomeEnum"));
        assertTrue(classTypeFacade.getCorrespondingTestCases().isEmpty());
    }

    @Test
    public void getCorrespondingTestMethod_should_return_testmethod_for_method() throws Exception
    {
        final IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        final IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()").get();

        final ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        final IMethod correspondingTestMethod = classTypeFacade.getCorrespondingTestMethod(getNumberOneMethod, testCaseHandler().get());
        assertEquals(getNumberOneTestMethod, correspondingTestMethod);
    }

    @Test
        public void getCorrespondingTestMethodsByName_withSearchMode_should_return_methods_with_testnaming_convention() throws Exception
        {
            final IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
            testCaseHandler().addMethod("public void testGetNumberOne()");

            final ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
            assertFalse(classTypeFacade.getCorrespondingTestMethods(getNumberOneMethod, MethodSearchMode.BY_NAME).isEmpty());
        }

    @Test
        public void getCorrespondingTestMethodsByName_withSearchMode_no_testmethod() throws Exception
        {
            final IMethod methodWithoutCorrespondingTestMethod = cutHandler().addMethod("public int getNumberTwo()", "return 2;").get();

            final ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
            assertTrue(classTypeFacade.getCorrespondingTestMethods(methodWithoutCorrespondingTestMethod, MethodSearchMode.BY_NAME).isEmpty());
        }

    @Test
        public void getCorrespondingTestMethodsByName_withSearchMode_testmethod_calls_method() throws Exception
        {
            final IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
            testCaseHandler().addMethod("public void testWhichNameDoesNotMatchTestedMethodName()", "new SomeClass().getNumberOne();");

            final ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
            assertFalse(classTypeFacade.getCorrespondingTestMethods(getNumberOneMethod, MethodSearchMode.BY_CALL).isEmpty());
        }

    @Test
        public void getCorrespondingTestMethodsByName_withSearchMode_no_test_calls_method() throws Exception
        {
            final IMethod methodWithoutCorrespondingTestMethod = cutHandler().addMethod("public int getNumberTwo()", "return 2;").get();

            final ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
            assertTrue(classTypeFacade.getCorrespondingTestMethods(methodWithoutCorrespondingTestMethod, MethodSearchMode.BY_CALL).isEmpty());
        }

    @Test
    public void getCorrespondingTestMethod_should_return_null_when_testmethod_is_missing() throws Exception
    {
        final IMethod methodWithoutCorrespondingTestMethod = cutHandler().addMethod("public int getNumberTwo()", "return 2;").get();

        final ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        assertNull(classTypeFacade.getCorrespondingTestMethod(methodWithoutCorrespondingTestMethod, testCaseHandler().get()));
    }

    @Test
        public void getCorrespondingTestMethodsByName_should_return_all_testmethods_for_method() throws Exception
        {
            final IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
            final IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public int testGetNumberOne()").get();
            final IMethod getNumberOneTestMethod2 = testCaseHandler().addMethod("public int testGetNumberOne2()").get();

            final ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
            final List<IMethod> correspondingTestMethods = classTypeFacade.getCorrespondingTestMethodsByName(getNumberOneMethod);
            assertEquals(Arrays.asList(getNumberOneTestMethod, getNumberOneTestMethod2), correspondingTestMethods);
        }

    @Test
    public void getOneCorrespondingMember_should_return_testcase_when_no_testmethod_given() throws Exception
    {
        final TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());

        final CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .build();

        final IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        testCaseHandler().assertThat().isEqualTo(oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_testmethod_by_name_when_it_exists() throws Exception
    {
        final IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        final IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()").get();

        final TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());

        final CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .methodSearchMode(MethodSearchMode.BY_NAME) //
                .build();

        final IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        assertEquals(getNumberOneTestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_testmethod_when_caller_exists() throws Exception
    {
        final IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        final IMethod giveMe1TestMethod = testCaseHandler().addMethod("public void testGiveMe1()", "new SomeClass().getNumberOne();").get();

        final TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());

        final CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .methodSearchMode(MethodSearchMode.BY_CALL) //
                .build();

        final IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        assertEquals(giveMe1TestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_testmethod_by_call_when_testmethod_is_named_according_to_pattern_and_caller_exist() throws Exception
    {
        final IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        final IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()", "new SomeClass().getNumberOne();").get();

        final TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());

        final CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .methodSearchMode(MethodSearchMode.BY_CALL) //
                .build();

        final IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        assertEquals(getNumberOneTestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_overridden_method_when_subtype_exists() throws Exception
    {
        cutHandler().addMethod("public void doIt()");

        testCaseHandler().addMethod("public void testDoIt()", "new SomeClass().doIt();");

        final TypeHandler subTypeHandler = cutHandler().createSubclass("org.SomeSubClass");
        final IMethod overridingMethod = subTypeHandler.addMethod("public void doIt()", null).get();

        final TypeHandler subTypeTestHandler = context.getProjectHandler().getTestSrcFolderHandler().createClass("org.SomeSubClassTest");
        final MethodHandler overridingMethodTestHandler = subTypeTestHandler.addMethod("public void testDoIt()", "new SomeSubClass().doIt();");

        final TypeFacade classTypeFacade = new ClassTypeFacade(subTypeHandler.getCompilationUnit());

        final CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(overridingMethod) //
                .methodSearchMode(MethodSearchMode.BY_CALL) //
                .build();

        final IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        overridingMethodTestHandler.assertThat().isEqualTo(oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_method_under_test_by_call_when_called_with_both_search_modes() throws CoreException
    {
        final IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        final IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGiveMe1()", "new SomeClass().getNumberOne();").get();

        final TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());

        final CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .methodSearchMode(MethodSearchMode.BY_CALL_AND_BY_NAME) //
                .build();

        final IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        assertEquals(oneCorrespondingTestMember, getNumberOneTestMethod);
    }

    @Test
    public void getOneCorrespondingMember_should_return_method_under_test_with_naming_pattern_when_called_with_both_search_modes() throws CoreException
    {
        final IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        final IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()").get();

        final TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());

        final CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .methodSearchMode(MethodSearchMode.BY_CALL_AND_BY_NAME) //
                .build();

        final IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        assertEquals(oneCorrespondingTestMember, getNumberOneTestMethod);
    }

    @Test
    public void getOneCorrespondingTestCase_should_return_not_found_result_when_no_test_case_exists() throws Exception
    {
        final TypeHandler typeWithoutTest = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.ClassWithoutTest");

        final ClassTypeFacade classTypeFacade = new ClassTypeFacade(typeWithoutTest.getCompilationUnit());
        final ClassTypeFacade.CorrespondingTestCase result = classTypeFacade.getOneCorrespondingTestCase(false);

        assertFalse(result.found());
        assertNull(result.get());
        assertFalse(result.hasJustBeenCreated());
    }

    private static class TestableClassTypeFacade extends ClassTypeFacade
    {
        TestableClassTypeFacade(org.eclipse.jdt.core.ICompilationUnit compilationUnit)
        {
            super(compilationUnit);
        }

        @Override
        protected NewClassyWizard newCorrespondingClassWizard(IType fromType)
        {
            return super.newCorrespondingClassWizard(fromType);
        }
    }

    @Test
    public void newCorrespondingClassWizard_should_return_new_test_case_wizard() throws Exception
    {
        final TestableClassTypeFacade classTypeFacade = new TestableClassTypeFacade(cutHandler().getCompilationUnit());

        final NewClassyWizard wizard = classTypeFacade.newCorrespondingClassWizard(cutHandler().get());

        assertNotNull(wizard);
        assertTrue(wizard instanceof NewTestCaseWizard);
    }

    private TypeHandler cutHandler()
    {
        return context.getPrimaryTypeHandler("org.SomeClass");
    }

    private TypeHandler testCaseHandler()
    {
        return context.getPrimaryTypeHandler("org.SomeClassTest");
    }

    @Test
    @Project(mainCls = "com:Foo", testCls = "com:FooTest; com:FooTestNG", properties = @Properties(testType = TestType.JUNIT4, testClassNameTemplate = "${srcFile}(Test|TestNG)"))
    public void getOneCorrespondingTestCase_should_open_dialog_when_several_test_cases_exist() throws Exception
    {
        final Display display = Display.getDefault();
        final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
        display.asyncExec(DialogHelper.closerUntilHandled(display, knownShells, shell -> DialogHelper.confirmItem(shell, "FooTest"), 2000));

        final ClassTypeFacade classTypeFacade = new ClassTypeFacade(context.getCompilationUnit("com.Foo"));
        final ClassTypeFacade.CorrespondingTestCase result = classTypeFacade.getOneCorrespondingTestCase(false, "Please choose a test case...");

        assertTrue(result.found());
        assertFalse(result.hasJustBeenCreated());
        assertEquals("FooTest", result.get().getElementName());
    }

    @Test
    public void getOneCorrespondingMember_should_open_dialog_when_several_test_methods_match() throws Exception
    {
        final IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        testCaseHandler().addMethod("public void testGetNumberOne()", "new SomeClass().getNumberOne();").get();
        final IMethod callerTestMethod = testCaseHandler().addMethod("public void testGiveMe1()", "new SomeClass().getNumberOne();").get();

        // the jump history provides the default selection of the dialog
        MemberJumpHistory.getInstance().registerJump(getNumberOneMethod, callerTestMethod);

        final Display display = Display.getDefault();
        final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
        display.asyncExec(DialogHelper.closerUntilHandled(display, knownShells, shell -> DialogHelper.confirmItem(shell, "testGiveMe1"), 2000));

        final TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        final CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .methodSearchMode(MethodSearchMode.BY_CALL_AND_BY_NAME) //
                .build();

        final IMember member = classTypeFacade.getOneCorrespondingMember(request);

        assertEquals("testGiveMe1", member.getElementName());
        assertEquals(callerTestMethod.getDeclaringType(), member.getDeclaringType());
    }
}
