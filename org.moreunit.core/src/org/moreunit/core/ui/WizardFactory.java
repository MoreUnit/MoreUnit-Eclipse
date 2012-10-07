package org.moreunit.core.ui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.moreunit.core.log.Logger;
import org.moreunit.core.matching.SourceFolderPath;

public class WizardFactory
{
    private final IWorkbench workbench;
    private final Shell activeShell;
    private final Logger logger;

    public WizardFactory(IWorkbench workbench, Shell activeShell, Logger logger)
    {
        this.workbench = workbench;
        this.activeShell = activeShell;
        this.logger = logger;
    }

    public WizardDialog<NewFileWizard> createNewFileWizard(SourceFolderPath selectedFolder, String fileName)
    {
        NewFileWizard wizard = new NewFileWizard(workbench, selectedFolder, fileName, logger);
        return new WizardDialog<NewFileWizard>(createWizardDialog(wizard), wizard);
    }

    protected org.eclipse.jface.wizard.WizardDialog createWizardDialog(IWizard wizard)
    {
        return new org.eclipse.jface.wizard.WizardDialog(activeShell, wizard);
    }

    protected final Shell getActiveShell()
    {
        return activeShell;
    }
}
