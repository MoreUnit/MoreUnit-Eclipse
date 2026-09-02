package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.MoveArguments;
import org.junit.jupiter.api.Test;
import org.moreunit.SourceFolderContext;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo; other:Other", testCls = "com:FooTest")
public class MoveClassParticipantTest extends ContextTestCase
{
    @Test
    public void initialize_should_return_false_when_element_is_a_test_case_compilation_unit()
    {
        assertFalse(new TestableParticipant().init(context.getCompilationUnit("com.FooTest"), new MoveArguments(new Object(), true)));
    }

    @Test
    public void initialize_should_return_true_when_element_is_a_class_compilation_unit()
    {
        assertTrue(new TestableParticipant().init(context.getCompilationUnit("com.Foo"), new MoveArguments(new Object(), true)));
    }

    @Test
    public void getName_should_return_moreunit_name()
    {
        assertEquals("MoreUnit testcase move operation", new TestableParticipant().getName());
    }

    @Test
    public void checkConditions_should_return_ok_status()
    {
        TestableParticipant participant = new TestableParticipant();
        participant.init(context.getCompilationUnit("com.Foo"), new MoveArguments(new Object(), true));

        RefactoringStatus status = participant.checkConditions(new NullProgressMonitor(), null);

        assertNotNull(status);
        assertTrue(status.isOK());
    }

    @Test
    public void createChange_should_return_null_when_destination_is_not_a_package_fragment() throws Exception
    {
        TestableParticipant participant = new TestableParticipant();
        participant.init(context.getCompilationUnit("com.Foo"), new MoveArguments(new Object(), true));

        assertNull(participant.createChange(new NullProgressMonitor()));
    }

    @Test
    public void createChange_should_do_nothing_when_test_cases_would_stay_in_destination_package() throws Exception
    {
        // moving "com.Foo" to package "com" means that the test case
        // "com.FooTest" would stay in the very same test package
        TestableParticipant participant = new TestableParticipant();
        participant.init(context.getCompilationUnit("com.Foo"), new MoveArguments(context.getCompilationUnit("com.Foo").getParent(), true));

        assertNull(participant.createChange(new NullProgressMonitor()));
    }

    @Test
    public void createChange_should_not_throw_when_moving_to_another_package() throws Exception
    {
        SourceFolderContext.getInstance().initContextForWorkspace();

        TestableParticipant participant = new TestableParticipant();
        participant.init(context.getCompilationUnit("com.Foo"), new MoveArguments(context.getCompilationUnit("other.Other").getParent(), true));

        // the destination test package does not exist yet: the participant
        // prepares it and builds the move refactoring; whether the resulting
        // change can be built depends on the workspace state, but no exception
        // may escape
        participant.createChange(new NullProgressMonitor());
    }

    private static class TestableParticipant extends MoveClassParticipant
    {
        boolean init(Object element, MoveArguments arguments)
        {
            initialize(arguments);
            return initialize(element);
        }
    }
}
