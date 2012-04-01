package org.moreunit.core.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.moreunit.core.MoreUnitCore;
import org.moreunit.core.Preferences;
import org.moreunit.core.Preferences.ProjectPreferences;
import org.moreunit.core.matching.TestFileNamePattern;

public class GenericPropertyPage extends PropertyPage
{
    private final Preferences preferences;
    private Text testFileTemplateField;

    public GenericPropertyPage()
    {
        preferences = MoreUnitCore.get().getPreferences();
    }

    @Override
    protected Control createContents(Composite parent)
    {
        initializeDialogUnits(parent);

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
        ProjectPreferences projectPreferences = preferences.get((IProject) getElement());

        GridData labelAndFieldLayout = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        labelAndFieldLayout.horizontalIndent = 30;

        Label testFileTemplateLabel = new Label(parent, SWT.NONE);
        testFileTemplateLabel.setText("Rule for naming test files:");

        testFileTemplateField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        testFileTemplateField.setLayoutData(labelAndFieldLayout);
        testFileTemplateField.setText(projectPreferences.getTestFileNameTemplate());
        testFileTemplateField.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent event)
            {
                validate();
            }
        });

        GridData rowLayout = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        rowLayout.horizontalSpan = 2;

        Label explainationLabel = new Label(parent, SWT.NONE);
        explainationLabel.setLayoutData(rowLayout);
        explainationLabel.setText("Use the variable " + TestFileNamePattern.SRC_FILE_VARIABLE + " to represent the production source file.");
    }

    @Override
    public boolean performOk()
    {
        saveProperties();
        return super.performOk();
    }

    @Override
    protected void performDefaults()
    {
        super.performDefaults();
        testFileTemplateField.setText(Preferences.DEFAULT_TEST_FILE_NAME_TEMPLATE);
    }

    private void validate()
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
            setMessage(null);
            setValid(true);
        }
        else
        {
            setMessage(errorMsg, IMessageProvider.ERROR);
            setValid(false);
            testFileTemplateField.forceFocus();
        }
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if(! visible)
        {
            return;
        }

        validate();
    }

    private void saveProperties()
    {
        ProjectPreferences projectPreferences = preferences.get((IProject) getElement());
        projectPreferences.setTestFileNameTemplate(testFileTemplateField.getText());
        projectPreferences.save();
    }
}
