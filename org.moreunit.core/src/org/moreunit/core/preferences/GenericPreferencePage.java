package org.moreunit.core.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class GenericPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    private final GenericConfigurationPage delegate;

    public GenericPreferencePage(LanguagePreferencesWriter prefWriter)
    {
        this(null, prefWriter);
    }

    public GenericPreferencePage(String title, LanguagePreferencesWriter prefWriter)
    {
        if(title != null)
        {
            setTitle(title);
        }
        delegate = new GenericConfigurationPage(this, prefWriter);
    }

    public void init(IWorkbench workbench)
    {
    }

    @Override
    protected Control createContents(Composite parent)
    {
        initializeDialogUnits(parent);

        delegate.createContents(parent);

        return parent;
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
        if(! visible)
        {
            return;
        }

        delegate.validate();
    }
}
