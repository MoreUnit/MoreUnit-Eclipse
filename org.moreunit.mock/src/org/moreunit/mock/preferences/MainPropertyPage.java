package org.moreunit.mock.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;
import org.moreunit.core.log.Logger;

import static org.moreunit.mock.config.MockModule.$;

public class MainPropertyPage extends PropertyPage
{
    private final Preferences preferences;
    private final TemplateStyleSelector templateStyleSelector;
    private final Logger logger;

    private Button specificSettingsCheckbox;

    public MainPropertyPage()
    {
        this($().getPreferences(), $().getTemplateStyleSelector(), $().getLogger());
    }

    public MainPropertyPage(Preferences preferences, TemplateStyleSelector templateStyleSelector, Logger logger)
    {
        this.preferences = preferences;
        this.templateStyleSelector = templateStyleSelector;
        this.logger = logger;
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite contentComposite = new Composite(parent, SWT.NONE);
        contentComposite.setLayout(new GridLayout(1, true));
        contentComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createCheckboxContent(contentComposite);
        templateStyleSelector.createContents(contentComposite, project());

        initValues();

        return parent;
    }

    private void createCheckboxContent(Composite parent)
    {
        specificSettingsCheckbox = new Button(parent, SWT.CHECK);
        specificSettingsCheckbox.setText("Use project specific settings");
        specificSettingsCheckbox.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        specificSettingsCheckbox.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
                // nothing to do
            }

            public void widgetSelected(SelectionEvent e)
            {
                specificSettingsChecboxChanged();
            }
        });
    }

    private void specificSettingsChecboxChanged()
    {
        templateStyleSelector.setEnabled(specificSettingsChecked());
    }

    void initValues()
    {
        checkSpecificSettingsCheckbox(preferences.hasSpecificSettings(project()));
    }

    protected void checkSpecificSettingsCheckbox(boolean checked)
    {
        specificSettingsCheckbox.setSelection(checked);
        specificSettingsChecboxChanged();
    }

    protected boolean specificSettingsChecked()
    {
        return specificSettingsCheckbox.getSelection();
    }

    private IJavaProject project()
    {
        if(getElement() instanceof IJavaProject)
        {
            return (IJavaProject) getElement();
        }
        return JavaCore.create((IProject) getElement());
    }

    @Override
    public boolean performOk()
    {
        IJavaProject project = project();
        boolean hadSpecificSettings = preferences.hasSpecificSettings(project);
        if(hadSpecificSettings != specificSettingsChecked())
        {
            preferences.setSpecificSettings(project, specificSettingsChecked());

            if(logger.debugEnabled())
            {
                if(! hadSpecificSettings && specificSettingsChecked())
                {
                    logger.debug("Defined specific settings for project " + project.getElementName());
                }
                else if(hadSpecificSettings && ! specificSettingsChecked())
                {
                    logger.debug("Disabled specific settings for project " + project.getElementName());
                }
            }
        }

        if(specificSettingsChecked())
        {
            templateStyleSelector.savePreferences();
        }

        return super.performOk();
    }
}
