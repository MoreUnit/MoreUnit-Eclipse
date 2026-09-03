package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.junit.jupiter.api.Test;

public class MoveMethodChangeTest
{
    private final IProgressMonitor monitor = new NullProgressMonitor();

    @Test
    public void getName_should_describe_the_move()
    {
        final IType sourceType = mock(IType.class);
        final IType destinationType = mock(IType.class);
        final IMethod methodToMove = mock(IMethod.class);
        when(sourceType.getElementName()).thenReturn("FooTest");
        when(destinationType.getElementName()).thenReturn("BarTest");
        when(methodToMove.getElementName()).thenReturn("testFoo");

        final MoveMethodChange change = new MoveMethodChange(sourceType, destinationType, methodToMove);

        assertEquals("Move method testFoo from FooTest to BarTest", change.getName());
    }

    @Test
    public void getModifiedElement_should_return_the_moved_method()
    {
        final IType sourceType = mock(IType.class);
        final IType destinationType = mock(IType.class);
        final IMethod methodToMove = mock(IMethod.class);

        final MoveMethodChange change = new MoveMethodChange(sourceType, destinationType, methodToMove);

        assertEquals(methodToMove, change.getModifiedElement());
    }

    @Test
    public void isValid_should_return_ok_status() throws Exception
    {
        final IType sourceType = mock(IType.class);
        final IType destinationType = mock(IType.class);
        final IMethod methodToMove = mock(IMethod.class);

        final RefactoringStatus status = new MoveMethodChange(sourceType, destinationType, methodToMove).isValid(monitor);

        assertNotNull(status);
        assertTrue(status.isOK());
    }

    @Test
    public void perform_should_move_method_and_return_undo_change_with_swapped_types() throws Exception
    {
        final IType sourceType = mock(IType.class);
        final IType destinationType = mock(IType.class);
        final IMethod methodToMove = mock(IMethod.class);
        when(sourceType.getElementName()).thenReturn("FooTest");
        when(destinationType.getElementName()).thenReturn("BarTest");
        when(methodToMove.getElementName()).thenReturn("testFoo");

        final MoveMethodChange change = new MoveMethodChange(sourceType, destinationType, methodToMove);
        final Change undo = change.perform(monitor);

        verify(methodToMove).move(eq(destinationType), isNull(), isNull(), eq(false), isNull());
        assertNotNull(undo);
        assertInstanceOf(MoveMethodChange.class, undo);
        assertEquals("Move method testFoo from BarTest to FooTest", undo.getName());
    }
}
