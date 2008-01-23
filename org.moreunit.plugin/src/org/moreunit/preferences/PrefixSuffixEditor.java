package org.moreunit.preferences;

import org.eclipse.jface.preference.StringFieldEditor;

/**
 * @author vera
 *
 * 02.01.2008 21:44:58
 */
public class PrefixSuffixEditor extends StringFieldEditor {

	@Override
	protected void doLoad() {
		String preferenceValue = getPreferenceStore().getString(getPreferenceName());
		
	}
	
	@Override
	protected void doLoadDefault() {
		super.doLoadDefault();
	}
	
	@Override
	protected void doStore() {
		super.doStore();
	}
}
