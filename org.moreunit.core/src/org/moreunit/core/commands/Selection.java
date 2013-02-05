package org.moreunit.core.commands;

import static org.moreunit.core.config.CoreModule.$;

import java.util.Collection;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moreunit.core.log.Logger;
import org.moreunit.core.resources.ConcreteSrcFile;
import org.moreunit.core.resources.File;
import org.moreunit.core.resources.SrcFile;
import org.moreunit.core.resources.Workspace;

public class Selection
{
    private final ExecutionEvent event;
    private final Workspace workspace;
    private final Logger logger;

    public Selection(ExecutionEvent event)
    {
        this.event = event;
        this.workspace = $().getWorkspace();
        this.logger = $().getLogger();
    }

    public SrcFile getUniqueSrcFile()
    {
        IFile platformFile = uniqueFile();
        if(platformFile == null)
            return null;

        File ourFile = workspace.getFile(platformFile.getFullPath().toString());
        return new ConcreteSrcFile(ourFile);
    }

    private IFile uniqueFile()
    {
        Object firstElement = getUniqueSelectedElement(event);
        if(firstElement instanceof IAdaptable)
        {
            IFile file = toFile((IAdaptable) firstElement);
            if(file != null)
            {
                return file;
            }
        }

        IFile file = toFile(getActiveEditorInput(event));
        if(file != null)
        {
            return file;
        }

        return null;
    }

    private Object getUniqueSelectedElement(ExecutionEvent event)
    {
        if(! (event.getApplicationContext() instanceof IEvaluationContext))
        {
            logger.trace("Unsupported context: " + event.getApplicationContext()); //$NON-NLS-1$
            return null;
        }

        IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();

        Collection< ? > selectedElements = (Collection< ? >) context.getDefaultVariable();
        if(selectedElements == null || selectedElements.size() != 1)
        {
            return null;
        }

        return selectedElements.iterator().next();
    }

    private IFile toFile(IAdaptable adaptable)
    {
        return adaptable == null ? null : (IFile) adaptable.getAdapter(IFile.class);
    }

    // implemented in HandlerUtil only since 3.7
    private static IEditorInput getActiveEditorInput(ExecutionEvent event)
    {
        Object editor = HandlerUtil.getVariable(event, ISources.ACTIVE_EDITOR_NAME);
        if(editor instanceof IEditorPart)
        {
            return ((IEditorPart) editor).getEditorInput();
        }
        return null;
    }
}
