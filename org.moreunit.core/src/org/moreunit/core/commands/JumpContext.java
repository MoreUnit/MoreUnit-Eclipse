package org.moreunit.core.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.moreunit.core.extension.jump.IJumpContext;

public class JumpContext implements IJumpContext
{
    private final ExecutionContext context;
    private final IFile selectedFile;

    public JumpContext(ExecutionContext context, IFile selectedFile)
    {
        this.context = context;
        this.selectedFile = selectedFile;
    }

    public ExecutionEvent getExecutionEvent()
    {
        return context.getEvent();
    }

    public IFile getSelectedFile()
    {
        return selectedFile;
    }
}
