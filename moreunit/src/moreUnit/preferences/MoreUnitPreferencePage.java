package moreUnit.preferences;

import moreUnit.MoreUnitPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author vera
 * 08.01.2006 19:24:23
 */
public class MoreUnitPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage{

	public MoreUnitPreferencePage() {
		super(FieldEditorPreferencePage.FLAT);
	}
	
	protected void createFieldEditors() {
		StringFieldEditor junitDirPreferenceField = new StringFieldEditor(MoreUnitPlugin.PREF_JUNIT_PATH, "Directory for testcases", 10, getFieldEditorParent());
		StringFieldEditor testcaseSuffixPreferenceField = new StringFieldEditor(MoreUnitPlugin.PREF_TESTCASE_SUFFIX, "Suffix for testcases", 20, getFieldEditorParent());
		BooleanFieldEditor showDialogField = new BooleanFieldEditor(MoreUnitPlugin.SHOW_REFACTORING_DIALOG, "Should ask before perform refactorings to tests either", getFieldEditorParent());
		addField(junitDirPreferenceField);
		addField(testcaseSuffixPreferenceField);
		addField(showDialogField);
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(MoreUnitPlugin.getDefault().getPreferenceStore());
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.3  2006/02/19 21:46:45  gianasista
// Dialog to ask user of refactoring should be performed on corresponding tests (configurable via properties)
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//