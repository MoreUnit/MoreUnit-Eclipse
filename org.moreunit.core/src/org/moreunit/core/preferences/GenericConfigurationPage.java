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
import org.moreunit.core.matching.TestFolderPathPattern;

class GenericConfigurationPage
{
    private final PreferencePage page;
    private final LanguagePreferencesWriter prefWriter;
    private Text srcFolderTemplateField;
    private Text testFolderTemplateField;
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
        createFileTplExplanations(parent);

        placeHolder(parent);
        placeHolder(parent);

        createFolderTemplateFields(parent);
        createFolderTplExplanations(parent);

        placeHolder(parent);
        placeHolder(parent);

        createOverviewArea(parent);
    }

    private void createFileTemplateField(Composite parent)
    {
        Label label = new Label(parent, SWT.NONE);
        label.setText("Rule for naming test files:");

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

        addValidationOnModification(testFileTemplateField);
    }

    private void addValidationOnModification(Text field)
    {
        field.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent event)
            {
                validate();
            }
        });
    }

    private void createWordSeparatorField(Composite parent)
    {
        Label label = new Label(parent, SWT.NONE);
        label.setText("Word separator:");

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

    private void createFolderTemplateFields(Composite parent)
    {
        Label label = new Label(parent, SWT.NONE);
        label.setText("Rule for locating test files:");

        srcFolderTemplateField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        srcFolderTemplateField.setLayoutData(LayoutData.LABEL_AND_FIELD);

        if(prefWriter.getSrcFolderPathTemplate().length() != 0)
        {
            srcFolderTemplateField.setText(prefWriter.getSrcFolderPathTemplate());
        }
        else
        {
            srcFolderTemplateField.setText(Preferences.DEFAULTS.getSrcFolderPathTemplate());
        }

        addValidationOnModification(srcFolderTemplateField);

        placeHolder(parent);

        testFolderTemplateField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        testFolderTemplateField.setLayoutData(LayoutData.LABEL_AND_FIELD);

        if(prefWriter.getTestFolderPathTemplate().length() != 0)
        {
            testFolderTemplateField.setText(prefWriter.getTestFolderPathTemplate());
        }
        else
        {
            testFolderTemplateField.setText(Preferences.DEFAULTS.getTestFolderPathTemplate());
        }

        addValidationOnModification(testFolderTemplateField);
    }

    private void createFolderTplExplanations(Composite parent)
    {
        String[] explanations = { //
        "Use the variable " + TestFolderPathPattern.SRC_PROJECT_VARIABLE + " to represent the production source project.", //
        "You may use stars '*' to represent variable parts within segments, and double stars '**' for a variable set of segments.", //
        "You may capture variable parts using parentheses and then reference them using backslashes '\\'.", //
        "When matching files, the part of the file path that lies after then end of your pattern is automatically used.", //
        "Examples: the definition '" + TestFolderPathPattern.SRC_PROJECT_VARIABLE + "'/(*)-src' => '" + TestFolderPathPattern.SRC_PROJECT_VARIABLE + "'/\\1-test'", //
        "\tallows for finding the file 'my-project/js-test/some/path/to/MyClassTest.js'", //
        "\tfrom 'my-project/js-src/some/path/to/MyClass.js'" };

        for (String e : explanations)
        {
            Label lbl = new Label(parent, SWT.NONE);
            lbl.setLayoutData(LayoutData.ROW);
            lbl.setText(e);
        }
    }

    public void placeHolder(Composite parent)
    {
        new Label(parent, SWT.NONE);
    }

    private void createFileTplExplanations(Composite parent)
    {
        String[] explanations = { //
        "Use the variable " + TestFileNamePattern.SRC_FILE_VARIABLE + " to represent the production source file.", //
        "You may use stars '*' to represent variable parts.", //
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
        srcFolderTemplateField.setText(Preferences.DEFAULTS.getSrcFolderPathTemplate());
        testFileTemplateField.setText(Preferences.DEFAULTS.getTestFileNameTemplate());
        testFolderTemplateField.setText(Preferences.DEFAULTS.getTestFolderPathTemplate());
        wordSeparatorField.setText(Preferences.DEFAULTS.getFileWordSeparator());
    }

    public void validate()
    {
        String errorMsg = validateTestFileTemplate();
        if(errorMsg != null)
        {
            testFileTemplateField.forceFocus();
        }
        else
        {
            errorMsg = validateTestFolderTemplate();
        }

        if(errorMsg == null)
        {
            setValid();

            if(countOccurrences(testFileTemplateField.getText(), "*") > 1)
            {
                page.setMessage("Using too many wildcards may degrade search performance and results!", IMessageProvider.WARNING);
            }
        }
        else
        {
            page.setMessage(errorMsg, IMessageProvider.ERROR);
            page.setValid(false);
        }
    }

    private String validateTestFileTemplate()
    {
        String testFileTemplate = testFileTemplateField.getText().trim();
        String separator = wordSeparatorField.getText().trim();

        String errorMsg = null;
        if(testFileTemplate.length() == 0)
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
            errorMsg = "Invalid file template: please follow the guidelines.";
        }
        return errorMsg;
    }

    private String validateTestFolderTemplate()
    {
        String srcFolderTpl = srcFolderTemplateField.getText().trim();
        String tstFolderTpl = testFolderTemplateField.getText().trim();

        String errorMsg = null;
        if(srcFolderTpl.length() == 0 || tstFolderTpl.length() == 0)
        {
            errorMsg = "You must enter a rule for locating test files";
        }
        else if(! srcFolderTpl.contains(TestFolderPathPattern.SRC_PROJECT_VARIABLE) || ! tstFolderTpl.contains(TestFolderPathPattern.SRC_PROJECT_VARIABLE))
        {
            errorMsg = "The rule for locating test files must use the variable " + TestFolderPathPattern.SRC_PROJECT_VARIABLE;
        }
        else if(! TestFolderPathPattern.isValid(srcFolderTpl, tstFolderTpl))
        {
            errorMsg = "Invalid folder templates: please follow the guidelines.";
        }
        return errorMsg;
    }

    private void setValid()
    {
        page.setMessage(null);
        page.setValid(true);
    }

    private void saveProperties()
    {
        prefWriter.setTestFileNameTemplate(testFileTemplateField.getText().trim(), wordSeparatorField.getText().trim());
        prefWriter.setTestFolderPathTemplate(srcFolderTemplateField.getText().trim(), testFolderTemplateField.getText().trim());
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
