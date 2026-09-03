package org.moreunit.actions;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.junit.jupiter.api.Test;

public class ActionsSelectionGuardTest
{
    @Test
    public void run_test_from_type_should_throw_when_selection_not_structured()
    {
        final RunTestFromTypeAction action = new RunTestFromTypeAction();
        assertThrows(ClassCastException.class, () ->
            action.selectionChanged(mock(IAction.class), mock(ISelection.class)));
    }

    @Test
    public void debug_test_from_type_should_throw_when_selection_not_structured()
    {
        final DebugTestFromTypeAction action = new DebugTestFromTypeAction();
        assertThrows(ClassCastException.class, () ->
            action.selectionChanged(mock(IAction.class), mock(ISelection.class)));
    }

    @Test
    public void run_test_from_compilation_unit_should_throw_when_selection_not_structured()
    {
        final RunTestFromCompilationUnitAction action = new RunTestFromCompilationUnitAction();
        assertThrows(ClassCastException.class, () ->
            action.selectionChanged(mock(IAction.class), mock(ISelection.class)));
    }

    @Test
    public void jump_from_type_should_throw_when_selection_not_structured()
    {
        final JumpFromTypeAction action = new JumpFromTypeAction();
        assertThrows(ClassCastException.class, () ->
            action.selectionChanged(mock(IAction.class), mock(ISelection.class)));
    }

    @Test
    public void jump_from_compilation_unit_should_throw_when_selection_not_structured()
    {
        final JumpFromCompilationUnitAction action = new JumpFromCompilationUnitAction();
        assertThrows(ClassCastException.class, () ->
            action.selectionChanged(mock(IAction.class), mock(ISelection.class)));
    }
}
