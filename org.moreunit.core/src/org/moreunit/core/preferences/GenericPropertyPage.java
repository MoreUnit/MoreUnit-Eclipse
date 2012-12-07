package org.moreunit.core.preferences;

import static org.moreunit.core.config.CoreModule.$;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

public class GenericPropertyPage extends PropertyPage
{
    private final String languageId;
    private final Preferences wsPreferences;
    private LanguagePreferencesWriter prefs;
    private Button projectSpecificSettingsCheckbox;
    private GenericConfigurationPage delegate;

    public GenericPropertyPage(String languageId, String description)
    {
        this.languageId = languageId;
        wsPreferences = $().getPreferences();
        if(description != null)
        {
            setDescription(description);
        }
    }

    @Override
    protected Control createContents(Composite parent)
    {
        IProject project = (IProject) getElement().getAdapter(IProject.class);
        if(project == null)
        {
            return parent;
        }

        prefs = wsPreferences.get(project).writerForLanguage(languageId);

        delegate = new GenericConfigurationPage(this, prefs);

        initializeDialogUnits(parent);

        delegate.createContainer(parent);

        createCheckboxContent(delegate.getBody());

        delegate.createContents();

        Dialog.applyDialogFont(parent);

        if(prefs.isActive())
        {
            delegate.validate();
        }
        else
        {
            delegate.setEnabled(false);
        }

        return parent;
    }

    private void createCheckboxContent(Composite parent)
    {
        projectSpecificSettingsCheckbox = new Button(parent, SWT.CHECK);
        projectSpecificSettingsCheckbox.setText("Use project specific settings");

        projectSpecificSettingsCheckbox.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                boolean checked = projectSpecificSettingsCheckbox.getSelection();
                prefs.setActive(checked);
                delegate.setEnabled(checked);

                if(checked)
                {
                    delegate.validate();
                }
            }
        });

        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        gridData.horizontalSpan = 2;
        projectSpecificSettingsCheckbox.setLayoutData(gridData);

        projectSpecificSettingsCheckbox.setSelection(prefs.isActive());
    }

    @Override
    public boolean performOk()
    {
        if(! delegate.performOk())
        {
            return false;
        }
        return super.performOk();
    }

    @Override
    protected void performDefaults()
    {
        super.performDefaults();
        delegate.performDefaults();
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
    }
}
