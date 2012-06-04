package org.moreunit.core.preferences;

import static org.moreunit.core.util.Strings.countOccurrences;

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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.moreunit.core.matching.TestFileNamePattern;
import org.moreunit.core.matching.TestFolderPathPattern;
import org.moreunit.core.ui.ExpandableCompositeContainer;
import org.moreunit.core.ui.ExpandableCompositeContainer.CompositeBody;
import org.moreunit.core.ui.FileNamePatternDemo;

class GenericConfigurationPage
{
    private final PreferencePage page;
    private final LanguagePreferencesWriter prefWriter;
    private ExpandableCompositeContainer container;
    private Text srcFolderTemplateField;
    private Text testFolderTemplateField;
    private Text testFileTemplateField;
    private Text wordSeparatorField;

    public GenericConfigurationPage(PreferencePage page, LanguagePreferencesWriter prefWriter)
    {
        this.page = page;
        this.prefWriter = prefWriter;
    }

    public void createContainer(final Composite parent)
    {
        container = new ExpandableCompositeContainer(parent, 2);

        container.getBodyLayout().marginRight = 10;
    }

    public ExpandableCompositeContainer getContainer()
    {
        return container;
    }

    public Composite getBody()
    {
        return container.getBody();
    }

    public void createContents()
    {
        Composite parent = getBody();

        createFileTemplateField(parent);
        createWordSeparatorField(parent);
        createFileTplExplanations(parent);
        createOverviewArea(parent);

        placeHolder(parent, 2);

        createFolderTemplateFields(parent);
        createFolderTplExplanations(parent);
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

        placeHolder(parent, 1);

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

    private void createFolderTplExplanations(final Composite parent)
    {
        String[] explanations = { //
        TestFolderPathPattern.SRC_PROJECT_VARIABLE + " = source project, * = variable part, ** = variable set of segments,", //
        "(**) or (*) = group capture, \\1 = reference to 1st captured group" };

        for (String e : explanations)
        {
            placeHolder(parent, 1);

            Label lbl = new Label(parent, SWT.NONE);
            lbl.setLayoutData(LayoutData.LABEL_AND_FIELD);
            lbl.setText(e);
        }

        container.addExpandableComposite("More explanations...", false, new CompositeBody()
        {
            public Control createBody(ExpandableComposite expandableComposite)
            {
                Composite inner = new Composite(expandableComposite, SWT.NONE);
                inner.setFont(parent.getFont());
                inner.setLayout(new GridLayout());

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
                    Label lbl = new Label(inner, SWT.NONE);
                    lbl.setText(e);
                }

                placeHolder(parent, 1);

                return inner;
            }
        });
    }

    public void placeHolder(Composite parent, int numColumns)
    {
        Label lbl = new Label(parent, SWT.NONE);
        GridData gridData = new GridData();
        gridData.horizontalSpan = numColumns;
        lbl.setLayoutData(gridData);
    }

    private void createFileTplExplanations(final Composite parent)
    {
        placeHolder(parent, 1);

        Label lbl = new Label(parent, SWT.NONE);
        lbl.setLayoutData(LayoutData.LABEL_AND_FIELD);
        lbl.setText(TestFileNamePattern.SRC_FILE_VARIABLE + " = source file name, * = any string, (abc|def) = 'abc' or 'def'");

        container.addExpandableComposite("More explanations...", false, new CompositeBody()
        {
            public Control createBody(ExpandableComposite expandableComposite)
            {
                Composite inner = new Composite(expandableComposite, SWT.NONE);
                inner.setFont(parent.getFont());
                inner.setLayout(new GridLayout());

                String[] explanations = { //
                "Use the variable " + TestFileNamePattern.SRC_FILE_VARIABLE + " to represent the production source file.", //
                "You may use stars '*' to represent variable parts.", //
                "You may use parentheses and pipes to define several possible prefixes or suffixes: (pre1|pre2)" };

                for (String e : explanations)
                {
                    Label lbl = new Label(inner, SWT.NONE);
                    lbl.setText(e);
                }

                placeHolder(inner, 1);

                return inner;
            }
        });
    }

    private void createOverviewArea(final Composite parent)
    {
        final FileNamePatternDemo demo = new FileNamePatternDemo()
        {
            @Override
            protected TestFileNamePattern getPattern()
            {
                return new TestFileNamePattern(testFileTemplateField.getText().trim(), wordSeparatorField.getText().trim());
            }

            @Override
            protected void sizeChanged()
            {
                container.reflow();
            }
        };

        wordSeparatorField.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                demo.patternChanged();
            }
        });

        container.addExpandableComposite("Demonstration", false, new CompositeBody()
        {
            public Control createBody(ExpandableComposite expandableComposite)
            {
                Composite inner = new Composite(expandableComposite, SWT.NONE);
                inner.setFont(parent.getFont());
                inner.setLayout(new GridLayout());
                demo.createContents(inner);
                return inner;
            }
        });
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
