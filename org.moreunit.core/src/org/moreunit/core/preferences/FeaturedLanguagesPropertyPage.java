package org.moreunit.core.preferences;

import static org.moreunit.core.ui.Labels.placeHolder;
import static org.moreunit.core.ui.Labels.wrappingLabel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.moreunit.core.ui.Composites;

public class FeaturedLanguagesPropertyPage extends PropertyPage implements IWorkbenchPreferencePage
{
    private static final int WIDTH_HINT = 350;

    public FeaturedLanguagesPropertyPage()
    {
        noDefaultAndApplyButton();
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite composite = Composites.fillBothNoMargin(parent);

        wrappingLabel("This section regroups settings for languages for which MoreUnit provides specific support.", WIDTH_HINT, composite);
        placeHolder(composite);
        wrappingLabel("Such support is obtained by installing ad-hoc plug-ins. For instance, MoreUnit's repository proposes a plug-in to support Java projects.", WIDTH_HINT, composite);

        Dialog.applyDialogFont(composite);

        return composite;
    }

    public void init(IWorkbench wb)
    {
    }
}
