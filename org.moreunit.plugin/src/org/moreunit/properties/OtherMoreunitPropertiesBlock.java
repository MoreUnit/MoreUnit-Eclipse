package org.moreunit.properties;


import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
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
 * @author vera
 *
 * 14.03.2008 23:09:24
 */
public class OtherMoreunitPropertiesBlock {
	
	private Button junit3Button;
	private Button junit4Button;
	private Button testNgButton;
	
	private Text prefixTextField;
	private Text suffixTextField;
	
	private Text packagePrefixTextField;
	private Text packageSuffixTextField;
	
	private Text superClassTextField;
	
	private Button flexibleNamingCheckbox;
	
	private GridData layoutForTextFields;
	private GridData layourForOneLineControls;
	
	private IJavaProject javaProject;

	public OtherMoreunitPropertiesBlock(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public Composite getControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		
		GridLayout gridLayout = new GridLayout(2, false);
		composite.setLayout(gridLayout);
		
		layourForOneLineControls = new GridData();
		layourForOneLineControls.horizontalSpan = 2;
		layourForOneLineControls.widthHint = 350;
		
		layoutForTextFields = new GridData();
		layoutForTextFields.widthHint = 200;
		
		createTestTypeChoice(composite);
		createPrefixSuffixTextFields(composite);
		createPackagePrefixSuffixTextFields(composite);
		createSuperClassTextField(composite);
		createFlexibleNamingCheckbox(composite);
		
		return composite;
	}
	

	private void createTestTypeChoice(Composite parent) {
		// Label
		Label choiceLabel = new Label(parent, SWT.NONE);
		choiceLabel.setText(PreferenceConstants.TEXT_TEST_TYPE);
		choiceLabel.setLayoutData(layourForOneLineControls);
		
		// Junit3 choice
		junit3Button = new Button(parent, SWT.RADIO);
		junit3Button.setText(PreferenceConstants.TEXT_JUNIT_3_8);
		junit3Button.setLayoutData(layourForOneLineControls);
		junit3Button.setSelection(Preferences.getInstance().shouldUseJunit3Type(javaProject));
		
		// Junit 4 choice
		junit4Button = new Button(parent, SWT.RADIO);
		junit4Button.setText(PreferenceConstants.TEXT_JUNIT_4);
		junit4Button.setLayoutData(layourForOneLineControls);
		junit4Button.setSelection(Preferences.getInstance().shouldUseJunit4Type(javaProject));
		
		// TestNg choice
		testNgButton = new Button(parent, SWT.RADIO);
		testNgButton.setText(PreferenceConstants.TEXT_TEST_NG);
		testNgButton.setLayoutData(layourForOneLineControls);
		testNgButton.setSelection(Preferences.getInstance().shouldUseTestNgType(javaProject));
	}
	
	private void createPrefixSuffixTextFields(Composite parent) {
		Label prefixLabel = new Label(parent, SWT.NONE);
		prefixLabel.setText(PreferenceConstants.TEXT_TEST_PREFIXES);
		prefixTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);
		
		Label suffixLabel = new Label(parent, SWT.NONE);
		suffixLabel.setText(PreferenceConstants.TEXT_TEST_SUFFIXES);
		suffixTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);
		
		prefixTextField.setLayoutData(layoutForTextFields);
		suffixTextField.setLayoutData(layoutForTextFields);
		
		prefixTextField.setText(PreferencesConverter.convertArrayToString(Preferences.getInstance().getPrefixes(javaProject)));
		suffixTextField.setText(PreferencesConverter.convertArrayToString(Preferences.getInstance().getSuffixes(javaProject)));
	}
	
	private void createPackagePrefixSuffixTextFields(Composite parent) {
		Label packagePrefixLabel = new Label(parent, SWT.NONE);
		packagePrefixLabel.setText(PreferenceConstants.TEXT_PACKAGE_PREFIX);
		packagePrefixTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);

		Label packageSuffixLabel = new Label(parent, SWT.NONE);
		packageSuffixLabel.setText(PreferenceConstants.TEXT_PACKAGE_SUFFIX);
		packageSuffixTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);
		
		packagePrefixTextField.setLayoutData(layoutForTextFields);
		packageSuffixTextField.setLayoutData(layoutForTextFields);
		
		packagePrefixTextField.setText(Preferences.getInstance().getTestPackagePrefix(javaProject));
		packageSuffixTextField.setText(Preferences.getInstance().getTestPackageSuffix(javaProject));
	}
	
	private void createSuperClassTextField(Composite parent) {
		Label superClassLabel = new Label(parent, SWT.NONE);
		superClassLabel.setText(PreferenceConstants.TEXT_TEST_SUPERCLASS);
		
		superClassTextField = new Text(parent, SWT.SINGLE | SWT.BORDER);
		superClassTextField.setLayoutData(layoutForTextFields);
		superClassTextField.setText(Preferences.getInstance().getTestSuperClass(javaProject));
	}
	
	private void createFlexibleNamingCheckbox(Composite parent) {
		flexibleNamingCheckbox = new Button(parent, SWT.CHECK);
		flexibleNamingCheckbox.setText(PreferenceConstants.TEXT_FLEXIBLE_NAMING);
		flexibleNamingCheckbox.setLayoutData(layourForOneLineControls);
		flexibleNamingCheckbox.setSelection(Preferences.getInstance().shouldUseFlexibleTestCaseNaming(javaProject));
	}
	
	public void saveProperties() {
		Preferences.getInstance().setTestType(javaProject, getSelectedTestType());
		
		Preferences.getInstance().setPrefixes(javaProject, PreferencesConverter.convertStringToArray(prefixTextField.getText()));
		Preferences.getInstance().setSuffixes(javaProject, PreferencesConverter.convertStringToArray(suffixTextField.getText()));
		
		Preferences.getInstance().setTestPackagePrefix(javaProject, packagePrefixTextField.getText());
		Preferences.getInstance().setTestPackageSuffix(javaProject, packageSuffixTextField.getText());
		
		Preferences.getInstance().setTestSuperClass(javaProject, superClassTextField.getText());
		
		Preferences.getInstance().setShouldUseFlexibleTestCaseNaming(javaProject, flexibleNamingCheckbox.getSelection());
	}
	
	private String getSelectedTestType() {
		if(junit3Button.getSelection())
			return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3;
		else if(junit4Button.getSelection())
			return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4;
		else if(testNgButton.getSelection())
			return PreferenceConstants.TEST_TYPE_VALUE_TESTNG;
		
		return StringConstants.EMPTY_STRING;
	}
}