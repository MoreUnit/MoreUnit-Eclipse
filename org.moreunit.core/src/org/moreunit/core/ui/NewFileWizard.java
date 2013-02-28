package org.moreunit.core.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;
import org.moreunit.core.matching.SourceFolderPath;
import org.moreunit.core.resources.ContainerCreationRecord;
import org.moreunit.core.resources.SrcFile;
import org.moreunit.core.resources.Workspace;

public class NewFileWizard extends BasicNewFileResourceWizard
{
    private final Workspace workspace;
    private final String fileNameInitialValue;
    private final ContainerCreationRecord maybeCreatedFolder;
    private FileCreationListener creationListener;

    public NewFileWizard(IWorkbench workbench, Workspace workspace, SourceFolderPath selectedFolder, String fileName)
    {
        this.workspace = workspace;
        this.fileNameInitialValue = fileName;
        maybeCreatedFolder = selectedFolder.createResolvedPartIfItDoesNotExist();
        init(workbench, new StructuredSelection(selectedFolder.getResolvedPartAsResource().getUnderlyingPlatformResource()));
        setFileCreationListener(new MarkCorrespondingFileAsTestedIfRequired());
    }

    @Override
    public void addPage(IWizardPage page)
    {
        if(page instanceof WizardNewFileCreationPage)
        {
            ((WizardNewFileCreationPage) page).setFileName(fileNameInitialValue);
        }
        super.addPage(page);
    }

    @Override
    protected void selectAndReveal(IResource newResource)
    {
        SrcFile createdFile = workspace.toSrcFile((IFile) newResource);
        maybeCreatedFolder.cancelCreationOfFoldersThatAreNotAncestorsOf(createdFile);
        creationListener.fileCreated(createdFile);
        super.selectAndReveal(newResource);
    }

    @Override
    public boolean performCancel()
    {
        maybeCreatedFolder.cancelCreation();
        return super.performCancel();
    }

    public void setFileCreationListener(FileCreationListener creationListener)
    {
        this.creationListener = creationListener;
    }
}
