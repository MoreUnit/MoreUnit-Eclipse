package org.moreunit.core.commands;

import static org.moreunit.core.config.CoreModule.$;

import java.util.Collection;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IEditorPart;
import org.moreunit.core.resources.SrcFile;
import org.moreunit.core.resources.Workspace;

public class Selection
{
    private final ExecutionContext executionContext;
    private final Workspace workspace;

    public Selection(ExecutionContext context)
    {
        this.executionContext = context;
        this.workspace = $().getWorkspace();
    }

    public SelectedSrcFile getUniqueSrcFile()
    {
        Object firstElement = getUniqueSelectedElement();
        if(firstElement instanceof IAdaptable)
        {
            IFile file = toFile((IAdaptable) firstElement);
            if(file != null)
                return SelectedSrcFile.fromSelection(toSrcFile(file), executionContext);
        }

        IEditorPart activeEditorPart = executionContext.getActiveEditorPart();
        if(activeEditorPart != null)
        {
            IFile file = toFile(activeEditorPart.getEditorInput());
            if(file != null)
                return SelectedSrcFile.fromEditor(toSrcFile(file), activeEditorPart, executionContext);
        }

        return SelectedSrcFile.none();
    }

    private SrcFile toSrcFile(IFile platformFile)
    {
        return workspace.toSrcFile(platformFile);
    }

    private Object getUniqueSelectedElement()
    {
        IEvaluationContext context = executionContext.getApplicationContext();
        if(context == null)
            return null;

        Collection< ? > selectedElements = (Collection< ? >) context.getDefaultVariable();
        if(selectedElements == null || selectedElements.size() != 1)
            return null;

        return selectedElements.iterator().next();
    }

    private IFile toFile(IAdaptable adaptable)
    {
        return adaptable == null ? null : (IFile) adaptable.getAdapter(IFile.class);
    }
}
