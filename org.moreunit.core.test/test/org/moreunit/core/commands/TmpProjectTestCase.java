package org.moreunit.core.commands;

import static com.google.common.collect.Sets.newHashSet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ContributorFactoryOSGi;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;
import org.moreunit.core.MoreUnitCoreTest;
import org.moreunit.test.context.StringUtils;

@SuppressWarnings("restriction")
public abstract class TmpProjectTestCase
{
    protected static final String TEST_PROJECT = "test-project";

    protected IProject project;
    private Set<String> extensionsToClean = newHashSet();

    @Before
    public void createProject() throws Exception
    {
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        project = workspaceRoot.getProject(TEST_PROJECT);
        project.create(null);
        project.open(null);
    }

    @After
    public void deleteProject() throws Exception
    {
        project.delete(true, true, null);
    }

    protected IFile createFile(String filePath) throws CoreException
    {
        IFile file = project.getFile(filePath);

        IPath fullPath = file.getFullPath();
        if(fullPath.segmentCount() > 2)
        {
            createFolder(project, fullPath.removeFirstSegments(1).removeLastSegments(1));
        }

        file.create(new ByteArrayInputStream("".getBytes()), IResource.NONE, null);
        return file;
    }

    private static IFolder createFolder(IProject project, IPath folderPath) throws CoreException
    {
        IFolder srcFolder = project.getFolder(folderPath);
        if(srcFolder.exists())
        {
            return srcFolder;
        }

        IFolder folder = null;

        for (String part : StringUtils.split(folderPath.toString(), "/"))
        {
            if(folder == null)
            {
                folder = project.getFolder(part);
            }
            else
            {
                folder = folder.getFolder(part);
            }

            if(! folder.exists())
            {
                folder.create(false, true, null);
            }
        }

        return folder;
    }

    protected IFile getFile(String filePath) throws CoreException
    {
        return project.getFile(filePath);
    }

    protected static void openEditor(IFile sourceFile) throws PartInitException
    {
        IDE.openEditor(getActivePage(), sourceFile, true);
    }

    protected static IWorkbenchPage getActivePage()
    {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    }

    protected static IFile getFileInActiveEditor()
    {
        return (IFile) getActiveEditor().getEditorInput().getAdapter(IFile.class);
    }

    protected static IEditorPart getActiveEditor()
    {
        return getActivePage().getActiveEditor();
    }

    protected void executeCommand(String commandId) throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException
    {
        IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
        handlerService.executeCommand(commandId, null);
    }

    protected void addExtension(String id, String point, String content)
    {
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

        IContributor contributor = ContributorFactoryOSGi.createContributor(MoreUnitCoreTest.get().getBundle());

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?eclipse version=\"3.4\"?><plugin>" //
                     + "<extension id=\"" + id + "\" point=\"" + point + "\">" + content + "</extension>" //
                     + "</plugin>";
        InputStream is = new ByteArrayInputStream(xml.getBytes());

        if(extensionRegistry.addContribution(is, contributor, false, null, null, getTempUserToken()))
        {
            extensionsToClean.add(id);
        }
    }

    private Object getTempUserToken()
    {
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        return ((ExtensionRegistry) extensionRegistry).getTemporaryUserToken();
    }

    @After
    public void cleanExtensions()
    {
        for (String id : extensionsToClean)
        {
            try
            {
                removeExtension(id);
            }
            catch (Exception e)
            {
                // ignored, try to clean next extensions
            }
        }
    }

    protected void removeExtension(String id)
    {
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

        IExtension extension = extensionRegistry.getExtension(id);

        extensionRegistry.removeExtension(extension, getTempUserToken());
    }
}
