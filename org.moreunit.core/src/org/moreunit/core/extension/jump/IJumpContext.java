package org.moreunit.core.extension.jump;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;

public interface IJumpContext
{
    ExecutionEvent getExecutionEvent();

    IEditorPart getOpenEditorPart();

    IFile getSelectedFile();

    boolean isFileOpenInEditor();
}
