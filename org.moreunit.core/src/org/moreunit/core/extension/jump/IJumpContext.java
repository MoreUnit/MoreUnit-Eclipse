package org.moreunit.core.extension.jump;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;

public interface IJumpContext
{
    ExecutionEvent getExecutionEvent();

    IFile getSelectedFile();
}
