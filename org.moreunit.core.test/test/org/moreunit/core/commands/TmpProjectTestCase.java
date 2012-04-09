package org.moreunit.core.commands;

import java.io.ByteArrayInputStream;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;

public abstract class TmpProjectTestCase
{
    protected IProject project;

    @Before
    public void createProject() throws Exception
    {
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        project = workspaceRoot.getProject("test-project");
        project.create(null);
        project.open(null);
    }

    @After
    public void deleteProject() throws Exception
    {
        project.delete(true, true, null);
    }

    protected IFile createFile(String filePath, String content) throws CoreException
    {
        IFile file = project.getFile(filePath);
        file.create(new ByteArrayInputStream(content.getBytes()), IResource.NONE, null);
        return file;
    }

    protected void openEditor(IFile sourceFile) throws PartInitException
    {
        IDE.openEditor(getActivePage(), sourceFile);
    }

    protected IWorkbenchPage getActivePage()
    {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    }

    protected IFile getFileInActiveEditor()
    {
        return (IFile) getActivePage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
    }

    protected IEditorPart getActiveEditor()
    {
        return getActivePage().getActiveEditor();
    }

    protected void executeCommand(String commandId) throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException
    {
        IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
        handlerService.executeCommand(commandId, null);
    }
}
