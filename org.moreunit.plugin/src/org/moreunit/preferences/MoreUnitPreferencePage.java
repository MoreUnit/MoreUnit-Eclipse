package org.moreunit.preferences;



import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.moreunit.MoreUnitPlugin;

import com.bdaum.overlayPages.FieldEditorOverlayPage;

/**
 * @author vera
 * 08.01.2006 19:24:23
 */
public class MoreUnitPreferencePage extends FieldEditorOverlayPage implements IWorkbenchPreferencePage{

	public MoreUnitPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	protected void createFieldEditors() {
		StringFieldEditor junitDirPreferenceField = new StringFieldEditor(PreferenceConstants.PREF_JUNIT_PATH, "Directory for testcases", 10, getFieldEditorParent());
		addField(junitDirPreferenceField);

		String[][] labelAndValues = new String[][] {
				{"JUnit 3.8", PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3},
				{"Junit 4", PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4},
				{"TestNG", PreferenceConstants.TEST_TYPE_VALUE_TESTNG}
		};
		addField(new RadioGroupFieldEditor(PreferenceConstants.TEST_TYPE, "Test Type", 1, labelAndValues, getFieldEditorParent()));

		addField(new StringListEditor(PreferenceConstants.PREFIXES, "Unit Test &Prefixes:", getFieldEditorParent()));
		addField(new StringListEditor(PreferenceConstants.SUFFIXES, "Unit Test &Suffixes:", getFieldEditorParent()));
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
		return super.performOk();
	}
	
	
}

// $Log: not supported by cvs2svn $
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