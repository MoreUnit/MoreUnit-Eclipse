package org.moreunit.properties;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.PreferencesConverter;
import org.moreunit.util.StringConstants;

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

        layoutForOneLineControls = new GridData();
        layoutForOneLineControls.horizontalSpan = 2;
        layoutForOneLineControls.widthHint = 350;

        layoutForTextFields = new GridData();
        layoutForTextFields.widthHint = 200;
    }

    public Composite getControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout(2, false);
        composite.setLayout(gridLayout);

        createTestTypeChoice(composite);
        createTestMethodComposite(composite);
        createMethodContentTextField(composite);
        createPrefixSuffixTextFields(composite);
        createPackagePrefixSuffixTextFields(composite);
        createSuperClassTextField(composite);
        createFlexibleNamingCheckbox(composite);
        createExtendedSearchCheckbox(composite);

        checkStateOfMethodPrefixButton();

        return composite;
    }

    private void createTestTypeChoice(Composite parent)
    {
        // Label
        Label choiceLabel = new Label(parent, SWT.NONE);
        choiceLabel.setText(PreferenceConstants.TEXT_TEST_TYPE);
        choiceLabel.setLayoutData(layoutForOneLineControls);

        // Junit3 choice
        junit3Button = new Button(parent, SWT.RADIO);
        junit3Button.setText(PreferenceConstants.TEXT_JUNIT_3_8);
        junit3Button.setLayoutData(layoutForOneLineControls);
        junit3Button.setSelection(preferences.shouldUseJunit3Type(javaProject));
        junit3Button.addSelectionListener(this);

        // Junit 4 choice
        junit4Button = new Button(parent, SWT.RADIO);
        junit4Button.setText(PreferenceConstants.TEXT_JUNIT_4);
        junit4Button.setLayoutData(layoutForOneLineControls);
        junit4Button.setSelection(preferences.shouldUseJunit4Type(javaProject));
        junit4Button.addSelectionListener(this);

        // TestNg choice
        testNgButton = new Button(parent, SWT.RADIO);
        testNgButton.setText(PreferenceConstants.TEXT_TEST_NG);
        testNgButton.setLayoutData(layoutForOneLineControls);
        testNgButton.setSelection(preferences.shouldUseTestNgType(javaProject));
        testNgButton.addSelectionListener(this);
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
        methodContentLabel.setText("Method content");

        methodContentTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        methodContentTextField.setLayoutData(layoutForTextFields);
        methodContentTextField.setText(preferences.getTestMethodDefaultContent(javaProject));
    }

    private boolean shouldUseNoTestMethodPrefix()
    {
        return PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX.equals(preferences.getTestMethodType(javaProject));
    }

    private void createPrefixSuffixTextFields(Composite parent)
    {
        Label prefixLabel = new Label(parent, SWT.NONE);
        prefixLabel.setText(PreferenceConstants.TEXT_TEST_PREFIXES);
        prefixTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);

        Label suffixLabel = new Label(parent, SWT.NONE);
        suffixLabel.setText(PreferenceConstants.TEXT_TEST_SUFFIXES);
        suffixTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);

        prefixTextField.setLayoutData(layoutForTextFields);
        suffixTextField.setLayoutData(layoutForTextFields);

        prefixTextField.setText(PreferencesConverter.convertArrayToString(preferences.getPrefixes(javaProject)));
        suffixTextField.setText(PreferencesConverter.convertArrayToString(preferences.getSuffixes(javaProject)));
    }

    private void createPackagePrefixSuffixTextFields(Composite parent)
    {
        Label packagePrefixLabel = new Label(parent, SWT.NONE);
        packagePrefixLabel.setText(PreferenceConstants.TEXT_PACKAGE_PREFIX);
        packagePrefixTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);

        Label packageSuffixLabel = new Label(parent, SWT.NONE);
        packageSuffixLabel.setText(PreferenceConstants.TEXT_PACKAGE_SUFFIX);
        packageSuffixTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);

        packagePrefixTextField.setLayoutData(layoutForTextFields);
        packageSuffixTextField.setLayoutData(layoutForTextFields);

        packagePrefixTextField.setText(preferences.getTestPackagePrefix(javaProject));
        packageSuffixTextField.setText(preferences.getTestPackageSuffix(javaProject));
    }

    private void createSuperClassTextField(Composite parent)
    {
        Label superClassLabel = new Label(parent, SWT.NONE);
        superClassLabel.setText(PreferenceConstants.TEXT_TEST_SUPERCLASS);

        superClassTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        superClassTextField.setLayoutData(layoutForTextFields);
        superClassTextField.setText(preferences.getTestSuperClass(javaProject));
    }

    private void createFlexibleNamingCheckbox(Composite parent)
    {
        flexibleNamingCheckbox = new Button(parent, SWT.CHECK);
        flexibleNamingCheckbox.setText(PreferenceConstants.TEXT_FLEXIBLE_NAMING);
        flexibleNamingCheckbox.setLayoutData(layoutForOneLineControls);
        flexibleNamingCheckbox.setSelection(preferences.shouldUseFlexibleTestCaseNaming(javaProject));
    }
    
    private void createExtendedSearchCheckbox(Composite parent)
    {
        extendedSearchCheckbox = new Button(parent, SWT.CHECK);
        extendedSearchCheckbox.setText(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH);
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
