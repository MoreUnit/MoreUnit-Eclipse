package org.moreunit.core.preferences;

import static org.moreunit.core.util.Strings.countOccurrences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.moreunit.core.matching.TestFileNamePattern;
import org.moreunit.core.ui.Composites;
import org.moreunit.core.ui.ExpandableCompositeContainer;
import org.moreunit.core.ui.FileNamePatternDemo;
import org.moreunit.core.ui.Labels;
import org.moreunit.core.ui.LayoutData;

public class TestFileNamePatternGroup
{
    private static final int EXPLANATION_WIDTH_HINT = 350;

    private final ExpandableCompositeContainer container;
    private final TestFileNamePatternPreferencesWriter prefWriter;
    private final Text testFileTemplateField;
    private final OptionalTextField wordSeparatorField;

    public static TestFileNamePatternGroup forCamelCasePattern(Composite parent, ExpandableCompositeContainer container, TestFileNamePatternPreferencesWriter prefsWriter)
    {
        return new TestFileNamePatternGroup(parent, container, prefsWriter, true);
    }

    public static TestFileNamePatternGroup forPatternUsingSeparator(Composite parent, ExpandableCompositeContainer container, TestFileNamePatternPreferencesWriter prefsWriter)
    {
        return new TestFileNamePatternGroup(parent, container, prefsWriter, false);
    }

    private TestFileNamePatternGroup(Composite parent, ExpandableCompositeContainer container, TestFileNamePatternPreferencesWriter prefsWriter, boolean forceCamelCase)
    {
        this.container = container;
        this.prefWriter = prefsWriter;

        final Composite fileTplGroup = Composites.gridGroup(parent, "Rule for naming test files:", 2, 10);

        testFileTemplateField = createFileTemplateField(fileTplGroup);
        wordSeparatorField = createWordSeparatorField(fileTplGroup, forceCamelCase);
        createFileTplExplanations(fileTplGroup);
        createOverviewArea(fileTplGroup);
    }

    private Text createFileTemplateField(Composite parent)
    {
        final Label label = new Label(parent, SWT.NONE);
        label.setText("Pattern:");

        final Text field = new Text(parent, SWT.SINGLE | SWT.BORDER);
        field.setLayoutData(LayoutData.labelledField());

        if(prefWriter.getTestFileNameTemplate().length() != 0)
        {
            field.setText(prefWriter.getTestFileNameTemplate());
        }
        else
        {
            field.setText(Preferences.DEFAULTS.getTestFileNameTemplate());
        }

        return field;
    }

    private OptionalTextField createWordSeparatorField(Composite parent, boolean forceCamelCase)
    {
        if(forceCamelCase)
        {
            return new OptionalTextField("");
        }

        final Label label = new Label(parent, SWT.NONE);
        label.setText("Word separator:");

        final Text field = new Text(parent, SWT.SINGLE | SWT.BORDER);
        field.setLayoutData(LayoutData.labelledField());

        if(prefWriter.getFileWordSeparator().length() != 0)
        {
            field.setText(prefWriter.getFileWordSeparator());
        }
        else
        {
            field.setText(Preferences.DEFAULTS.getFileWordSeparator());
        }

        return new OptionalTextField(field);
    }

    private void createFileTplExplanations(final Composite parent)
    {
        Labels.placeHolder(parent, 1);

        final Label lbl = new Label(parent, SWT.NONE);
        lbl.setLayoutData(LayoutData.labelledField());
        lbl.setText(TestFileNamePattern.SRC_FILE_VARIABLE + " = source file name, * = any string, (abc|def) = 'abc' or 'def'");

        container.newExpandableComposite(parent, "More explanations...", false, expandableComposite -> {
            final Composite inner = new Composite(expandableComposite, SWT.NONE);
            inner.setFont(parent.getFont());
            inner.setLayout(new GridLayout());

            final String[] explanations = { //
            "Use the variable " + TestFileNamePattern.SRC_FILE_VARIABLE + " to represent the production source file.", //
            "You may use stars '*' to represent variable parts.", //
            "You may use parentheses and pipes to define several possible prefixes or suffixes: (pre1|pre2)" };

            for (final String e : explanations)
            {
                final Label lbl1 = Labels.wrappingLabel(e, EXPLANATION_WIDTH_HINT, inner);
                lbl1.setText(e);
            }

            return inner;
        });
    }

    private void createOverviewArea(final Composite parent)
    {
        final FileNamePatternDemo demo = new FileNamePatternDemo()
        {
            @Override
            protected TestFileNamePattern getPattern()
            {
                return new TestFileNamePattern(testFileTemplateField.getText().trim(), wordSeparatorField.getText());
            }

            @Override
            protected void sizeChanged()
            {
                container.reflow();
            }
        };

        wordSeparatorField.addModifyListener(e -> demo.patternChanged());

        container.newExpandableComposite(parent, "Demonstration", false, expandableComposite -> {
            final Composite inner = new Composite(expandableComposite, SWT.NONE);
            inner.setFont(parent.getFont());
            inner.setLayout(new GridLayout());
            demo.createContents(inner);
            return inner;
        });

        // initializes demo field
        demo.patternChanged();
    }

    public void addModifyListener(ModifyListener listener)
    {
        testFileTemplateField.addModifyListener(listener);
    }

    public void forceFocus()
    {
        testFileTemplateField.forceFocus();
    }

    public String getError()
    {
        return validateTestFileTemplate();
    }

    public String getWarning()
    {
        if(countOccurrences(testFileTemplateField.getText(), "*") > 1)
        {
            return "Using too many wildcards may degrade search performance and results!";
        }
        return null;
    }

    private String validateTestFileTemplate()
    {
        final String testFileTemplate = testFileTemplateField.getText().trim();
        final String separator = wordSeparatorField.getText();

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

    public void saveProperties()
    {
        prefWriter.setTestFileNameTemplate(testFileTemplateField.getText().trim(), wordSeparatorField.getText());
    }

    public void setEnabled(boolean enabled)
    {
        wordSeparatorField.setEnabled(enabled);
        testFileTemplateField.setEnabled(enabled);
        container.setExpandable(enabled);
    }

    public void restoreDefaults()
    {
        testFileTemplateField.setText(Preferences.DEFAULTS.getTestFileNameTemplate());
        wordSeparatorField.setText(Preferences.DEFAULTS.getFileWordSeparator());
    }

    private static class OptionalTextField
    {
        final Text field;
        final String value;

        OptionalTextField(Text field)
        {
            this.field = field;
            value = null;
        }

        OptionalTextField(String value)
        {
            field = null;
            this.value = value;
        }

        void setText(String fileWordSeparator)
        {
            if(field == null)
            {
                return;
            }
            field.setText(fileWordSeparator);
        }

        void setEnabled(boolean enabled)
        {
            if(field == null)
            {
                return;
            }
            field.setEnabled(enabled);
        }

        void addModifyListener(ModifyListener modifyListener)
        {
            if(field == null)
            {
                return;
            }
            field.addModifyListener(modifyListener);
        }

        String getText()
        {
            return field == null ? value : field.getText().trim();
        }
    }
}
