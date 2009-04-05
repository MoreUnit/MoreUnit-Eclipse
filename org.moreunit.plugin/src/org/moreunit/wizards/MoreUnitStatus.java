package org.moreunit.wizards;

import org.eclipse.core.runtime.IStatus;
import org.moreunit.MoreUnitPlugin;

public class MoreUnitStatus implements IStatus
{

    private String fStatusMessage;
    private int fSeverity;

    public MoreUnitStatus()
    {
        this(OK, null);
    }

    public MoreUnitStatus(int severity, String message)
    {
        fStatusMessage = message;
        fSeverity = severity;
    }

    public IStatus[] getChildren()
    {
        return new IStatus[0];
    }

    public int getCode()
    {
        return fSeverity;
    }

    public Throwable getException()
    {
        return null;
    }

    public String getMessage()
    {
        return fStatusMessage;
    }

    public String getPlugin()
    {
        return MoreUnitPlugin.PLUGIN_ID;
    }

    public int getSeverity()
    {
        return fSeverity;
    }

    public boolean isMultiStatus()
    {
        return false;
    }

    public boolean isOK()
    {
        return fSeverity == IStatus.OK;
    }

    public boolean matches(int severityMask)
    {
        return (fSeverity & severityMask) != 0;
    }

}
