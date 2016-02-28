package org.moreunit.properties;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.moreunit.core.preferences.TestFileNamePatternGroup;
import org.moreunit.core.preferences.TestFileNamePatternPreferencesWriter;
import org.moreunit.core.ui.Composites;
import org.moreunit.core.ui.ExpandableCompositeContainer;
import org.moreunit.core.ui.LayoutData;
import org.moreunit.core.util.StringConstants;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.ProjectPreferences;
import org.moreunit.preferences.TestAnnotationMode;

/**
 * @author vera 14.03.2008 23:09:24
 */
public class OtherMoreunitPropertiesBlock implements SelectionListener
{
    private Button junit3Button;
    private Button junit4Button;
    private Button testNgButton;

    private Button methodPrefixButton;
    private TestFileNamePatternGroup testCaseNamePatternArea;
    private Text methodContentTextField;
    private Text packagePrefixTextField;
    private Text packageSuffixTextField;
    private Text superClassTextField;
    private Button addCommentsToTestMethodCheckbox;
    private Button extendedSearchCheckbox;
    private Button enableSearchByNameCheckbox;

    private Button testAnnotationsDisabledButton;
    private Button testAnnotationsByNameButton;
    private Button testAnnotationsByNameAndByCallButton;

    private GridData layoutForOneLineControls;

    private final Preferences preferences;
    private final IJavaProject javaProject;
    private final ProjectPreferences projectPreferences;
    private ExpandableCompositeContainer container;

    public OtherMoreunitPropertiesBlock(IJavaProject javaProject)
    {
        this.javaProject = javaProject;
        preferences = Preferences.getInstance();
        projectPreferences = Preferences.forProject(javaProject);

        layoutForOneLineControls = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        layoutForOneLineControls.horizontalSpan = 2;
    }

    public Composite getControl(Composite parent, boolean createIntermediateComposite)
    {
        Composite newParent = createIntermediateComposite ? new Composite(parent, SWT.NONE) : parent;

        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        newParent.setLayout(layout);

        newParent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        container = new ExpandableCompositeContainer(newParent);

        Composite grid = Composites.grid(container, 2);

        beforeContent(grid);

        createCompositeWith2ColsParent(grid);

        return newParent;
    }

    /**
     * To be overridden when needed.
     */
    protected void beforeContent(Composite parentWith2Cols)
    {
    }

    private void createCompositeWith2ColsParent(final Composite parentWith2Cols)
    {
        createTestTypeChoice(parentWith2Cols);

        createTestNamePatternArea(parentWith2Cols);

        createTestMethodComposite(parentWith2Cols);
        createMethodContentTextField(parentWith2Cols);
        createPackagePrefixSuffixTextFields(parentWith2Cols);
        createSuperClassTextField(parentWith2Cols);
        createExtendedSearchCheckboxes(parentWith2Cols);
        createTestAnnotationModeRadioButtons(parentWith2Cols);
        createAddCommentsToTestMethodsCheckbox(parentWith2Cols);

        checkStateOfMethodPrefixButton();
    }

    private void createTestTypeChoice(Composite parent)
    {
        // Group with label
        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        group.setLayoutData(gridData);
        group.setText(PreferenceConstants.TEXT_TEST_TYPE);

        // Junit3 choice
        junit3Button = new Button(group, SWT.RADIO);
        junit3Button.setText(PreferenceConstants.TEXT_JUNIT_3_8);
        junit3Button.setLayoutData(radioButtonLayoutData(false));
        junit3Button.setSelection(projectPreferences.shouldUseJunit3Type());
        junit3Button.addSelectionListener(this);

        // Junit 4 choice
        junit4Button = new Button(group, SWT.RADIO);
        junit4Button.setText(PreferenceConstants.TEXT_JUNIT_4);
        junit4Button.setLayoutData(radioButtonLayoutData(true));
        junit4Button.setSelection(projectPreferences.shouldUseJunit4Type());
        junit4Button.addSelectionListener(this);

        // TestNg choice
        testNgButton = new Button(group, SWT.RADIO);
        testNgButton.setText(PreferenceConstants.TEXT_TEST_NG);
        testNgButton.setLayoutData(radioButtonLayoutData(true));
        testNgButton.setSelection(projectPreferences.shouldUseTestNgType());
        testNgButton.addSelectionListener(this);
    }

    private GridData radioButtonLayoutData(boolean indent)
    {
        GridData data = new GridData();
        if(indent)
        {
            data.horizontalIndent = 20;
        }
        return data;
    }

    private void createTestNamePatternArea(Composite parentWith2Cols)
    {
        Composite parent = Composites.fillWidth(parentWith2Cols);

        testCaseNamePatternArea = TestFileNamePatternGroup.forCamelCasePattern(parent, container, new TestFileNamePatternPreferencesWriter()
        {
            public void setTestFileNameTemplate(String template, String separator)
            {
                projectPreferences.setTestClassNameTemplate(template);
            }

            public String getTestFileNameTemplate()
            {
                return projectPreferences.getTestClassNameTemplate();
            }

            public String getFileWordSeparator()
            {
                return "";
            }
        });
    }

    private void createTestMethodComposite(Composite parent)
    {
        methodPrefixButton = new Button(parent, SWT.CHECK);
        methodPrefixButton.setText(PreferenceConstants.TEXT_TEST_METHOD_TYPE);
        methodPrefixButton.setLayoutData(layoutForOneLineControls);
        methodPrefixButton.setSelection(! shouldUseNoTestMethodPrefix());
    }

    private void createMethodContentTextField(Composite parent)
    {
        Label methodContentLabel = new Label(parent, SWT.NONE);
        methodContentLabel.setText(PreferenceConstants.TEXT_TEST_METHOD_CONTENT);
        methodContentLabel.setToolTipText(PreferenceConstants.TOOLTIP_TEST_METHOD_CONTENT);

        methodContentTextField = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        methodContentTextField.setLayoutData(LayoutData.labelledField());
        methodContentTextField.setText(preferences.getTestMethodDefaultContent(javaProject));
        methodContentTextField.setToolTipText(PreferenceConstants.TOOLTIP_TEST_METHOD_CONTENT);
    }

    private boolean shouldUseNoTestMethodPrefix()
    {
        return PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX.equals(preferences.getTestMethodType(javaProject));
    }

    private void createPackagePrefixSuffixTextFields(Composite parent)
    {
        Label packagePrefixLabel = new Label(parent, SWT.NONE);
        packagePrefixLabel.setText(PreferenceConstants.TEXT_PACKAGE_PREFIX);
        packagePrefixLabel.setToolTipText(PreferenceConstants.TOOLTIP_PACKAGE_PREFIX);
        packagePrefixTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);

        Label packageSuffixLabel = new Label(parent, SWT.NONE);
        packageSuffixLabel.setText(PreferenceConstants.TEXT_PACKAGE_SUFFIX);
        packageSuffixLabel.setToolTipText(PreferenceConstants.TOOLTIP_PACKAGE_SUFFIX);
        packageSuffixTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);

        packagePrefixTextField.setLayoutData(LayoutData.labelledField());
        packageSuffixTextField.setLayoutData(LayoutData.labelledField());

        packagePrefixTextField.setText(preferences.getTestPackagePrefix(javaProject));
        packageSuffixTextField.setText(preferences.getTestPackageSuffix(javaProject));

        packagePrefixTextField.setToolTipText(PreferenceConstants.TOOLTIP_PACKAGE_PREFIX);
        packageSuffixTextField.setToolTipText(PreferenceConstants.TOOLTIP_PACKAGE_SUFFIX);
    }

    private void createSuperClassTextField(Composite parent)
    {
        Label superClassLabel = new Label(parent, SWT.NONE);
        superClassLabel.setText(PreferenceConstants.TEXT_TEST_SUPERCLASS);
        superClassLabel.setToolTipText(PreferenceConstants.TOOLTIP_TEST_SUPERCLASS);

        superClassTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        superClassTextField.setLayoutData(LayoutData.labelledField());
        superClassTextField.setText(preferences.getTestSuperClass(javaProject));
        superClassTextField.setToolTipText(PreferenceConstants.TOOLTIP_TEST_SUPERCLASS);
    }

    private void createExtendedSearchCheckboxes(Composite parent)
    {
        extendedSearchCheckbox = new Button(parent, SWT.CHECK);
        extendedSearchCheckbox.setText(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH);
        extendedSearchCheckbox.setToolTipText(PreferenceConstants.TOOLTIP_EXTENDED_TEST_METHOD_SEARCH);
        extendedSearchCheckbox.setLayoutData(layoutForOneLineControls);
        extendedSearchCheckbox.setSelection(preferences.getMethodSearchMode(javaProject).searchByCall);
        extendedSearchCheckbox.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if(! extendedSearchCheckbox.getSelection())
                {
                    enableSearchByNameCheckbox.setSelection(true);
                }
            }
        });

        enableSearchByNameCheckbox = new Button(parent, SWT.CHECK);
        enableSearchByNameCheckbox.setText(PreferenceConstants.TEXT_ENABLE_TEST_METHOD_SEARCH_BY_NAME);
        enableSearchByNameCheckbox.setToolTipText(PreferenceConstants.TOOLTIP_ENABLE_TEST_METHOD_SEARCH_BY_NAME);
        enableSearchByNameCheckbox.setLayoutData(layoutForOneLineControls);
        enableSearchByNameCheckbox.setSelection(preferences.getMethodSearchMode(javaProject).searchByName);
        enableSearchByNameCheckbox.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if(! enableSearchByNameCheckbox.getSelection())
                {
                    extendedSearchCheckbox.setSelection(true);
                }
            }
        });
    }

    private void createTestAnnotationModeRadioButtons(Composite parent)
    {
        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        group.setLayoutData(gridData);
        group.setText(PreferenceConstants.TEXT_ANNOTATION_MODE);

        TestAnnotationMode mode = projectPreferences.getTestAnnotationMode();

        testAnnotationsDisabledButton = new Button(group, SWT.RADIO);
        testAnnotationsDisabledButton.setText(PreferenceConstants.TEST_ANNOTATION_MODE_DISABLED);
        testAnnotationsDisabledButton.setLayoutData(radioButtonLayoutData(false));
        testAnnotationsDisabledButton.setSelection(mode == null || mode == TestAnnotationMode.OFF);

        testAnnotationsByNameButton = new Button(group, SWT.RADIO);
        testAnnotationsByNameButton.setText(PreferenceConstants.TEST_ANNOTATION_MODE_BY_NAME);
        testAnnotationsByNameButton.setLayoutData(radioButtonLayoutData(true));
        testAnnotationsByNameButton.setSelection(mode == TestAnnotationMode.BY_NAME);

        testAnnotationsByNameAndByCallButton = new Button(group, SWT.RADIO);
        testAnnotationsByNameAndByCallButton.setText(PreferenceConstants.TEST_ANNOTATION_MODE_EXTENDED_SEARCH);
        testAnnotationsByNameAndByCallButton.setToolTipText(PreferenceConstants.TOOLTIP_TEST_ANNOTATION_EXTENDED_SEARCH);
        testAnnotationsByNameAndByCallButton.setLayoutData(radioButtonLayoutData(true));
        testAnnotationsByNameAndByCallButton.setSelection(mode == TestAnnotationMode.BY_CALL_AND_BY_NAME);
    }

    private void createAddCommentsToTestMethodsCheckbox(Composite parent)
    {
        addCommentsToTestMethodCheckbox = new Button(parent, SWT.CHECK);
        addCommentsToTestMethodCheckbox.setText(PreferenceConstants.TEXT_GENERATE_COMMENTS_FOR_TEST_METHOD);
        addCommentsToTestMethodCheckbox.setLayoutData(layoutForOneLineControls);
        addCommentsToTestMethodCheckbox.setSelection(preferences.shouldGenerateCommentsForTestMethod(javaProject));
    }

    public void saveProperties()
    {
        preferences.setTestType(javaProject, getSelectedTestType());

        if(junit3Button.getSelection())
        {
            preferences.setTestMethodTypeShouldUsePrefix(javaProject, true);
        }
        else
        {
            preferences.setTestMethodTypeShouldUsePrefix(javaProject, methodPrefixButton.getSelection());
        }

        testCaseNamePatternArea.saveProperties();

        preferences.setTestMethodDefaultContent(javaProject, methodContentTextField.getText());

        preferences.setTestPackagePrefix(javaProject, packagePrefixTextField.getText());
        preferences.setTestPackageSuffix(javaProject, packageSuffixTextField.getText());

        preferences.setTestSuperClass(javaProject, superClassTextField.getText());

        preferences.setShouldUseTestMethodExtendedSearch(javaProject, extendedSearchCheckbox.getSelection());
        preferences.setShouldUseTestMethodSearchByName(javaProject, enableSearchByNameCheckbox.getSelection());
        preferences.setGenerateCommentsForTestMethod(javaProject, addCommentsToTestMethodCheckbox.getSelection());

        if(testAnnotationsByNameAndByCallButton.getSelection())
        {
            preferences.setTestAnnotationMode(javaProject, TestAnnotationMode.BY_CALL_AND_BY_NAME);
        }
        else if(testAnnotationsByNameButton.getSelection())
        {
            preferences.setTestAnnotationMode(javaProject, TestAnnotationMode.BY_NAME);
        }
        else
        {
            preferences.setTestAnnotationMode(javaProject, TestAnnotationMode.OFF);
        }
    }

    private boolean isJunit4OrTestNgTestTypeSelected()
    {
        return junit4Button.getSelection() || testNgButton.getSelection();
    }

    private String getSelectedTestType()
    {
        if(junit3Button.getSelection())
            return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3;
        else if(junit4Button.getSelection())
            return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4;
        else if(testNgButton.getSelection())
            return PreferenceConstants.TEST_TYPE_VALUE_TESTNG;

        return StringConstants.EMPTY_STRING;
    }

    /*
     * Interface SelectionListener
     */

    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    /*
     * One of the test type buttons was clicked by the use. Now it is necessary
     * to check if the test method prefix checkbox should be hidden.
     */
    public void widgetSelected(SelectionEvent e)
    {
        checkStateOfMethodPrefixButton();
    }

    private void checkStateOfMethodPrefixButton()
    {
        methodPrefixButton.setEnabled(isJunit4OrTestNgTestTypeSelected());
    }

    public String getError()
    {
        return testCaseNamePatternArea.getError();
    }

    public String getWarning()
    {
        return testCaseNamePatternArea.getWarning();
    }

    public void addModifyListener(ModifyListener listener)
    {
        testCaseNamePatternArea.addModifyListener(listener);
    }

    public void forceFocus()
    {
        testCaseNamePatternArea.forceFocus();
    }

    public void setEnabled(boolean enabled)
    {
        junit3Button.setEnabled(enabled);
        junit4Button.setEnabled(enabled);
        testNgButton.setEnabled(enabled);
        methodPrefixButton.setEnabled(enabled);
        testCaseNamePatternArea.setEnabled(enabled);
        methodContentTextField.setEnabled(enabled);
        packagePrefixTextField.setEnabled(enabled);
        packageSuffixTextField.setEnabled(enabled);
        superClassTextField.setEnabled(enabled);
        extendedSearchCheckbox.setEnabled(enabled);
        enableSearchByNameCheckbox.setEnabled(enabled);
        addCommentsToTestMethodCheckbox.setEnabled(enabled);
        testCaseNamePatternArea.setEnabled(enabled);
        testAnnotationsDisabledButton.setEnabled(enabled);
        testAnnotationsByNameButton.setEnabled(enabled);
        testAnnotationsByNameAndByCallButton.setEnabled(enabled);
    }
}
