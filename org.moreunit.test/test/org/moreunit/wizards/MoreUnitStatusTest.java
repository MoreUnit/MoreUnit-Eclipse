package org.moreunit.wizards;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.core.runtime.IStatus;
import org.junit.jupiter.api.Test;
import org.moreunit.MoreUnitPlugin;

public class MoreUnitStatusTest
{
    @Test
    public void should_create_ok_status_by_default()
    {
        MoreUnitStatus status = new MoreUnitStatus();

        assertTrue(status.isOK());
        assertEquals(IStatus.OK, status.getSeverity());
        assertEquals(IStatus.OK, status.getCode());
        assertNull(status.getMessage());
    }

    @Test
    public void should_create_status_with_severity_and_message()
    {
        MoreUnitStatus status = new MoreUnitStatus(IStatus.ERROR, "An error occurred");

        assertFalse(status.isOK());
        assertEquals(IStatus.ERROR, status.getSeverity());
        assertEquals(IStatus.ERROR, status.getCode());
        assertEquals("An error occurred", status.getMessage());
    }

    @Test
    public void getChildren_should_return_empty_array()
    {
        MoreUnitStatus status = new MoreUnitStatus();
        assertArrayEquals(new IStatus[0], status.getChildren());
    }

    @Test
    public void getException_should_return_null()
    {
        MoreUnitStatus status = new MoreUnitStatus();
        assertNull(status.getException());
    }

    @Test
    public void getPlugin_should_return_moreunit_plugin_id()
    {
        MoreUnitStatus status = new MoreUnitStatus();
        assertEquals(MoreUnitPlugin.PLUGIN_ID, status.getPlugin());
    }

    @Test
    public void isMultiStatus_should_return_false()
    {
        MoreUnitStatus status = new MoreUnitStatus();
        assertFalse(status.isMultiStatus());
    }

    @Test
    public void matches_should_return_true_if_severity_matches()
    {
        MoreUnitStatus status = new MoreUnitStatus(IStatus.ERROR, "Error");
        assertTrue(status.matches(IStatus.ERROR));
        assertTrue(status.matches(IStatus.ERROR | IStatus.WARNING));
        assertFalse(status.matches(IStatus.WARNING));
    }
}
