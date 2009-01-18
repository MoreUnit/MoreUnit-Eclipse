package org.moreunit.preferences;



import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.SourceFolderContext;
import org.moreunit.properties.OtherMoreunitPropertiesBlock;
import org.moreunit.util.SearchScopeSingelton;

/**
 * @author vera
 * 08.01.2006 19:24:23
 */
public class MoreUnitPreferencePage extends PreferencePage implements IWorkbenchPreferencePage{

	private Text testSourceFolderField;
	private OtherMoreunitPropertiesBlock otherMoreunitPropertiesBlock;

	public MoreUnitPreferencePage() {
		//super(FieldEditorPreferencePage.GRID);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite contentComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, true);
		contentComposite.setLayout(gridLayout);
		otherMoreunitPropertiesBlock = new OtherMoreunitPropertiesBlock(null);
		
		createTestSourceFolderField(contentComposite);
		otherMoreunitPropertiesBlock.getControl(contentComposite);
		
		return parent;
	}
	
	private void createTestSourceFolderField(Composite parent) {
		Composite labelAndTextFieldComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, true);
		labelAndTextFieldComposite.setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.heightHint = 30;
		labelAndTextFieldComposite.setLayoutData(gridData);

		Label label = new Label(labelAndTextFieldComposite, SWT.NONE);
		label.setText(PreferenceConstants.TEXT_TEST_SOURCE_FOLDER);

		testSourceFolderField = new Text(labelAndTextFieldComposite, SWT.SINGLE | SWT.BORDER);
		testSourceFolderField.setLayoutData(otherMoreunitPropertiesBlock.getLayoutForTextFields());
		testSourceFolderField.setText(Preferences.getInstance().getJunitDirectoryFromPreferences(null));
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(MoreUnitPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return MoreUnitPlugin.getDefault().getPreferenceStore();
	}
	
	public boolean performOk() {
		Preferences.getInstance().setJunitDirectory(testSourceFolderField.getText());
		otherMoreunitPropertiesBlock.saveProperties();
		Preferences.clearProjectCach();
		
		SourceFolderContext.getInstance().initContextForWorkspace();
		SearchScopeSingelton.getInstance().resetCachedSearchScopes();
		
		return super.performOk();
	}
	
	/*
	@Override
	protected void createFieldEditors() {
		StringFieldEditor junitDirPreferenceField = new StringFieldEditor(PreferenceConstants.PREF_JUNIT_PATH, "Directory for testcases", 10, getFieldEditorParent());
		addField(junitDirPreferenceField);

		String[][] labelAndValues = new String[][] {
				{PreferenceConstants.TEXT_JUNIT_3_8, PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3},
				{PreferenceConstants.TEXT_JUNIT_4, PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4},
				{PreferenceConstants.TEXT_TEST_NG, PreferenceConstants.TEST_TYPE_VALUE_TESTNG}
		};
		addField(new RadioGroupFieldEditor(PreferenceConstants.TEST_TYPE, PreferenceConstants.TEXT_TEST_TYPE, 1, labelAndValues, getFieldEditorParent()));

		addField(new StringFieldEditor(PreferenceConstants.PREFIXES, PreferenceConstants.TEXT_TEST_PREFIXES, 30, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.SUFFIXES, PreferenceConstants.TEXT_TEST_SUFFIXES, 30, getFieldEditorParent()));
		
		addField(new StringFieldEditor(PreferenceConstants.TEST_PACKAGE_PREFIX, PreferenceConstants.TEXT_PACKAGE_PREFIX, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.TEST_PACKAGE_SUFFIX, PreferenceConstants.TEXT_PACKAGE_SUFFIX, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.TEST_SUPERCLASS, PreferenceConstants.TEXT_TEST_SUPERCLASS, getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(PreferenceConstants.FLEXIBEL_TESTCASE_NAMING, PreferenceConstants.TEXT_FLEXIBLE_NAMING, getFieldEditorParent()));
		String[][] testMethodLabelsAndValues = new String[][] {
				{"Test Methods Prefixed With \"test\"",PreferenceConstants.TEST_METHOD_TYPE_JUNIT3},
				{"Test Methods With no Prefix",PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX}
		};
		addField(new RadioGroupFieldEditor(PreferenceConstants.TEST_METHOD_TYPE, "Test Method Type", 1, testMethodLabelsAndValues, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.TEST_METHOD_DEFAULT_CONTENT, "Test Method Default Content", 30, getFieldEditorParent()));
	}

	public void init(final IWorkbench workbench) {
		setPreferenceStore(MoreUnitPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return MoreUnitPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected String getPageId() {
		return PreferenceConstants.PREF_PAGE_ID;
	}

	@Override
	public boolean performOk() {
		Preferences.clearProjectCach();
		
		SourceFolderContext.getInstance().initContextForWorkspace();
		SearchScopeSingelton.getInstance().resetCachedSearchScopes();
		
		return super.performOk();
	}
	*/
	
	
}

// $Log: not supported by cvs2svn $
// Revision 1.9  2009/01/15 19:06:53  gianasista
// Patch from Zach: configurable content for test method
//
// Revision 1.8  2009/01/08 19:58:21  gianasista
// Patch from Zach for more flexible test method naming
//
// Revision 1.7  2008/03/21 18:20:17  gianasista
// First version of new property page with source folder mapping
//
// Revision 1.6  2008/01/23 19:31:32  gianasista
// Project specific settings
//
// Revision 1.5  2007/11/19 21:00:38  gianasista
// Patch from Bjoern: project specific settings
//
// Revision 1.4  2007/08/12 17:10:11  gianasista
// Refactoring: Test method creation
//
// Revision 1.3  2007/01/24 20:12:21  gianasista
// Property for felxible testcase matching
//
// Revision 1.2  2006/10/08 17:37:24  gianasista
// Suffix preference
//
// Revision 1.1.1.1  2006/08/13 14:31:16  gianasista
// initial
//
// Revision 1.1  2006/06/22 20:22:28  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.10  2006/06/11 20:07:11  gianasista
// Removed some prefs
//
// Revision 1.9  2006/06/03 14:45:57  gianasista
// StringListEditor (extends ListEditor) as an alternative for NameListEditor?
//
// Revision 1.8  2006/05/25 19:51:01  gianasista
// JUnit4 support
//
// Revision 1.7  2006/05/21 10:58:44  gianasista
// moved prefs to Preferences class
//
// Revision 1.6  2006/05/20 16:09:40  gianasista
// Integration of switchunit preferences
//
// Revision 1.5  2006/05/12 17:53:41  gianasista
// started extended preferences (Lists of testcase prefixes, suffixes)
//
// Revision 1.4  2006/04/14 17:11:56  gianasista
// Suffix for testcasename ist configurable (+Tests)
//
// Revision 1.3  2006/02/19 21:46:45  gianasista
// Dialog to ask user of refactoring should be performed on corresponding tests (configurable via properties)
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//