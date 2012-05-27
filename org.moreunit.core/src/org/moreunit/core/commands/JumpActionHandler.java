package org.moreunit.core.commands;

import static org.eclipse.ui.handlers.HandlerUtil.getActiveShell;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.moreunit.core.MoreUnitCore;
import org.moreunit.core.extension.jump.JumpResult;
import org.moreunit.core.log.Logger;
import org.moreunit.core.matching.DoesNotMatchConfigurationException;
import org.moreunit.core.matching.FileMatcher;
import org.moreunit.core.ui.MessageDialogs;

public class JumpActionHandler extends AbstractHandler
{
    private final Logger logger;
    private final FileMatcher fileMatcher;
    private final JumperExtensionManager extensionManager;

    public JumpActionHandler()
    {
        this(p().getLogger(), new FileMatcher(TextSearchEngine.create(), p().getPreferences(), p().getLogger()), new JumperExtensionManager(p().getLogger()));
    }

    private static MoreUnitCore p()
    {
        return MoreUnitCore.get();
    }

    public JumpActionHandler(Logger logger, FileMatcher fileMatcher, JumperExtensionManager extensionManager)
    {
        this.logger = logger;
        this.fileMatcher = fileMatcher;
        this.extensionManager = extensionManager;
    }

    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IFile selectedFile = getSelectedFile(event);
        if(selectedFile == null)
        {
            return null;
        }

        JumpResult jumpResult = extensionManager.jump(new JumpContext(event, selectedFile));
        if(jumpResult.isDone())
        {
            return null;
        }

        try
        {
            IFile result = fileMatcher.match(selectedFile);

            if(result == null)
            {
                MessageDialogs.openInformation(getActiveShell(event), "No matching file found");
            }
            else
            {
                openEditor(result, event);
            }
        }
        catch (DoesNotMatchConfigurationException e)
        {
            MessageDialogs.openInformation(getActiveShell(event), e.getPath() + " does not match your source folder preferences");
        }

        return null;
    }

    private IFile getSelectedFile(ExecutionEvent event)
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
        if(! (event.getApplicationContext() instanceof EvaluationContext))
        {
            logger.warn("Unsupported context: " + event.getApplicationContext()); //$NON-NLS-1$
            return null;
        }

        EvaluationContext context = (EvaluationContext) event.getApplicationContext();

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

    private void openEditor(IFile file, ExecutionEvent event)
    {
        IWorkbenchPage activePage = getActivePage(event);
        if(activePage == null)
        {
            return;
        }

        try
        {
            IDE.openEditor(activePage, file, true);
        }
        catch (PartInitException e)
        {
            e.printStackTrace();
        }
    }

    private static IWorkbenchPage getActivePage(ExecutionEvent event)
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        return window == null ? null : window.getActivePage();
    }

    // implemented in HandlerUtil only since 3.7
    private static IEditorInput getActiveEditorInput(ExecutionEvent event)
    {
        Object o = HandlerUtil.getVariable(event, ISources.ACTIVE_EDITOR_INPUT_NAME);
        if(o instanceof IEditorInput)
        {
            return (IEditorInput) o;
        }
        return null;
    }
}
