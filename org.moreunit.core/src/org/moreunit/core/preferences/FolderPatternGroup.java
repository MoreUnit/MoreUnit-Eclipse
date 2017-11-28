package org.moreunit.core.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.moreunit.core.matching.TestFolderPathPattern;
import org.moreunit.core.ui.Composites;
import org.moreunit.core.ui.ExpandableCompositeContainer;
import org.moreunit.core.ui.ExpandableCompositeContainer.ExpandableContent;
import org.moreunit.core.ui.Labels;
import org.moreunit.core.ui.LayoutData;

public class FolderPatternGroup implements GenericPreferencesGroup
{
    private static final int EXPLANATION_WIDTH_HINT = 350;
    
    private final ExpandableCompositeContainer container;
    private final LanguagePreferencesWriter prefWriter;
    private final Composite folderTplGroup;
    
    private Text srcFolderTemplateField;
    private Text testFolderTemplateField;
    
    public FolderPatternGroup(Composite parent, ExpandableCompositeContainer container, LanguagePreferencesWriter prefsWriter)
    {
        this.container = container;
        this.prefWriter = prefsWriter;

        this.folderTplGroup = Composites.gridGroup(parent, "Rule for locating test files:", 2, 10);
    }
    
    @Override
    public void createContents()
    {
        createFolderTemplateFields(folderTplGroup);
        createFolderTplExplanations(folderTplGroup);
        
    }

    @Override
    public void forceFocus()
    {
        // no action on Focus
    }

    @Override
    public String getError()
    {
        return validateTestFolderTemplate();
    }

    @Override
    public String getWarning()
    {
        return null;
    }

    @Override
    public void saveProperties()
    {
        prefWriter.setTestFolderPathTemplate(srcFolderTemplateField.getText().trim(), testFolderTemplateField.getText().trim());        
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        srcFolderTemplateField.setEnabled(enabled);
        testFolderTemplateField.setEnabled(enabled);        
    }

    @Override
    public void restoreDefaults()
    {
        srcFolderTemplateField.setText(Preferences.DEFAULTS.getSrcFolderPathTemplate());
        testFolderTemplateField.setText(Preferences.DEFAULTS.getTestFolderPathTemplate());        
    }
    
    private void createFolderTemplateFields(Composite parent)
    {
        Label label = new Label(parent, SWT.NONE);
        label.setText("Source path:");

        srcFolderTemplateField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        srcFolderTemplateField.setLayoutData(LayoutData.labelledField());

        if(prefWriter.getSrcFolderPathTemplate().length() != 0)
        {
            srcFolderTemplateField.setText(prefWriter.getSrcFolderPathTemplate());
        }
        else
        {
            srcFolderTemplateField.setText(Preferences.DEFAULTS.getSrcFolderPathTemplate());
        }

        label = new Label(parent, SWT.NONE);
        label.setText("Corresponding test path:");

        testFolderTemplateField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        testFolderTemplateField.setLayoutData(LayoutData.labelledField());

        if(prefWriter.getTestFolderPathTemplate().length() != 0)
        {
            testFolderTemplateField.setText(prefWriter.getTestFolderPathTemplate());
        }
        else
        {
            testFolderTemplateField.setText(Preferences.DEFAULTS.getTestFolderPathTemplate());
        }
    }

    private void createFolderTplExplanations(final Composite parent)
    {
        String[] explanations = { //
        TestFolderPathPattern.SRC_PROJECT_VARIABLE + " = source project, * = variable part, ** = variable set of segments,", //
        "(**) or (*) = group capture, \\1 = reference to 1st captured group" };

        for (String e : explanations)
        {
            Labels.placeHolder(parent, 1);

            Label lbl = new Label(parent, SWT.NONE);
            lbl.setLayoutData(LayoutData.labelledField());
            lbl.setText(e);
        }

        container.newExpandableComposite(parent, "More explanations...", false, new ExpandableContent()
        {
            public Control createBody(ExpandableComposite expandableComposite)
            {
                Composite inner = new Composite(expandableComposite, SWT.NONE);
                inner.setFont(parent.getFont());
                inner.setLayout(new GridLayout());

                String[] explanations = { //
                "Use the variable " + TestFolderPathPattern.SRC_PROJECT_VARIABLE + " to represent the production source project.", //
                "You may use stars '*' to represent variable parts within path segments, and double stars '**' for a variable set of path segments.", //
                "You may capture variable parts using parentheses and then reference them using backslashes '\\'.", //
                "When matching files, the part of the file path that lies after then end of your pattern is automatically used.", //
                "", //
                "Example: the definition '" + TestFolderPathPattern.SRC_PROJECT_VARIABLE + "'/(*)-src' => '" + TestFolderPathPattern.SRC_PROJECT_VARIABLE + "'/\\1-test'" //
                        + " allows for finding the file 'my-project/js-test/some/path/to/MyClassTest.js'" //
                        + " from 'my-project/js-src/some/path/to/MyClass.js'" };

                for (String e : explanations)
                {
                    Label lbl = Labels.wrappingLabel(e, EXPLANATION_WIDTH_HINT, inner);
                    lbl.setText(e);
                }

                return inner;
            }
        });
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

    @Override
    public void addModifyListener(ModifyListener listener)
    {
        srcFolderTemplateField.addModifyListener(listener);
        testFolderTemplateField.addModifyListener(listener);        
    }
}
