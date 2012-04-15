package org.moreunit.core.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.moreunit.core.MoreUnitCore;

public class GenericPreferencePage extends PreferencePageBase
{
    private final Language lang;
    private final Preferences preferences;
    private final PageManager pageManager;

    public GenericPreferencePage(Language lang, LanguagePreferencesWriter prefWriter)
    {
        super(lang.getLabel(), prefWriter);
        this.lang = lang;
        preferences = MoreUnitCore.get().getPreferences();
        pageManager = MoreUnitCore.get().getPageManager();
    }

    @Override
    protected void doCreateContent(Composite contentComposite)
    {
        createBaseContents(contentComposite);
        createFields(contentComposite);
    }

    private void createFields(Composite composite)
    {
        // place holder
        new Label(composite, SWT.NONE);

        Button button = new Button(composite, SWT.NONE);
        button.setText("Delete Configuration");
        button.addSelectionListener(new SelectionListener()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if(MessageDialog.openConfirm(getShell(), "MoreUnit", "This action is definitive. Please confirm that you want to delete the configuration for " + lang.getLabel() + "."))
                {
                    preferences.removeConfiguredLanguage(lang);
                    pageManager.removePagesFor(lang);
                }
            }

            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        });
    }
}
