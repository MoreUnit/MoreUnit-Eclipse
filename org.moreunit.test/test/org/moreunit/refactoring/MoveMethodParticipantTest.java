package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.MoveArguments;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo, Bar", testCls = "com:FooTest, BarTest")
public class MoveMethodParticipantTest extends ContextTestCase
{
    private MethodHandler fooMethod;
    private MethodHandler testFooMethod;
    private IType barType;

    @BeforeEach
    public void setUp()
    {
        TypeHandler fooType = context.getCompilationUnitHandler("com.Foo").getPrimaryTypeHandler();
        fooMethod = fooType.addMethod("public int foo()", "return 0;");

        TypeHandler fooTestType = context.getCompilationUnitHandler("com.FooTest").getPrimaryTypeHandler();
        testFooMethod = fooTestType.addMethod("public void foo()", "");

        barType = context.getPrimaryTypeHandler("com.Bar").get();
    }

    @Test
    public void initialize_should_return_false_when_element_is_a_method_of_a_test_case()
    {
        assertFalse(new TestableParticipant().init(testFooMethod.get(), new MoveArguments(barType, true)));
    }

    @Test
    public void initialize_should_return_true_when_element_is_a_method_of_a_class_under_test()
    {
        assertTrue(new TestableParticipant().init(fooMethod.get(), new MoveArguments(barType, true)));
    }

    @Test
    public void checkConditions_should_return_ok_status()
    {
        TestableParticipant participant = new TestableParticipant();
        participant.init(fooMethod.get(), new MoveArguments(barType, true));

        RefactoringStatus status = participant.checkConditions(new NullProgressMonitor(), null);

        assertNotNull(status);
        assertTrue(status.isOK());
    }

    @Test
    public void createChange_should_return_null_when_destination_has_no_corresponding_test_case() throws Exception
    {
        TypeHandler bazType = context.getProjectHandler().getMainSrcFolderHandler().createClass("com.Baz");

        TestableParticipant participant = new TestableParticipant();
        participant.init(fooMethod.get(), new MoveArguments(bazType.get(), true));

        assertNull(participant.createChange(new NullProgressMonitor()));
    }

    @Test
    public void createChange_should_create_move_change_for_corresponding_test_methods() throws Exception
    {
        TestableParticipant participant = new TestableParticipant();
        participant.init(fooMethod.get(), new MoveArguments(barType, true));
        awaitSearchResult(() -> new org.moreunit.elements.ClassTypeFacade(context.getCompilationUnit("com.Bar")).getCorrespondingTestCases());
        awaitSearchResult(() -> new org.moreunit.elements.ClassTypeFacade(context.getCompilationUnit("com.Foo")).getCorrespondingTestMethodsByName(fooMethod.get()));

        Change change = participant.createChange(new NullProgressMonitor());

        assertNotNull(change);
        assertInstanceOf(MoveMethodChange.class, change);
        assertEquals(testFooMethod.get(), change.getModifiedElement());
        assertEquals("Move method foo from Foo to BarTest", change.getName());
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

    private static class TestableParticipant extends MoveMethodParticipant
    {
        boolean init(Object element, MoveArguments arguments)
        {
            initialize(arguments);
            return initialize(element);
        }
    }
}
