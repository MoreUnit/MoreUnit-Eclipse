package org.moreunit.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.IStatus;
import org.junit.jupiter.api.Test;
import org.moreunit.MoreUnitPlugin;

public class MoreUnitStatusTest
{
    @Test
    public void should_create_ok_status_by_default()
    {
        MoreUnitStatus status = new MoreUnitStatus();

        assertThat(status.isOK()).isTrue();
        assertThat(status.getSeverity()).isEqualTo(IStatus.OK);
        assertThat(status.getCode()).isEqualTo(IStatus.OK);
        assertThat(status.getMessage()).isNull();
    }

    @Test
    public void should_create_status_with_severity_and_message()
    {
        MoreUnitStatus status = new MoreUnitStatus(IStatus.ERROR, "An error occurred");

        assertThat(status.isOK()).isFalse();
        assertThat(status.getSeverity()).isEqualTo(IStatus.ERROR);
        assertThat(status.getCode()).isEqualTo(IStatus.ERROR);
        assertThat(status.getMessage()).isEqualTo("An error occurred");
    }

    @Test
    public void getChildren_should_return_empty_array()
    {
        MoreUnitStatus status = new MoreUnitStatus();
        assertThat(status.getChildren()).isEmpty();
    }

    @Test
    public void getException_should_return_null()
    {
        MoreUnitStatus status = new MoreUnitStatus();
        assertThat(status.getException()).isNull();
    }

    @Test
    public void getPlugin_should_return_moreunit_plugin_id()
    {
        MoreUnitStatus status = new MoreUnitStatus();
        assertThat(status.getPlugin()).isEqualTo(MoreUnitPlugin.PLUGIN_ID);
    }

    @Test
    public void isMultiStatus_should_return_false()
    {
        MoreUnitStatus status = new MoreUnitStatus();
        assertThat(status.isMultiStatus()).isFalse();
    }

    @Test
    public void matches_should_return_true_if_severity_matches()
    {
        MoreUnitStatus status = new MoreUnitStatus(IStatus.ERROR, "Error");
        assertThat(status.matches(IStatus.ERROR)).isTrue();
        assertThat(status.matches(IStatus.ERROR | IStatus.WARNING)).isTrue();
        assertThat(status.matches(IStatus.WARNING)).isFalse();
    }
}
