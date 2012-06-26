package org.moreunit.properties;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.moreunit.core.util.StringConstants;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.PreferencesConverter;

/**
 * @author vera 14.03.2008 23:09:24
 */
public class OtherMoreunitPropertiesBlock implements SelectionListener
{

    private Button junit3Button;
    private Button junit4Button;
    private Button testNgButton;

    private Button methodPrefixButton;

    private Text methodContentTextField;

    private Text prefixTextField;
    private Text suffixTextField;

    private Text packagePrefixTextField;
    private Text packageSuffixTextField;

    private Text superClassTextField;

    private Button flexibleNamingCheckbox;
    private Button extendedSearchCheckbox;

    private GridData layoutForTextFields;
    private GridData layoutForOneLineControls;

    private Preferences preferences;
    private IJavaProject javaProject;

    public OtherMoreunitPropertiesBlock(IJavaProject javaProject)
    {
        this.javaProject = javaProject;
        preferences = Preferences.getInstance();

        layoutForOneLineControls = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        layoutForOneLineControls.horizontalSpan = 2;

        layoutForTextFields = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        layoutForTextFields.horizontalIndent = 30;
    }

    public Composite getControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.numColumns = 2;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createCompositeWith2ColsParent(composite);

        return composite;
    }

    public void createCompositeWith2ColsParent(Composite parentWith2Cols)
    {
        createTestTypeChoice(parentWith2Cols);

        createTestMethodComposite(parentWith2Cols);
        createMethodContentTextField(parentWith2Cols);
        createPrefixSuffixTextFields(parentWith2Cols);
        createPackagePrefixSuffixTextFields(parentWith2Cols);
        createSuperClassTextField(parentWith2Cols);
        createFlexibleNamingCheckbox(parentWith2Cols);
        createExtendedSearchCheckbox(parentWith2Cols);

        checkStateOfMethodPrefixButton();
    }

    private void createTestTypeChoice(Composite parent)
    {
        // Group with label
        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout());
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        group.setLayoutData(gridData);
        group.setText(PreferenceConstants.TEXT_TEST_TYPE);

        GridData buttonGridData = new GridData();

        // Junit3 choice
        junit3Button = new Button(group, SWT.RADIO);
        junit3Button.setText(PreferenceConstants.TEXT_JUNIT_3_8);
        junit3Button.setLayoutData(buttonGridData);
        junit3Button.setSelection(preferences.shouldUseJunit3Type(javaProject));
        junit3Button.addSelectionListener(this);

        // Junit 4 choice
        junit4Button = new Button(group, SWT.RADIO);
        junit4Button.setText(PreferenceConstants.TEXT_JUNIT_4);
        junit4Button.setLayoutData(buttonGridData);
        junit4Button.setSelection(preferences.shouldUseJunit4Type(javaProject));
        junit4Button.addSelectionListener(this);

        // TestNg choice
        testNgButton = new Button(group, SWT.RADIO);
        testNgButton.setText(PreferenceConstants.TEXT_TEST_NG);
        testNgButton.setLayoutData(buttonGridData);
        testNgButton.setSelection(preferences.shouldUseTestNgType(javaProject));
        testNgButton.addSelectionListener(this);

        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        testNgButton.setLayoutData(gd);
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

        methodContentTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        methodContentTextField.setLayoutData(layoutForTextFields);
        methodContentTextField.setText(preferences.getTestMethodDefaultContent(javaProject));
        methodContentTextField.setToolTipText(PreferenceConstants.TOOLTIP_TEST_METHOD_CONTENT);
    }

    private boolean shouldUseNoTestMethodPrefix()
    {
        return PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX.equals(preferences.getTestMethodType(javaProject));
    }

    private void createPrefixSuffixTextFields(Composite parent)
    {
        Label prefixLabel = new Label(parent, SWT.NONE);
        prefixLabel.setText(PreferenceConstants.TEXT_TEST_PREFIXES);
        prefixLabel.setToolTipText(PreferenceConstants.TOOLTIP_TEST_PREFIXES);
        prefixTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);

        Label suffixLabel = new Label(parent, SWT.NONE);
        suffixLabel.setText(PreferenceConstants.TEXT_TEST_SUFFIXES);
        suffixLabel.setToolTipText(PreferenceConstants.TOOLTIP_TEST_SUFFIXES);
        suffixTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);

        prefixTextField.setLayoutData(layoutForTextFields);
        suffixTextField.setLayoutData(layoutForTextFields);

        prefixTextField.setText(PreferencesConverter.convertArrayToString(preferences.getPrefixes(javaProject)));
        prefixTextField.setToolTipText(PreferenceConstants.TOOLTIP_TEST_PREFIXES);

        suffixTextField.setText(PreferencesConverter.convertArrayToString(preferences.getSuffixes(javaProject)));
        suffixTextField.setToolTipText(PreferenceConstants.TOOLTIP_TEST_SUFFIXES);
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

        packagePrefixTextField.setLayoutData(layoutForTextFields);
        packageSuffixTextField.setLayoutData(layoutForTextFields);

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
        superClassTextField.setLayoutData(layoutForTextFields);
        superClassTextField.setText(preferences.getTestSuperClass(javaProject));
        superClassTextField.setToolTipText(PreferenceConstants.TOOLTIP_TEST_SUPERCLASS);
    }

    private void createFlexibleNamingCheckbox(Composite parent)
    {
        flexibleNamingCheckbox = new Button(parent, SWT.CHECK);
        flexibleNamingCheckbox.setText(PreferenceConstants.TEXT_FLEXIBLE_NAMING);
        flexibleNamingCheckbox.setToolTipText(PreferenceConstants.TOOLTIP_FLEXIBLE_NAMING);
        flexibleNamingCheckbox.setLayoutData(layoutForOneLineControls);
        flexibleNamingCheckbox.setSelection(preferences.shouldUseFlexibleTestCaseNaming(javaProject));
    }

    private void createExtendedSearchCheckbox(Composite parent)
    {
        extendedSearchCheckbox = new Button(parent, SWT.CHECK);
        extendedSearchCheckbox.setText(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH);
        extendedSearchCheckbox.setToolTipText(PreferenceConstants.TOOLTIP_EXTENDED_TEST_METHOD_SEARCH);
        extendedSearchCheckbox.setLayoutData(layoutForOneLineControls);
        extendedSearchCheckbox.setSelection(preferences.shouldUseTestMethodExtendedSearch(javaProject));
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

        preferences.setTestMethodDefaultContent(javaProject, methodContentTextField.getText());

        preferences.setPrefixes(javaProject, PreferencesConverter.convertStringToArray(prefixTextField.getText()));
        preferences.setSuffixes(javaProject, PreferencesConverter.convertStringToArray(suffixTextField.getText()));

        preferences.setTestPackagePrefix(javaProject, packagePrefixTextField.getText());
        preferences.setTestPackageSuffix(javaProject, packageSuffixTextField.getText());

        preferences.setTestSuperClass(javaProject, superClassTextField.getText());

        preferences.setShouldUseFlexibleTestCaseNaming(javaProject, flexibleNamingCheckbox.getSelection());
        preferences.setShouldUseTestMethodExtendedSearch(javaProject, extendedSearchCheckbox.getSelection());
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

    public GridData getLayoutForTextFields()
    {
        return layoutForTextFields;
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
}
