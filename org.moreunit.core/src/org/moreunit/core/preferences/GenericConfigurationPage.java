package org.moreunit.core.preferences;

import static org.moreunit.core.util.Strings.countOccurrences;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
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

    public void createContents(Composite parent)
    {
        createFileTemplateField(parent);

        createWordSeparatorField(parent);

        placeHolder(parent);

        createExplanations(parent);

        placeHolder(parent);

        createOverviewArea(parent);
    }

    private void createFileTemplateField(Composite parent)
    {
        Label testFileTemplateLabel = new Label(parent, SWT.NONE);
        testFileTemplateLabel.setText("Rule for naming test files:");

        testFileTemplateField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        testFileTemplateField.setLayoutData(LayoutData.LABEL_AND_FIELD);

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
    }

    private void createWordSeparatorField(Composite parent)
    {
        Label wordSeparatorLabel = new Label(parent, SWT.NONE);
        wordSeparatorLabel.setText("Word separator:");

        wordSeparatorField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        wordSeparatorField.setLayoutData(LayoutData.LABEL_AND_FIELD);

        if(prefWriter.getFileWordSeparator().length() != 0)
        {
            wordSeparatorField.setText(prefWriter.getFileWordSeparator());
        }
        else
        {
            wordSeparatorField.setText(Preferences.DEFAULTS.getFileWordSeparator());
        }
    }

    public void placeHolder(Composite parent)
    {
        new Label(parent, SWT.NONE);
    }

    private void createExplanations(Composite parent)
    {
        String[] explanations = { //
        "Use the variable " + TestFileNamePattern.SRC_FILE_VARIABLE + " to represent the production source file.", //
        "You may use stars (*) to represent variable parts.", //
        "You may use parentheses and pipes to define several possible prefixes or suffixes: (pre1|pre2)" };

        for (String e : explanations)
        {
            Label lbl = new Label(parent, SWT.NONE);
            lbl.setLayoutData(LayoutData.ROW);
            lbl.setText(e);
        }
    }

    private void createOverviewArea(Composite parent)
    {
        // TODO Nicolas
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
        String separator = wordSeparatorField.getText();

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
        else if(! TestFileNamePattern.isValid(testFileTemplate, separator))
        {
            errorMsg = "Invalid template: please follow the guidelines.";
        }

        if(errorMsg == null)
        {
            setValid();

            if(countOccurrences(testFileTemplate, "*") > 1)
            {
                page.setMessage("Using too many wildcards may degrade search performance and results!", IMessageProvider.WARNING);
            }
        }
        else
        {
            page.setMessage(errorMsg, IMessageProvider.ERROR);
            page.setValid(false);
            testFileTemplateField.forceFocus();
        }
    }

    private void setValid()
    {
        page.setMessage(null);
        page.setValid(true);
    }

    private void saveProperties()
    {
        prefWriter.setTestFileNameTemplate(testFileTemplateField.getText(), wordSeparatorField.getText());
        prefWriter.save();
    }

    public void setEnabled(boolean enabled)
    {
        wordSeparatorField.setEnabled(enabled);
        testFileTemplateField.setEnabled(enabled);
        if(! enabled)
        {
            setValid();
        }
    }
}
