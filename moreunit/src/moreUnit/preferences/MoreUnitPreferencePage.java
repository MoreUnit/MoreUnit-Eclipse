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
		StringFieldEditor junitDirPreferenceField = new StringFieldEditor(MoreUnitPlugin.JUNIT_PATH_PREFERENCE, "Directory for testcases", 10, getFieldEditorParent());
		BooleanFieldEditor showDialogField = new BooleanFieldEditor(MoreUnitPlugin.SHOW_REFACTORING_DIALOG, "Should ask before perform refactorings to tests either", getFieldEditorParent());
		addField(junitDirPreferenceField);
		addField(showDialogField);
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(MoreUnitPlugin.getDefault().getPreferenceStore());
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//