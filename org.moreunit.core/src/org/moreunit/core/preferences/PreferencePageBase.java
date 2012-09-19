package org.moreunit.core.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

abstract class PreferencePageBase extends PreferencePage implements IWorkbenchPreferencePage
{
    private final GenericConfigurationPage delegate;

    public PreferencePageBase(LanguagePreferencesWriter prefWriter)
    {
        this(null, prefWriter);
    }

    public PreferencePageBase(String title, LanguagePreferencesWriter prefWriter)
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
    protected final Control createContents(Composite parent)
    {
        initializeDialogUnits(parent);

        delegate.createContainer(parent);

        doCreateContent(delegate.getBody());

        Dialog.applyDialogFont(parent);

        return parent;
    }

    protected abstract void doCreateContent(Composite contentComposite);

    protected void createBaseContents()
    {
        delegate.createContents();
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
