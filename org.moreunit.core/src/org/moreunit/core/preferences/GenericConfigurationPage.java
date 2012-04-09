package org.moreunit.core.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.moreunit.core.matching.TestFileNamePattern;

class GenericConfigurationPage
{
    private final PreferencePage page;
    private final LanguagePreferencesWriter prefWriter;
    private Text testFileTemplateField;
    private Text wordSeparatorField;

    public GenericConfigurationPage(PreferencePage page, LanguagePreferencesWriter prefWriter)
    {
        this.page = page;
        this.prefWriter = prefWriter;
    }

    public Control createContents(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginRight = 10;
        layout.numColumns = 2;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createFields(composite);

        Dialog.applyDialogFont(composite);

        return parent;
    }

    private void createFields(Composite parent)
    {
        GridData labelAndFieldLayout = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        labelAndFieldLayout.horizontalIndent = 30;

        Label testFileTemplateLabel = new Label(parent, SWT.NONE);
        testFileTemplateLabel.setText("Rule for naming test files:");

        testFileTemplateField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        testFileTemplateField.setLayoutData(labelAndFieldLayout);

        if(prefWriter.getTestFileNameTemplate().length() != 0)
        {
            testFileTemplateField.setText(prefWriter.getTestFileNameTemplate());
        }
        else
        {
            testFileTemplateField.setText(Preferences.DEFAULTS.getTestFileNameTemplate());
        }

        testFileTemplateField.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent event)
            {
                validate();
            }
        });

        Label wordSeparatorLabel = new Label(parent, SWT.NONE);
        wordSeparatorLabel.setText("Word separator:");

        wordSeparatorField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        wordSeparatorField.setLayoutData(labelAndFieldLayout);

        if(prefWriter.getFileWordSeparator().length() != 0)
        {
            wordSeparatorField.setText(prefWriter.getFileWordSeparator());
        }
        else
        {
            wordSeparatorField.setText(Preferences.DEFAULTS.getFileWordSeparator());
        }

        GridData rowLayout = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        rowLayout.horizontalSpan = 2;

        Label explainationLabel = new Label(parent, SWT.NONE);
        explainationLabel.setLayoutData(rowLayout);
        explainationLabel.setText("Use the variable " + TestFileNamePattern.SRC_FILE_VARIABLE + " to represent the production source file.");
    }

    public boolean performOk()
    {
        saveProperties();
        return true;
    }

    public void performDefaults()
    {
        wordSeparatorField.setText(Preferences.DEFAULTS.getFileWordSeparator());
        testFileTemplateField.setText(Preferences.DEFAULTS.getTestFileNameTemplate());
    }

    public void validate()
    {
        String testFileTemplate = testFileTemplateField.getText();

        String errorMsg = null;
        if(testFileTemplate.isEmpty())
        {
            errorMsg = "You must enter a rule for naming test files";
        }
        else if(! testFileTemplate.contains(TestFileNamePattern.SRC_FILE_VARIABLE))
        {
            errorMsg = "The rule for naming test files must use the variable " + TestFileNamePattern.SRC_FILE_VARIABLE;
        }
        else if(testFileTemplate.length() == TestFileNamePattern.SRC_FILE_VARIABLE.length())
        {
            errorMsg = "Test files must have a name different from their corresponding source file";
        }

        if(errorMsg == null)
        {
            page.setMessage(null);
            page.setValid(true);
        }
        else
        {
            page.setMessage(errorMsg, IMessageProvider.ERROR);
            page.setValid(false);
            testFileTemplateField.forceFocus();
        }
    }

    private void saveProperties()
    {
        prefWriter.setFileWordSeparator(wordSeparatorField.getText());
        prefWriter.setTestFileNameTemplate(testFileTemplateField.getText());
        prefWriter.save();
    }

    public void setEnabled(boolean enabled)
    {
        wordSeparatorField.setEnabled(enabled);
        testFileTemplateField.setEnabled(enabled);
    }
}
