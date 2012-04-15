package org.moreunit.core.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

        Composite contentComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginRight = 10;
        layout.numColumns = 2;
        contentComposite.setLayout(layout);
        contentComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        doCreateContent(contentComposite);

        Dialog.applyDialogFont(contentComposite);

        return parent;
    }

    protected abstract void doCreateContent(Composite contentComposite);

    protected void createBaseContents(Composite parent)
    {
        delegate.createContents(parent);
    }

    protected final void placeHolder(Composite parent)
    {
        delegate.placeHolder(parent);
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
