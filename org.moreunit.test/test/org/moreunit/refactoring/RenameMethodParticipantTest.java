package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;

/**
 * With the default (prefix-less) naming preferences, the test method keeping
 * the name of the renamed method is "foo" (same name as the method under
 * test).
 */
@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo", testCls = "com:FooTest")
public class RenameMethodParticipantTest extends ContextTestCase
{
    private MethodHandler methodUnderTest;
    private MethodHandler testMethod;

    @BeforeEach
    public void setUp()
    {
        TypeHandler cutType = context.getCompilationUnitHandler("com.Foo").getPrimaryTypeHandler();
        methodUnderTest = cutType.addMethod("public int foo()", "return 0;");

        TypeHandler testType = context.getCompilationUnitHandler("com.FooTest").getPrimaryTypeHandler();
        testMethod = testType.addMethod("public void foo()", "");
    }

    @Test
    public void initialize_should_return_false_when_element_is_a_method_of_a_test_case()
    {
        assertFalse(new TestableParticipant().init(testMethod.get(), new RenameArguments("newName", true)));
    }

    @Test
    public void initialize_should_return_true_when_element_is_a_method_of_a_class_under_test()
    {
        assertTrue(new TestableParticipant().init(methodUnderTest.get(), new RenameArguments("newName", true)));
    }

    @Test
    public void checkConditions_should_return_ok_status()
    {
        TestableParticipant participant = new TestableParticipant();
        participant.init(methodUnderTest.get(), new RenameArguments("bar", true));

        RefactoringStatus status = participant.checkConditions(new NullProgressMonitor(), null);

        assertNotNull(status);
        assertTrue(status.isOK());
    }

    @Test
    public void createChange_should_return_null_when_references_should_not_be_updated() throws Exception
    {
        TestableParticipant participant = new TestableParticipant();
        participant.init(methodUnderTest.get(), new RenameArguments("bar", false));

        assertNull(participant.createChange(new NullProgressMonitor()));
    }

    @Test
    public void createChange_should_return_null_when_no_test_method_exists_for_the_renamed_method() throws Exception
    {
        MethodHandler methodWithoutTest = context.getPrimaryTypeHandler("com.Foo").addMethod("public int baz()", "return 1;");

        TestableParticipant participant = new TestableParticipant();
        participant.init(methodWithoutTest.get(), new RenameArguments("qux", true));

        assertNull(participant.createChange(new NullProgressMonitor()));
    }

    @Test
    public void createChange_should_rename_corresponding_test_methods() throws Exception
    {
        TestableParticipant participant = new TestableParticipant();
        participant.init(methodUnderTest.get(), new RenameArguments("bar", true));
        awaitSearchResult(() -> new org.moreunit.elements.ClassTypeFacade(context.getCompilationUnit("com.Foo")).getCorrespondingTestMethodsByName(methodUnderTest.get()));

        Change change = participant.createChange(new NullProgressMonitor());

        assertNotNull(change);
        assertInstanceOf(RenameMethodChange.class, change);
        assertEquals(testMethod.get(), change.getModifiedElement());
        assertEquals("Rename method foo in FooTest to bar", change.getName());
    }


    /**
     * JDT search results depend on the (asynchronous) index state: freshly
     * created projects are not always fully indexed when a test starts. This
     * helper polls the given search until it returns a non-empty result.
     */
    private void awaitSearchResult(java.util.function.Supplier<java.util.Collection< ? >> search) throws Exception
    {
        org.eclipse.swt.widgets.Display display = org.eclipse.swt.widgets.Display.getDefault();
        long deadline = System.currentTimeMillis() + 20_000;
        while (System.currentTimeMillis() < deadline)
        {
            if(! search.get().isEmpty())
                return;
            display.readAndDispatch();
            Thread.sleep(25);
        }
    }

    private static class TestableParticipant extends RenameMethodParticipant
    {
        boolean init(Object element, RenameArguments arguments)
        {
            initialize(arguments);
            return initialize(element);
        }
    }
}
