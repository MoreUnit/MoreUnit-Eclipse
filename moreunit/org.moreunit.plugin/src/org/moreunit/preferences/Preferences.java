package org.moreunit.preferences;


import org.eclipse.jface.preference.IPreferenceStore;
import org.moreunit.MoreUnitPlugin;

public class Preferences {
	
	private static Preferences	INSTANCE;
	
	public static synchronized Preferences instance() {
		if (INSTANCE == null) {
			INSTANCE = new Preferences();
		}
		return INSTANCE;
	}
	
	protected Preferences() {
		IPreferenceStore preferenceStore = store();
		preferenceStore.setDefault(PreferenceConstants.PREF_JUNIT_PATH, PreferenceConstants.PREF_JUNIT_PATH_DEFAULT);
		preferenceStore.setDefault(PreferenceConstants.SHOW_REFACTORING_DIALOG, true);
		
		preferenceStore.setDefault(PreferenceConstants.PREFIXES, PreferenceConstants.DEFAULT_QUALIFIERS);
		preferenceStore.setDefault(PreferenceConstants.SUFFIXES, PreferenceConstants.DEFAULT_QUALIFIERS);
		preferenceStore.setDefault(PreferenceConstants.USE_WIZARDS, PreferenceConstants.DEFAULT_USE_WIZARDS);
		preferenceStore.setDefault(PreferenceConstants.SWITCH_TO_MATCHING_METHOD, PreferenceConstants.DEFAULT_SWITCH_TO_MATCHING_METHOD);
		preferenceStore.setDefault(PreferenceConstants.TEST_PACKAGE_PREFIX, PreferenceConstants.DEFAULT_TEST_PACKAGE_PREFIX);
		preferenceStore.setDefault(PreferenceConstants.JUNIT4_TEST_TYPE, PreferenceConstants.DEFAULT_JUNIT4_TEST_TYPE);
	}
	
	public String getJunitDirectoryFromPreferences() {
		return store().getString(PreferenceConstants.PREF_JUNIT_PATH);
	}
	
	public String[] getPrefixes() {
		return getValues(PreferenceConstants.PREFIXES);
	}

	public String[] getSuffixes() {
		return getValues(PreferenceConstants.SUFFIXES);
	}

	/*
	public boolean useClassCreationWizards() {
		if (store().contains(PreferenceConstants.USE_WIZARDS)) {
			return store().getBoolean(PreferenceConstants.USE_WIZARDS);
		}
		return store().getDefaultBoolean(PreferenceConstants.USE_WIZARDS);
	}
	*/

	/*
	public boolean switchToMatchingMethod() {
		if (store().contains(PreferenceConstants.SWITCH_TO_MATCHING_METHOD)) {
			return store().getBoolean(PreferenceConstants.SWITCH_TO_MATCHING_METHOD);
		}
		return store().getDefaultBoolean(PreferenceConstants.SWITCH_TO_MATCHING_METHOD);
	}
	*/
	
	public boolean shouldUseJunit4Type() {
		if(store().contains(PreferenceConstants.JUNIT4_TEST_TYPE)) {
			return store().getBoolean(PreferenceConstants.JUNIT4_TEST_TYPE);
		}
		return store().getDefaultBoolean(PreferenceConstants.JUNIT4_TEST_TYPE);
	}

	public String getTestPackagePrefix() {
		if (store().contains(PreferenceConstants.TEST_PACKAGE_PREFIX)) {
			return store().getString(PreferenceConstants.TEST_PACKAGE_PREFIX);
		}
		return store().getDefaultString(PreferenceConstants.TEST_PACKAGE_PREFIX);
	}

	private String[] getValues(String listPreference) {
		if (store().contains(listPreference)) {
			String prefValue = store().getString(listPreference);
			if(prefValue == null || prefValue.length() == 0)
				return new String[0];
			return prefValue.split(",");
		}
		return store().getDefaultString(listPreference).split(",");
	}

	private IPreferenceStore store() {
		return MoreUnitPlugin.getDefault().getPreferenceStore();
	}

}

// $Log: not supported by cvs2svn $
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.8  2006/06/11 20:07:45  gianasista
// Removed some prefs
//
// Revision 1.7  2006/06/10 20:31:17  gianasista
// Bugfix in getValue (problem with empty list solved)
//
// Revision 1.6  2006/05/25 19:51:01  gianasista
// JUnit4 support
//
// Revision 1.5  2006/05/21 20:43:19  gianasista
// Moved initialization of preferenceStore
//
// Revision 1.4  2006/05/21 10:59:31  gianasista
// moved prefs to Preferences class
//
// Revision 1.3  2006/05/13 18:27:37  gianasista
// Preferences as singelton (protected constructor for testing purposes)
//
// Revision 1.2  2006/05/12 22:34:36  channingwalton
// added class creation wizards if type to jump to does not exist
//
