package org.moreunit.core.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.moreunit.core.extension.jump.IJumpContext;

public class JumpContext implements IJumpContext
{
    private final ExecutionEvent event;
    private final IFile selectedFile;

    public JumpContext(ExecutionEvent event, IFile selectedFile)
    {
        this.event = event;
        this.selectedFile = selectedFile;
    }

    public ExecutionEvent getExecutionEvent()
    {
        return event;
    }

    public IFile getSelectedFile()
    {
        return selectedFile;
    }
}
