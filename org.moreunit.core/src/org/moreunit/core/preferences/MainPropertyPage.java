package org.moreunit.core.preferences;

import static org.moreunit.core.ui.Labels.wrappingLabel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.moreunit.core.ui.Composites;

public class MainPropertyPage extends PropertyPage implements IWorkbenchPreferencePage
{
    private static final int WIDTH_HINT = 300;

    public MainPropertyPage()
    {
        noDefaultAndApplyButton();
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite composite = Composites.fillBothNoMargin(parent);

        wrappingLabel("Please use this page's children to let MoreUnit know how your tests are organized.", WIDTH_HINT, composite);

        Dialog.applyDialogFont(composite);

        return composite;
    }

    public void init(IWorkbench wb)
    {
    }
}
