package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo", testCls = "com:FooTest")
public class RenamePackageParticipantTest extends ContextTestCase
{
    private IPackageFragment mainPackageFragment()
    {
        return (IPackageFragment) context.getCompilationUnit("com.Foo").getParent();
    }

    private IPackageFragment testPackageFragment()
    {
        return (IPackageFragment) context.getCompilationUnit("com.FooTest").getParent();
    }

    @Test
    public void initialize_should_return_false_when_package_is_in_test_source_folder()
    {
        assertFalse(new TestableParticipant().init(testPackageFragment(), new RenameArguments("org", true)));
    }

    @Test
    public void initialize_should_return_true_when_package_is_not_in_test_source_folder()
    {
        assertTrue(new TestableParticipant().init(mainPackageFragment(), new RenameArguments("org", true)));
    }

    @Test
    public void getName_should_return_moreunit_name()
    {
        assertEquals("MoreUnit Rename Package", new TestableParticipant().getName());
    }

    @Test
    public void checkConditions_should_return_ok_status()
    {
        final TestableParticipant participant = new TestableParticipant();
        participant.init(mainPackageFragment(), new RenameArguments("org", true));

        final RefactoringStatus status = participant.checkConditions(new NullProgressMonitor(), null);

        assertNotNull(status);
        assertTrue(status.isOK());
    }

    @Test
    public void createChange_should_return_null_when_references_should_not_be_updated() throws Exception
    {
        final TestableParticipant participant = new TestableParticipant();
        participant.init(mainPackageFragment(), new RenameArguments("org", false));

        assertNull(participant.createChange(new NullProgressMonitor()));
    }

    @Test
    public void createChange_should_rename_corresponding_test_package() throws Exception
    {
        final TestableParticipant participant = new TestableParticipant();
        participant.init(mainPackageFragment(), new RenameArguments("org", true));

        final Change change = participant.createChange(new NullProgressMonitor());

        assertNotNull(change);
        final CompositeChange compositeChange = assertInstanceOf(CompositeChange.class, change);
        assertEquals(1, compositeChange.getChildren().length);
    }

    @Test
    public void createChange_should_return_empty_change_when_no_test_package_exists() throws Exception
    {
        context.getProjectHandler().getMainSrcFolderHandler().createClass("empty.Empty");
        final IPackageFragment emptyPackage = (IPackageFragment) context.getCompilationUnit("empty.Empty").getParent();

        final TestableParticipant participant = new TestableParticipant();
        participant.init(emptyPackage, new RenameArguments("renamed", true));

        final Change change = participant.createChange(new NullProgressMonitor());

        assertNotNull(change);
        final CompositeChange compositeChange = assertInstanceOf(CompositeChange.class, change);
        assertEquals(0, compositeChange.getChildren().length);
    }

    private static class TestableParticipant extends RenamePackageParticipant
    {
        boolean init(Object element, RenameArguments arguments)
        {
            initialize(arguments);
            return initialize(element);
        }
    }
}
