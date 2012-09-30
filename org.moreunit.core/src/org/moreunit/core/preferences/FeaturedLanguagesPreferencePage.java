package org.moreunit.core.preferences;

import static org.moreunit.core.ui.Composites.placeHolder;
import static org.moreunit.core.ui.Labels.wrappingLabel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.moreunit.core.ui.Composites;

public class FeaturedLanguagesPreferencePage extends PropertyPage implements IWorkbenchPreferencePage
{
    protected static final int WIDTH_HINT = 350;

    public FeaturedLanguagesPreferencePage()
    {
        noDefaultAndApplyButton();
    }

    @Override
    protected final Control createContents(Composite parent)
    {
        Composite composite = Composites.fillWidth(parent);

        wrappingLabel("Welcome! These preferences pages allow you to tell MoreUnit how your tests are organized.", WIDTH_HINT, composite);
        placeHolder(composite);
        wrappingLabel("This page's direct children relate to programming languages for which MoreUnit provides specific support.", WIDTH_HINT, composite);
        wrappingLabel("Such support is obtained by installing ad-hoc plug-ins. For instance, MoreUnit's repository proposes a plug-in to support Java and Groovy projects.", WIDTH_HINT, composite);
        placeHolder(composite);
        wrappingLabel("Additionally, you can setup a basic support for any other language you like, within the section: \"User Languages\".", WIDTH_HINT, composite);
        createLastPart(composite);

        Dialog.applyDialogFont(composite);

        return composite;
    }

    protected void createLastPart(Composite composite)
    {
        wrappingLabel("The main page of the section allows you to target all possible languages at once. Using the form at the bottom of the page, you may also create \"per-language\" settings.", WIDTH_HINT, composite);
        placeHolder(composite);
        wrappingLabel("All settings may be overridden at the project level.", WIDTH_HINT, composite);
    }

    public final void init(IWorkbench wb)
    {
    }
}
