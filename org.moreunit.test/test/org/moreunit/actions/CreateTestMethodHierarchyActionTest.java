package org.moreunit.actions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.junit.jupiter.api.Test;

public class CreateTestMethodHierarchyActionTest
{
    @Test
    public void should_accept_selection_and_ignore_non_structured_selection()
    {
        CreateTestMethodHierarchyAction action = new CreateTestMethodHierarchyAction();

        ISelection nonStructured = mock(ISelection.class);
        action.selectionChanged(null, nonStructured);
        // run with non-structured selection should do nothing (no exception)
        action.run(mock(IAction.class));
    }
}
