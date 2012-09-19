package org.moreunit.core.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.moreunit.core.MoreUnitCore;
import org.moreunit.core.languages.Language;
import org.moreunit.core.languages.LanguageRepository;
import org.moreunit.core.ui.Composites;
import org.moreunit.core.ui.Labels;
import org.moreunit.core.ui.LayoutData;

public class OtherLanguagesPreferencePage extends PreferencePageBase
{
    private final LanguageRepository languageRepository;
    private Text nameField;
    private ExtensionField extensionField;

    public OtherLanguagesPreferencePage()
    {
        super(MoreUnitCore.get().getPreferences().writerForAnyLanguage());
        languageRepository = MoreUnitCore.get().getLanguageRepository();
    }

    @Override
    protected void doCreateContent(Composite parent)
    {
        Label explainationLabel = new Label(parent, SWT.NONE);
        explainationLabel.setLayoutData(LayoutData.row(1));
        explainationLabel.setText("The following configuration will be applied to all languages as a default:");

        createBaseContents();

        Labels.placeHolder(parent);

        createFields(parent);
    }

    private void createFields(Composite parent)
    {
        Composite group = Composites.gridGroup(parent, "Per-language configurations may also be created:", 2);

        Label nameLabel = new Label(group, SWT.NONE);
        nameLabel.setText("Language name:");

        nameField = new Text(group, SWT.SINGLE | SWT.BORDER);
        nameField.setLayoutData(LayoutData.labelledField());

        Label extensionLabel = new Label(group, SWT.NONE);
        extensionLabel.setText("Extension:");

        extensionField = new ExtensionField(group, SWT.SINGLE | SWT.BORDER);
        extensionField.setLayoutData(LayoutData.labelledField());

        Button creationButton = new Button(group, SWT.NONE);
        creationButton.setText("Create Configuration");
        creationButton.addSelectionListener(new SelectionAdapter()
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
        });

        Labels.placeHolder(group);
    }
}
