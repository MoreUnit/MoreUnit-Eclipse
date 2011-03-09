package org.moreunit.mock.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.moreunit.mock.MoreUnitMockPlugin;

import com.google.inject.Inject;

public class MainPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    private final TemplateStyleSelector templateStyleSelector;

    @Inject
    public MainPreferencePage(TemplateStyleSelector templateStyleSelector)
    {
        this.templateStyleSelector = templateStyleSelector;
    }

    public void init(IWorkbench workbench)
    {
        setPreferenceStore(MoreUnitMockPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite contentComposite = new Composite(parent, SWT.NONE);
        contentComposite.setLayout(new GridLayout(1, true));

        templateStyleSelector.createContents(contentComposite, null);

        return parent;
    }

    @Override
    public boolean performOk()
    {
        templateStyleSelector.savePreferences();
        return super.performOk();
    }
}
