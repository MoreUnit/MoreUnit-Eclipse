package org.moreunit.mock.preferences;

import java.net.URL;
import java.util.Map.Entry;

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
import org.moreunit.core.ui.DialogFactory;
import org.moreunit.mock.MoreUnitMockPlugin;
import org.moreunit.mock.templates.LoadingResult;
import org.moreunit.mock.templates.MockingTemplateLoader;

import static org.moreunit.core.util.StringConstants.NEWLINE;
import static org.moreunit.mock.config.MockModule.$;

public class MainPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    private final TemplateStyleSelector templateStyleSelector;
    private final MockingTemplateLoader templateLoader;

    public MainPreferencePage()
    {
        this($().getTemplateStyleSelector(), $().getTemplateLoader());
    }

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
        lbl.setText(templateLoader.getWorkspaceTemplatesLocation());
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
                LoadingResult templateLoadingResult = templateLoader.loadTemplates();
                templateStyleSelector.reloadTemplates();
                informUserAboutInvalidTemplates(templateLoadingResult);
            }
        });

        return parent;
    }

    private void placeHolder(Composite parent)
    {
        new Label(parent, SWT.NONE);
    }

    private void informUserAboutInvalidTemplates(LoadingResult templateLoadingResult)
    {
        if(! templateLoadingResult.invalidTemplatesFound())
        {
            return;
        }

        StringBuilder errBuilder = new StringBuilder("The following templates could not be loaded:");

        for (Entry<URL, String> urlAndReason : templateLoadingResult.invalidTemplates().entrySet())
        {
            errBuilder.append(NEWLINE)
                    .append(NEWLINE).append("Template: ")
                    .append(NEWLINE).append(urlAndReason.getKey())
                    .append(NEWLINE).append("Reason: ")
                    .append(NEWLINE).append(urlAndReason.getValue());
        }

        new DialogFactory(getShell()).createErrorDialog(errBuilder.toString()).open();
    }

    @Override
    public boolean performOk()
    {
        templateStyleSelector.savePreferences();
        return super.performOk();
    }
}
