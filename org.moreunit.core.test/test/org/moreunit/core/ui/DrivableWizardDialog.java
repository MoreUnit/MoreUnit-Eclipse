package org.moreunit.core.ui;

import java.lang.reflect.Method;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class DrivableWizardDialog extends WizardDialog
{
    private WizardDriver driver;

    public DrivableWizardDialog(Shell parentShell, IWizard newWizard, WizardDriver driver)
    {
        super(parentShell, newWizard);
        this.driver = driver;
    }

    @Override
    public int open()
    {
        driver.configure(this);
        setBlockOnOpen(false);
        super.open();
        int result = driver.onOpen(this);
        doClose();
        return result;
    }

    /**
     * Closes the dialog without calling getWizard().performCancel().
     * <p>
     * Note: if WizardDriver's implementation ever changes, an alternative would
     * be to proxy the wizard to prevent performCancel from being called when
     * undesired...
     * </p>
     */
    private void doClose()
    {
        try
        {
            Method m = WizardDialog.class.getDeclaredMethod("hardClose");
            m.setAccessible(true);
            m.invoke(this);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // increases getWizard()'s visibility
    @Override
    public IWizard getWizard()
    {
        return super.getWizard();
    }
}
