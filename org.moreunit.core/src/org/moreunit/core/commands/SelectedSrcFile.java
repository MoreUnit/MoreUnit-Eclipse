package org.moreunit.core.commands;

import org.eclipse.ui.IEditorPart;
import org.moreunit.core.extension.jump.IJumpContext;
import org.moreunit.core.resources.SrcFile;

public class SelectedSrcFile
{
    public static SelectedSrcFile fromEditor(SrcFile file, IEditorPart editorPart, ExecutionContext context)
    {
        return new SelectedSrcFile(file, editorPart, context);
    }

    public static SelectedSrcFile fromSelection(SrcFile file, ExecutionContext context)
    {
        return new SelectedSrcFile(file, null, context);
    }

    public static SelectedSrcFile none()
    {
        return new SelectedSrcFile(null, null, null);
    }

    private final SrcFile file;
    private final IEditorPart editorPart;
    private final ExecutionContext context;

    private SelectedSrcFile(SrcFile file, IEditorPart editorPart, ExecutionContext context)
    {
        this.file = file;
        this.editorPart = editorPart;
        this.context = context;
    }

    public boolean isSupported()
    {
        return file != null && file.isSupported();
    }

    public SrcFile getSrcFile()
    {
        return file;
    }

    public IJumpContext createJumpContext()
    {
        if(file == null)
            throw new IllegalStateException("A jump context should not be created without a file");
        return new JumpContext(context, file.getUnderlyingPlatformFile(), editorPart);
    }
}
