package org.moreunit.core;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.moreunit.core.config.CoreModule;
import org.moreunit.core.matching.FileMatchSelector;
import org.moreunit.core.resources.Workspace;
import org.moreunit.core.ui.DialogFactory;
import org.moreunit.core.ui.DrivableWizardFactory;
import org.moreunit.core.ui.WizardDriver;
import org.moreunit.core.ui.WizardFactory;
import org.moreunit.test.config.ConfigItem;

public class CoreTestModule extends CoreModule
{
    public ConfigItem<DialogFactory> dialogFactory = ConfigItem.useDefault();
    public ConfigItem<FileMatchSelector> fileMatchSelector = ConfigItem.useDefault();
    public ConfigItem<Workspace> workspace = ConfigItem.useDefault();
    public WizardDriver wizardDriver;

    public CoreTestModule()
    {
        super(true);
    }

    @Override
    public DialogFactory getDialogFactory(Shell activeShell)
    {
        if(dialogFactory.isOverridden())
        {
            return dialogFactory.get();
        }
        return super.getDialogFactory(activeShell);
    }

    @Override
    public FileMatchSelector getFileMatchSelector()
    {
        if(fileMatchSelector.isOverridden())
        {
            return fileMatchSelector.get();
        }
        return super.getFileMatchSelector();
    }

    @Override
    public WizardFactory getWizardFactory(IWorkbench workbench, Shell activeShell)
    {
        return new DrivableWizardFactory(workbench, activeShell, wizardDriver);
    }

    @Override
    public Workspace getWorkspace()
    {
        if(workspace.isOverridden())
        {
            return workspace.get();
        }
        return super.getWorkspace();
    }
}
