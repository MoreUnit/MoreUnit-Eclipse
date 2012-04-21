package org.moreunit.core.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.moreunit.core.MoreUnitCore;
import org.moreunit.core.languages.Language;
import org.moreunit.core.languages.LanguageRepository;

public class MainPreferencePage extends PreferencePageBase
{
    private final LanguageRepository languageRepository;
    private Text nameField;
    private ExtensionField extensionField;

    public MainPreferencePage()
    {
        super(MoreUnitCore.get().getPreferences().writerForAnyLanguage());
        languageRepository = MoreUnitCore.get().getLanguageRepository();
    }

    @Override
    protected void doCreateContent(Composite contentComposite)
    {
        Label explainationLabel = new Label(contentComposite, SWT.NONE);
        explainationLabel.setLayoutData(LayoutData.ROW);
        explainationLabel.setText("The following configuration will be applied to all languages as a default:");

        createBaseContents(contentComposite);

        createFields(contentComposite);
    }

    private void createFields(Composite parent)
    {
        Label explainationLabel = new Label(parent, SWT.NONE);
        explainationLabel.setLayoutData(LayoutData.ROW);
        explainationLabel.setText("Per-language configurations may also be created:");

        Label nameLabel = new Label(parent, SWT.NONE);
        nameLabel.setText("Language name:");

        nameField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        nameField.setLayoutData(LayoutData.LABEL_AND_FIELD);

        Label extensionLabel = new Label(parent, SWT.NONE);
        extensionLabel.setText("Extension:");

        extensionField = new ExtensionField(parent, SWT.SINGLE | SWT.BORDER);
        extensionField.setLayoutData(LayoutData.LABEL_AND_FIELD);

        placeHolder(parent);

        Button creationButton = new Button(parent, SWT.NONE);
        creationButton.setText("Create Configuration");
        creationButton.addSelectionListener(new SelectionListener()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if(! extensionField.isValid())
                {
                    MessageDialog.openWarning(getShell(), "MoreUnit", "Please enter a valid file extension and retry.");
                    return;
                }

                if(languageRepository.contains(extensionField.getExtension()))
                {
                    MessageDialog.openWarning(getShell(), "MoreUnit", "A configuration already exists for file extension *." + extensionField.getExtension());
                    return;
                }

                Language lang = new Language(extensionField.getExtension(), nameField.getText().trim());
                languageRepository.add(lang);
            }

            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        });
    }
}
