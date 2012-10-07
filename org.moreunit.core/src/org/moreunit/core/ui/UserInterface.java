package org.moreunit.core.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.moreunit.core.log.Logger;
import org.moreunit.core.matching.SourceFolderPath;

public class UserInterface
{
    private final IWorkbench workbench;
    private final IWorkbenchPage activePage;
    private final DialogFactory dialogFactory;
    private final WizardFactory wizardFactory;
    private final Logger logger;

    public UserInterface(IWorkbench workbench, IWorkbenchPage activePage, DialogFactory dialogFactory, WizardFactory wizardFactory, Logger logger)
    {
        this.workbench = workbench;
        this.activePage = activePage;
        this.dialogFactory = dialogFactory;
        this.wizardFactory = wizardFactory;
        this.logger = logger;
    }

    public IWorkbench getWorkbench()
    {
        return workbench;
    }

    public WizardDialog<NewFileWizard> createNewFileWizard(SourceFolderPath selectedFolder, String fileName)
    {
        return wizardFactory.createNewFileWizard(selectedFolder, fileName);
    }

    public void openEditor(IFile file)
    {
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
            logger.error("Could not open editor for " + file, e);
        }
    }

    public void showError(String message)
    {
        dialogFactory.createErrorDialog(message).open();
    }

    public void showInfo(String message)
    {
        dialogFactory.createInfoDialog(message).open();
    }
}
