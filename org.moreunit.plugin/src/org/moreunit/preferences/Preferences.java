package org.moreunit.preferences;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.log.LogHandler;

import com.bdaum.overlayPages.PropertyStore;

public class Preferences {

	private static Map<IJavaProject, Preferences> preferenceMap = new HashMap<IJavaProject, Preferences>();

	public static synchronized Preferences newInstance(final IJavaProject currentProject) {
		Preferences result = Preferences.preferenceMap.get(currentProject);
		if (result != null) {
			return result;
		}
		try {
			result = new Preferences(currentProject.getCorrespondingResource());
			Preferences.preferenceMap.put(currentProject, result);
			return result;
		} catch (JavaModelException e) {
			LogHandler.getInstance().handleExceptionLog(e);
			//should never appear, if so we can'T handle it so re throw
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private IPreferenceStore preferenceStore = null;
	private IPreferenceStore detailPreferences = null;

	protected Preferences() {
		super();
	}

	protected Preferences(final IResource currentProject) {
		IPreferenceStore workbenchStore = MoreUnitPlugin.getDefault().getPreferenceStore();
		workbenchStore.setDefault(PreferenceConstants.PREF_JUNIT_PATH, PreferenceConstants.PREF_JUNIT_PATH_DEFAULT);
		workbenchStore.setDefault(PreferenceConstants.TEST_TYPE, PreferenceConstants.DEFAULT_TEST_TYPE);
		workbenchStore.setDefault(PreferenceConstants.SHOW_REFACTORING_DIALOG, true);

		workbenchStore.setDefault(PreferenceConstants.PREFIXES, PreferenceConstants.DEFAULT_PRAEFIX);
		workbenchStore.setDefault(PreferenceConstants.SUFFIXES, PreferenceConstants.DEFAULT_SUFFIX);
		workbenchStore.setDefault(PreferenceConstants.USE_WIZARDS, PreferenceConstants.DEFAULT_USE_WIZARDS);
		workbenchStore.setDefault(PreferenceConstants.SWITCH_TO_MATCHING_METHOD, PreferenceConstants.DEFAULT_SWITCH_TO_MATCHING_METHOD);
		workbenchStore.setDefault(PreferenceConstants.TEST_PACKAGE_PREFIX, PreferenceConstants.DEFAULT_TEST_PACKAGE_PREFIX);
		workbenchStore.setDefault(PreferenceConstants.FLEXIBEL_TESTCASE_NAMING, PreferenceConstants.DEFAULT_FLEXIBLE_TESTCASE_NAMING);
		workbenchStore.setDefault(PreferenceConstants.TEST_SUPERCLASS, PreferenceConstants.DEFAULT_TEST_SUPERCLASS);
		this.preferenceStore = new PropertyStore(
				currentProject,
				workbenchStore,
				PreferenceConstants.PREF_PAGE_ID);
		this.detailPreferences = new PropertyStore(
				currentProject,
				workbenchStore,
				PreferenceConstants.PREF_DETAILS_PAGE_ID);
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
	
	public String getTestSuperClass() {
		return getStringValue(PreferenceConstants.TEST_SUPERCLASS, detailPreferences);
	}
	
	private String getStringValue(final String key) {
		return getStringValue(key, preferenceStore);
	}
	
	private String getStringValue(final String key, IPreferenceStore store) {
		if(store.contains(key)) {
			return store.getString(key);
		}
		return store.getDefaultString(key);
	}

	public String getTestType() {
		if(store().contains(PreferenceConstants.TEST_TYPE)) {
			return store().getString(PreferenceConstants.TEST_TYPE);
		}
		return store().getDefaultString(PreferenceConstants.DEFAULT_TEST_TYPE);
	}

	public boolean shouldUseJunit4Type() {
		if(store().contains(PreferenceConstants.TEST_TYPE)) {
			return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4.equals(store().getString(PreferenceConstants.TEST_TYPE));
		}
		return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4.equals(PreferenceConstants.DEFAULT_TEST_TYPE);
	}

	public boolean shouldUseJunit3Type() {
		if(store().contains(PreferenceConstants.TEST_TYPE)) {
			return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3.equals(store().getString(PreferenceConstants.TEST_TYPE));
		}
		return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3.equals(PreferenceConstants.DEFAULT_TEST_TYPE);
	}

	public boolean shouldUseTestNgType() {
		if(store().contains(PreferenceConstants.TEST_TYPE)) {
			return PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(store().getString(PreferenceConstants.TEST_TYPE));
		}
		return PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(PreferenceConstants.DEFAULT_TEST_TYPE);
	}

	public boolean shoulUseFlexibleTestCaseNaming() {
		if(store().contains(PreferenceConstants.FLEXIBEL_TESTCASE_NAMING)) {
			return store().getBoolean(PreferenceConstants.FLEXIBEL_TESTCASE_NAMING);
		}
		return store().getDefaultBoolean(PreferenceConstants.FLEXIBEL_TESTCASE_NAMING);
	}

	public String getTestPackagePrefix() {
		return getStringValue(PreferenceConstants.TEST_PACKAGE_PREFIX, detailPreferences);
	}

	public String getTestPackageSuffix() {
		return getStringValue(PreferenceConstants.TEST_PACKAGE_SUFFIX, detailPreferences);
	}

	private String[] getValues(final String listPreference) {
		if (store().contains(listPreference)) {
			String prefValue = store().getString(listPreference);
			if((prefValue == null) || (prefValue.length() == 0)) {
				return new String[0];
			}
			return prefValue.split(",");
		}
		return store().getDefaultString(listPreference).split(",");
	}

	private IPreferenceStore store() {
		return this.preferenceStore;
	}
	
	public static void clearProjectCach() {
		synchronized (preferenceMap) {
			preferenceMap.clear();
		}
	}

}

// $Log: not supported by cvs2svn $
// Revision 1.5  2007/11/19 21:01:20  gianasista
// Patch from Bjoern: project specific settings
//
// Revision 1.4  2007/08/12 17:10:09  gianasista
// Refactoring: Test method creation
//
// Revision 1.3  2007/01/24 20:12:20  gianasista
// Property for felxible testcase matching
//
// Revision 1.2  2006/10/08 17:27:22  gianasista
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
