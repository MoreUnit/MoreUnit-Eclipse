/**
 * 
 */
package org.moreunit.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.log.LogHandler;

import com.bdaum.overlayPages.FieldEditorOverlayPage;

/**
 * @author vera
 *
 */
public class MoreUnitDetailPreferencePage extends FieldEditorOverlayPage implements IWorkbenchPreferencePage {
	
	public MoreUnitDetailPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		
		LogHandler.getInstance().handleInfoLog("MoreUnitDetailPreferencePage()");
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(MoreUnitPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.TEST_PACKAGE_PREFIX, "Test package prefix", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.TEST_PACKAGE_SUFFIX, "Test package suffix", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.TEST_SUPERCLASS, "Test superclass", getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(PreferenceConstants.FLEXIBEL_TESTCASE_NAMING, "Enable flexible naming of tests", getFieldEditorParent()));
	}
	
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return MoreUnitPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected String getPageId() {
		return PreferenceConstants.PREF_DETAILS_PAGE_ID;
	}

	@Override
	public boolean performOk() {
		Preferences.clearProjectCach();
		return super.performOk();
	}
	
	

}
