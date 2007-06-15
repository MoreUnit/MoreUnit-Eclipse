/**
 * 
 */
package org.moreunit.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.log.LogHandler;

/**
 * @author vera
 *
 */
public class MoreUnitDetailPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public MoreUnitDetailPreferencePage() {
		super(FieldEditorPreferencePage.FLAT);
		
		LogHandler.getInstance().handleInfoLog("MoreUnitDetailPreferencePage()");
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(MoreUnitPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.PREF_CREATE_TEST_NG, "Create TestNG tests", getFieldEditorParent()));
	}

}
