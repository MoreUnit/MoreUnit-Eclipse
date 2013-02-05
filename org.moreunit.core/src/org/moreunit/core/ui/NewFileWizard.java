package org.moreunit.core.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;
import org.moreunit.core.matching.SourceFolderPath;
import org.moreunit.core.resources.ContainerCreationRecord;
import org.moreunit.core.resources.File;
import org.moreunit.core.resources.Workspace;

public class NewFileWizard extends BasicNewFileResourceWizard
{
    private final Workspace workspace;
    private final String fileNameInitialValue;
    private final ContainerCreationRecord maybeCreatedFolder;

    public NewFileWizard(IWorkbench workbench, Workspace workspace, SourceFolderPath selectedFolder, String fileName)
    {
        this.workspace = workspace;
        this.fileNameInitialValue = fileName;
        maybeCreatedFolder = selectedFolder.createResolvedPartIfItDoesNotExist();
        init(workbench, new StructuredSelection(selectedFolder.getResolvedPartAsResource().getUnderlyingPlatformResource()));
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
        File createdFile = workspace.getFile(newResource.getFullPath().toString());
        maybeCreatedFolder.cancelCreationOfFoldersThatAreNotAncestorsOf(createdFile);
        super.selectAndReveal(newResource);
    }

    @Override
    public boolean performCancel()
    {
        maybeCreatedFolder.cancelCreation();
        return super.performCancel();
    }
}
