package org.moreunit.core.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.moreunit.core.extension.jump.IJumpContext;

public class JumpContext implements IJumpContext
{
    private final ExecutionContext context;
    private final IFile selectedFile;
    private final IEditorPart fileEditor;

    public JumpContext(ExecutionContext context, IFile selectedFile, IEditorPart fileEditor)
    {
        this.context = context;
        this.selectedFile = selectedFile;
        this.fileEditor = fileEditor;
    }

    public ExecutionEvent getExecutionEvent()
    {
        return context.getEvent();
    }

    @Override
    public IEditorPart getOpenEditorPart()
    {
        return fileEditor;
    }

    public IFile getSelectedFile()
    {
        return selectedFile;
    }

    @Override
    public boolean isFileOpenInEditor()
    {
        return fileEditor != null;
    }
}
