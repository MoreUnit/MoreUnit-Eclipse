package org.moreunit.core.ui;

import static org.moreunit.core.config.CoreModule.$;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.moreunit.core.matching.SourceFolderPath;

public class WizardFactory
{
    private final IWorkbench workbench;
    private final Shell activeShell;

    public WizardFactory(IWorkbench workbench, Shell activeShell)
    {
        this.workbench = workbench;
        this.activeShell = activeShell;
    }

    public WizardDialog<NewFileWizard> createNewFileWizard(SourceFolderPath selectedFolder, String fileName)
    {
        NewFileWizard wizard = new NewFileWizard(workbench, $().getWorkspace(), selectedFolder, fileName);
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
