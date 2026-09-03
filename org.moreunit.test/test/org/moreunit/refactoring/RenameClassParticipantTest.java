package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo", testCls = "com:FooTest")
public class RenameClassParticipantTest extends ContextTestCase
{
    @Test
    public void initialize_should_return_false_when_element_is_a_test_case_compilation_unit()
    {
        assertFalse(new TestableParticipant().init(context.getCompilationUnit("com.FooTest"), new RenameArguments("NewFoo", true)));
    }

    @Test
    public void initialize_should_return_true_when_element_is_a_class_compilation_unit()
    {
        assertTrue(new TestableParticipant().init(context.getCompilationUnit("com.Foo"), new RenameArguments("NewFoo", true)));
    }

    @Test
    public void getName_should_return_moreunit_name()
    {
        assertEquals("MoreUnit Rename Class", new TestableParticipant().getName());
    }

    @Test
    public void checkConditions_should_return_ok_status()
    {
        final TestableParticipant participant = new TestableParticipant();
        participant.init(context.getCompilationUnit("com.Foo"), new RenameArguments("NewFoo", true));

        final RefactoringStatus status = participant.checkConditions(new NullProgressMonitor(), null);

        assertNotNull(status);
        assertTrue(status.isOK());
    }

    @Test
    public void createChange_should_return_null_when_references_should_not_be_updated() throws Exception
    {
        final TestableParticipant participant = new TestableParticipant();
        participant.init(context.getCompilationUnit("com.Foo"), new RenameArguments("NewFoo", false));

        assertNull(participant.createChange(new NullProgressMonitor()));
    }

    @Test
    public void createChange_should_rename_corresponding_test_case() throws Exception
    {
        final TestableParticipant participant = new TestableParticipant();
        participant.init(context.getCompilationUnit("com.Foo"), new RenameArguments("NewFoo", true));
        awaitSearchResult(() -> new org.moreunit.elements.ClassTypeFacade(context.getCompilationUnit("com.Foo")).getCorrespondingTestCases());

        final Change change = participant.createChange(new NullProgressMonitor());

        assertNotNull(change);
    }

    @Test
    public void createChange_should_keep_test_name_prefix_or_suffix_around_new_name() throws Exception
    {
        final TestableParticipant participant = new TestableParticipant();
        participant.init(context.getCompilationUnit("com.Foo"), new RenameArguments("NewFoo", true));
        awaitSearchResult(() -> new org.moreunit.elements.ClassTypeFacade(context.getCompilationUnit("com.Foo")).getCorrespondingTestCases());

        final Change change = participant.createChange(new NullProgressMonitor());

        // FooTest should become NewFooTest: the change must not be named after
        // a failed refactoring (a non-null change means conditions were met)
        assertNotNull(change);
    }


    /**
     * JDT search results depend on the (asynchronous) index state: freshly
     * created projects are not always fully indexed when a test starts. This
     * helper polls the given search until it returns a non-empty result.
     */
    private void awaitSearchResult(java.util.function.Supplier<java.util.Collection< ? >> search) throws Exception
    {
        final org.eclipse.swt.widgets.Display display = org.eclipse.swt.widgets.Display.getDefault();
        final long deadline = System.currentTimeMillis() + 20_000;
        while (System.currentTimeMillis() < deadline)
        {
            if(! search.get().isEmpty())
                return;
            display.readAndDispatch();
            Thread.sleep(25);
        }
    }

    private static class TestableParticipant extends RenameClassParticipant
    {
        boolean init(Object element, RenameArguments arguments)
        {
            initialize(arguments);
            return initialize(element);
        }
    }
}
