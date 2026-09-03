package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.junit.jupiter.api.Test;

public class RenameMethodChangeTest
{
    private final IProgressMonitor monitor = new NullProgressMonitor();

    @Test
    public void getName_should_describe_the_rename()
    {
        final IMethod testMethod = mock(IMethod.class);
        final IType declaringType = mock(IType.class);
        when(testMethod.getElementName()).thenReturn("testFoo");
        when(testMethod.getDeclaringType()).thenReturn(declaringType);
        when(declaringType.getElementName()).thenReturn("FooTest");

        final RenameMethodChange change = new RenameMethodChange(testMethod, "testBar");

        assertEquals("Rename method testFoo in FooTest to testBar", change.getName());
    }

    @Test
    public void getModifiedElement_should_return_the_renamed_method()
    {
        final IMethod testMethod = mock(IMethod.class);
        final IType declaringType = mock(IType.class);
        when(testMethod.getDeclaringType()).thenReturn(declaringType);

        final RenameMethodChange change = new RenameMethodChange(testMethod, "testBar");

        assertEquals(testMethod, change.getModifiedElement());
    }

    @Test
    public void isValid_should_return_ok_status()
    {
        final IMethod testMethod = mock(IMethod.class);
        final IType declaringType = mock(IType.class);
        when(testMethod.getDeclaringType()).thenReturn(declaringType);

        final RefactoringStatus status = new RenameMethodChange(testMethod, "testBar").isValid(monitor);

        assertNotNull(status);
        assertTrue(status.isOK());
    }

    @Test
    public void perform_should_rename_method_and_return_undo_change() throws Exception
    {
        final IMethod testMethod = mock(IMethod.class);
        final IType declaringType = mock(IType.class);
        final IMethod renamedMethod = mock(IMethod.class);
        when(testMethod.getElementName()).thenReturn("testFoo");
        when(testMethod.getDeclaringType()).thenReturn(declaringType);
        when(declaringType.getMethods()).thenReturn(new IMethod[] { renamedMethod });
        when(renamedMethod.getElementName()).thenReturn("testBar");

        final RenameMethodChange change = new RenameMethodChange(testMethod, "testBar");
        final Change undo = change.perform(monitor);

        verify(testMethod).rename("testBar", false, monitor);
        assertNotNull(undo);
        assertInstanceOf(RenameMethodChange.class, undo);
    }

    @Test
    public void perform_should_return_null_undo_when_renamed_method_cannot_be_found() throws Exception
    {
        final IMethod testMethod = mock(IMethod.class);
        final IType declaringType = mock(IType.class);
        when(testMethod.getElementName()).thenReturn("testFoo");
        when(testMethod.getDeclaringType()).thenReturn(declaringType);
        when(declaringType.getMethods()).thenReturn(new IMethod[0]);

        final RenameMethodChange change = new RenameMethodChange(testMethod, "testBar");

        assertNull(change.perform(monitor));
    }
}
