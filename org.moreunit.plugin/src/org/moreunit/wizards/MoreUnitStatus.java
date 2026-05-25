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

    @Override
    public IStatus[] getChildren()
    {
        return new IStatus[0];
    }

    @Override
    public int getCode()
    {
        return fSeverity;
    }

    @Override
    public Throwable getException()
    {
        return null;
    }

    @Override
    public String getMessage()
    {
        return fStatusMessage;
    }

    @Override
    public String getPlugin()
    {
        return MoreUnitPlugin.PLUGIN_ID;
    }

    @Override
    public int getSeverity()
    {
        return fSeverity;
    }

    @Override
    public boolean isMultiStatus()
    {
        return false;
    }

    @Override
    public boolean isOK()
    {
        return fSeverity == IStatus.OK;
    }

    @Override
    public boolean matches(int severityMask)
    {
        return (fSeverity & severityMask) != 0;
    }

}
