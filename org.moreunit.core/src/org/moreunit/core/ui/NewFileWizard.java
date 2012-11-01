package org.moreunit.core.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;
import org.moreunit.core.log.Logger;
import org.moreunit.core.matching.SourceFolderPath;
import org.moreunit.core.resources.CreatedFolderPath;

public class NewFileWizard extends BasicNewFileResourceWizard
{
    private final String fileNameInitialValue;
    private final CreatedFolderPath createdFolderPath;
    private final Logger logger;
    private IFile createdFile;

    public NewFileWizard(IWorkbench workbench, SourceFolderPath selectedFolder, String fileName, Logger logger)
    {
        this.fileNameInitialValue = fileName;
        this.logger = logger;
        createdFolderPath = selectedFolder.createResolvedPartIfItDoesNotExist();
        init(workbench, new StructuredSelection(selectedFolder.getResolvedPartAsResource()));
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
        createdFile = (IFile) newResource.getAdapter(IFile.class);

        try
        {
            createdFolderPath.deleteFoldersThatAreNotParentOf(createdFile);
        }
        catch (CoreException e)
        {
            logger.error("Could not delete " + createdFolderPath, e);
        }

        super.selectAndReveal(newResource);
    }

    public IFile getCreatedFile()
    {
        return createdFile;
    }

    @Override
    public boolean performCancel()
    {
        try
        {
            createdFolderPath.delete();
        }
        catch (CoreException e)
        {
            logger.error("Could not delete " + createdFolderPath, e);
        }
        return super.performCancel();
    }
}
