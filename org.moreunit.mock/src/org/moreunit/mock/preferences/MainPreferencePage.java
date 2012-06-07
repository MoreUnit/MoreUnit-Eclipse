package org.moreunit.mock.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.moreunit.mock.MoreUnitMockPlugin;
import org.moreunit.mock.templates.MockingTemplateLoader;

import com.google.inject.Inject;

public class MainPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    private final TemplateStyleSelector templateStyleSelector;
    private final MockingTemplateLoader templateLoader;

    @Inject
    public MainPreferencePage(TemplateStyleSelector templateStyleSelector, MockingTemplateLoader templateLoader)
    {
        this.templateStyleSelector = templateStyleSelector;
        this.templateLoader = templateLoader;
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
        contentComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        templateStyleSelector.createContents(contentComposite, null);

        placeHolder(contentComposite);

        Label lbl = new Label(contentComposite, SWT.NONE);
        lbl.setText("You may add custom templates by placing them in the following folder:");

        lbl = new Label(contentComposite, SWT.NONE);
        lbl.setText(templateLoader.getTemplatesLocation());
        GridData data = new GridData();
        data.horizontalIndent = 15;
        lbl.setLayoutData(data);

        placeHolder(contentComposite);

        lbl = new Label(contentComposite, SWT.NONE);
        lbl.setText("Please refer to MoreUnit's documentation for more information.");

        placeHolder(contentComposite);

        Button reloadTemplatesBtn = new Button(contentComposite, SWT.NONE);
        reloadTemplatesBtn.setText("Reload templates");
        reloadTemplatesBtn.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                templateLoader.loadTemplates();
                templateStyleSelector.reloadTemplates();
            }
        });

        return parent;
    }

    private void placeHolder(Composite parent)
    {
        new Label(parent, SWT.NONE);
    }

    @Override
    public boolean performOk()
    {
        templateStyleSelector.savePreferences();
        return super.performOk();
    }
}
