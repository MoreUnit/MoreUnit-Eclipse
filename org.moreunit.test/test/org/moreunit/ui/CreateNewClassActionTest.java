package org.moreunit.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.Test;

public class CreateNewClassActionTest
{
    @Test
    public void should_provide_element_via_execute()
    {
        final IType createdType = mock(IType.class);

        final CreateNewClassAction action = new CreateNewClassAction()
        {
            @Override
            public IType execute()
            {
                return createdType;
            }
        };

        assertTrue(action.provideElement());
        assertEquals(createdType, action.execute());
        assertEquals("New Class...", action.getText());
        assertNotNull(action.getImage());
    }
}
