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
        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        IType oneCorrespondingTestCase = classTypeFacade.getOneCorrespondingTestCase(false).get();

        testCaseHandler().assertThat().isEqualTo(oneCorrespondingTestCase);
    }

    @Test
    @Project(mainCls = "com: enum SomeEnum", properties = @Properties(testType = TestType.JUNIT3, testClassNameTemplate = "${srcFile}Test"))
    public void getOneCorrespondingTestCase_should_return_test_for_enum() throws Exception
    {
        ClassTypeFacade classTypeFacade = new ClassTypeFacade(context.getCompilationUnit("com.SomeEnum"));
        assertTrue(classTypeFacade.getCorrespondingTestCases().isEmpty());
    }

    @Test
    public void getCorrespondingTestMethod_should_return_testmethod_for_method() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        IMethod correspondingTestMethod = classTypeFacade.getCorrespondingTestMethod(getNumberOneMethod, testCaseHandler().get());
        assertEquals(getNumberOneTestMethod, correspondingTestMethod);
    }

    @Test
        public void getCorrespondingTestMethodsByName_withSearchMode_should_return_methods_with_testnaming_convention() throws Exception
        {
            IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
            testCaseHandler().addMethod("public void testGetNumberOne()");

            ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
            assertFalse(classTypeFacade.getCorrespondingTestMethods(getNumberOneMethod, MethodSearchMode.BY_NAME).isEmpty());
        }

    @Test
        public void getCorrespondingTestMethodsByName_withSearchMode_no_testmethod() throws Exception
        {
            IMethod methodWithoutCorrespondingTestMethod = cutHandler().addMethod("public int getNumberTwo()", "return 2;").get();

            ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
            assertTrue(classTypeFacade.getCorrespondingTestMethods(methodWithoutCorrespondingTestMethod, MethodSearchMode.BY_NAME).isEmpty());
        }

    @Test
        public void getCorrespondingTestMethodsByName_withSearchMode_testmethod_calls_method() throws Exception
        {
            IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
            testCaseHandler().addMethod("public void testWhichNameDoesNotMatchTestedMethodName()", "new SomeClass().getNumberOne();");

            ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
            assertFalse(classTypeFacade.getCorrespondingTestMethods(getNumberOneMethod, MethodSearchMode.BY_CALL).isEmpty());
        }

    @Test
        public void getCorrespondingTestMethodsByName_withSearchMode_no_test_calls_method() throws Exception
        {
            IMethod methodWithoutCorrespondingTestMethod = cutHandler().addMethod("public int getNumberTwo()", "return 2;").get();

            ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
            assertTrue(classTypeFacade.getCorrespondingTestMethods(methodWithoutCorrespondingTestMethod, MethodSearchMode.BY_CALL).isEmpty());
        }

    @Test
    public void getCorrespondingTestMethod_should_return_null_when_testmethod_is_missing() throws Exception
    {
        IMethod methodWithoutCorrespondingTestMethod = cutHandler().addMethod("public int getNumberTwo()", "return 2;").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        assertNull(classTypeFacade.getCorrespondingTestMethod(methodWithoutCorrespondingTestMethod, testCaseHandler().get()));
    }

    @Test
        public void getCorrespondingTestMethodsByName_should_return_all_testmethods_for_method() throws Exception
        {
            IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
            IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public int testGetNumberOne()").get();
            IMethod getNumberOneTestMethod2 = testCaseHandler().addMethod("public int testGetNumberOne2()").get();

            ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
            List<IMethod> correspondingTestMethods = classTypeFacade.getCorrespondingTestMethodsByName(getNumberOneMethod);
            assertEquals(Arrays.asList(getNumberOneTestMethod, getNumberOneTestMethod2), correspondingTestMethods);
        }

    @Test
    public void getOneCorrespondingMember_should_return_testcase_when_no_testmethod_given() throws Exception
    {
        TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());

        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .build();

        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        testCaseHandler().assertThat().isEqualTo(oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_testmethod_by_name_when_it_exists() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()").get();

        TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());

        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .methodSearchMode(MethodSearchMode.BY_NAME) //
                .build();

        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        assertEquals(getNumberOneTestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_testmethod_when_caller_exists() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod giveMe1TestMethod = testCaseHandler().addMethod("public void testGiveMe1()", "new SomeClass().getNumberOne();").get();

        TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());

        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .methodSearchMode(MethodSearchMode.BY_CALL) //
                .build();

        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        assertEquals(giveMe1TestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_testmethod_by_call_when_testmethod_is_named_according_to_pattern_and_caller_exist() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()", "new SomeClass().getNumberOne();").get();

        TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());

        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .methodSearchMode(MethodSearchMode.BY_CALL) //
                .build();

        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        assertEquals(getNumberOneTestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_overridden_method_when_subtype_exists() throws Exception
    {
        cutHandler().addMethod("public void doIt()");

        testCaseHandler().addMethod("public void testDoIt()", "new SomeClass().doIt();");

        TypeHandler subTypeHandler = cutHandler().createSubclass("org.SomeSubClass");
        IMethod overridingMethod = subTypeHandler.addMethod("public void doIt()", null).get();

        TypeHandler subTypeTestHandler = context.getProjectHandler().getTestSrcFolderHandler().createClass("org.SomeSubClassTest");
        MethodHandler overridingMethodTestHandler = subTypeTestHandler.addMethod("public void testDoIt()", "new SomeSubClass().doIt();");

        TypeFacade classTypeFacade = new ClassTypeFacade(subTypeHandler.getCompilationUnit());

        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(overridingMethod) //
                .methodSearchMode(MethodSearchMode.BY_CALL) //
                .build();

        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        overridingMethodTestHandler.assertThat().isEqualTo(oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_method_under_test_by_call_when_called_with_both_search_modes() throws CoreException
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGiveMe1()", "new SomeClass().getNumberOne();").get();

        TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());

        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .methodSearchMode(MethodSearchMode.BY_CALL_AND_BY_NAME) //
                .build();

        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        assertEquals(oneCorrespondingTestMember, getNumberOneTestMethod);
    }

    @Test
    public void getOneCorrespondingMember_should_return_method_under_test_with_naming_pattern_when_called_with_both_search_modes() throws CoreException
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()").get();

        TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());

        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .methodSearchMode(MethodSearchMode.BY_CALL_AND_BY_NAME) //
                .build();

        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        assertEquals(oneCorrespondingTestMember, getNumberOneTestMethod);
    }

    @Test
    public void getOneCorrespondingTestCase_should_return_not_found_result_when_no_test_case_exists() throws Exception
    {
        TypeHandler typeWithoutTest = context.getProjectHandler().getMainSrcFolderHandler().createClass("org.ClassWithoutTest");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(typeWithoutTest.getCompilationUnit());
        ClassTypeFacade.CorrespondingTestCase result = classTypeFacade.getOneCorrespondingTestCase(false);

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
        TestableClassTypeFacade classTypeFacade = new TestableClassTypeFacade(cutHandler().getCompilationUnit());

        NewClassyWizard wizard = classTypeFacade.newCorrespondingClassWizard(cutHandler().get());

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
        Display display = Display.getDefault();
        java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
        display.asyncExec(DialogHelper.closerFor(display, knownShells, shell -> DialogHelper.confirmItem(shell, "FooTest"), 2000));

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(context.getCompilationUnit("com.Foo"));
        ClassTypeFacade.CorrespondingTestCase result = classTypeFacade.getOneCorrespondingTestCase(false, "Please choose a test case...");

        assertTrue(result.found());
        assertFalse(result.hasJustBeenCreated());
        assertEquals("FooTest", result.get().getElementName());
    }

    @Test
    public void getOneCorrespondingMember_should_open_dialog_when_several_test_methods_match() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        testCaseHandler().addMethod("public void testGetNumberOne()", "new SomeClass().getNumberOne();").get();
        IMethod callerTestMethod = testCaseHandler().addMethod("public void testGiveMe1()", "new SomeClass().getNumberOne();").get();

        // the jump history provides the default selection of the dialog
        MemberJumpHistory.getInstance().registerJump(getNumberOneMethod, callerTestMethod);

        Display display = Display.getDefault();
        java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
        display.asyncExec(DialogHelper.closerFor(display, knownShells, shell -> DialogHelper.confirmItem(shell, "testGiveMe1"), 2000));

        TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .methodSearchMode(MethodSearchMode.BY_CALL_AND_BY_NAME) //
                .build();

        IMember member = classTypeFacade.getOneCorrespondingMember(request);

        assertEquals("testGiveMe1", member.getElementName());
        assertEquals(callerTestMethod.getDeclaringType(), member.getDeclaringType());
    }
}
