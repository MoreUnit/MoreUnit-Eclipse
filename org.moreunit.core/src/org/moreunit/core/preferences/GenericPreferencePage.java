package org.moreunit.core.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.moreunit.core.languages.Language;
import org.moreunit.core.languages.LanguageRepository;
import org.moreunit.core.ui.Labels;

public class GenericPreferencePage extends PreferencePageBase
{
    private final Language lang;
    private final LanguageRepository languageRepository;

    public GenericPreferencePage(Language lang, LanguagePreferencesWriter prefWriter, LanguageRepository repo)
    {
        super(lang.getLabel(), prefWriter);
        this.lang = lang;
        languageRepository = repo;
    }

    @Override
    protected void doCreateContent(Composite parent)
    {
        createFields(parent);
        createBaseContents();
    }

    private void createFields(Composite parent)
    {
        Button button = new Button(parent, SWT.NONE);
        button.setText("Delete Configuration");
        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if(MessageDialog.openConfirm(getShell(), "MoreUnit", "This action is definitive. Please confirm that you want to delete the configuration for " + lang.getLabel() + "."))
                {
                    languageRepository.remove(lang);
                }
            }
        });
    }
}
