package org.moreunit.preferences;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.log.LogHandler;
import org.moreunit.util.BaseTools;

import com.bdaum.overlayPages.PropertyStore;

public class Preferences {

	private static final String[] LOOKUP_ORDER = { ProjectScope.SCOPE, ConfigurationScope.SCOPE };
	private static Map<IJavaProject, IPreferenceStore> preferenceMap = new HashMap<IJavaProject, IPreferenceStore>();

	private static final IPreferenceStore workbenchStore = MoreUnitPlugin.getDefault().getPreferenceStore();
	private static final IScopeContext workbenchScopeContext = new ConfigurationScope();
	private static Map<IJavaProject, ProjectScope> preferenceScopeMap = new HashMap<IJavaProject, ProjectScope>();
	
	private static Preferences instance = new Preferences();
	
	static {
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
	}
	
	public static Preferences getInstance() {
		return instance;
	}
	
	/**
	 * Necessary for easier testing
	 */
	public static void setInstance(Preferences preferences) {
		instance = preferences;
	}
	
	/*
	public static boolean hasProjectSpecificSettings(IJavaProject javaProject) {
		if(javaProject == null)
			return false;
		
		try {
			String propertyValue = javaProject.getProject().getPersistentProperty(PreferenceConstants.USE_PROJECT_SPECIFIC_SETTINGS);
			if(BaseTools.isStringTrimmedEmpty(propertyValue))
				return false;
			
			return Boolean.parseBoolean(propertyValue);
		} catch (CoreException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
		
		return false;
	}
	*/
	
	public boolean hasProjectSpecificSettings(IJavaProject javaProject) {
		if(javaProject == null)
			return false;
		
		return store(javaProject).getBoolean(PreferenceConstants.USE_PROJECT_SPECIFIC_SETTINGS);
	}
	
	public void setHasProjectSpecificSettings(IJavaProject javaProject, boolean hasProjectSpecificSettings) {
		store(javaProject).setValue(PreferenceConstants.USE_PROJECT_SPECIFIC_SETTINGS, hasProjectSpecificSettings);
	}
	
	public void setMappingList(IJavaProject javaProject, List<SourceFolderMapping> mappingList) {
		store(javaProject).setValue(PreferenceConstants.UNIT_SOURCE_FOLDER, PreferencesConverter.convertSourceMappingsToString(mappingList));
	}
	
	public List<SourceFolderMapping> getSourceMappingList(IJavaProject javaProject) {
		String mappingString = store(javaProject).getString(PreferenceConstants.UNIT_SOURCE_FOLDER);
		return PreferencesConverter.convertStringToSourceMappingList(mappingString);
	}

	protected Preferences() {
		super();
	}

	public String getJunitDirectoryFromPreferences(IJavaProject javaProject) {
		return store(javaProject).getString(PreferenceConstants.PREF_JUNIT_PATH);
	}

	public String[] getPrefixes(IJavaProject javaProject) {
		String preferenceValue = store(javaProject).getString(PreferenceConstants.PREFIXES);
		return PreferencesConverter.convertStringToArray(preferenceValue);
	}
	
	public void setPrefixes(IJavaProject javaProject, String[] prefixes) {
		store(javaProject).setValue(PreferenceConstants.PREFIXES, PreferencesConverter.convertArrayToString(prefixes));
	}

	public String[] getSuffixes(IJavaProject javaProject) {
		String preferenceValue = store(javaProject).getString(PreferenceConstants.SUFFIXES);
		return PreferencesConverter.convertStringToArray(preferenceValue);
	}
	
	public void setSuffixes(IJavaProject javaProject, String[] suffixes) {
		store(javaProject).setValue(PreferenceConstants.SUFFIXES, PreferencesConverter.convertArrayToString(suffixes));
	}

	public String getTestSuperClass(IJavaProject javaProject) {
		return getStringValue(PreferenceConstants.TEST_SUPERCLASS, javaProject);
	}
	
	public void setTestSuperClass(IJavaProject javaProject, String testSuperClass) {
		store(javaProject).setValue(PreferenceConstants.TEST_SUPERCLASS, testSuperClass);
	}
	
	private String getStringValue(final String key, IJavaProject javaProject) {
		if(store(javaProject).contains(key)) {
			return store(javaProject).getString(key);
		}
		return store(javaProject).getDefaultString(key);
	}

	public String getTestType(IJavaProject javaProject) {
		if(store(javaProject).contains(PreferenceConstants.TEST_TYPE)) {
			return store(javaProject).getString(PreferenceConstants.TEST_TYPE);
		}
		return store(javaProject).getDefaultString(PreferenceConstants.DEFAULT_TEST_TYPE);
	}
	
	public void setTestType(IJavaProject javaProject, String testType) {
		store(javaProject).setValue(PreferenceConstants.TEST_TYPE, testType);
	}

	public boolean shouldUseJunit4Type(IJavaProject javaProject) {
		if(store(javaProject).contains(PreferenceConstants.TEST_TYPE)) {
			return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4.equals(store(javaProject).getString(PreferenceConstants.TEST_TYPE));
		}
		return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4.equals(PreferenceConstants.DEFAULT_TEST_TYPE);
	}

	public boolean shouldUseJunit3Type(IJavaProject javaProject) {
		if(store(javaProject).contains(PreferenceConstants.TEST_TYPE)) {
			return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3.equals(store(javaProject).getString(PreferenceConstants.TEST_TYPE));
		}
		return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3.equals(PreferenceConstants.DEFAULT_TEST_TYPE);
	}

	public boolean shouldUseTestNgType(IJavaProject javaProject) {
		if(store(javaProject).contains(PreferenceConstants.TEST_TYPE)) {
			return PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(store(javaProject).getString(PreferenceConstants.TEST_TYPE));
		}
		return PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(PreferenceConstants.DEFAULT_TEST_TYPE);
	}

	public boolean shouldUseFlexibleTestCaseNaming(IJavaProject javaProject) {
		if(store(javaProject).contains(PreferenceConstants.FLEXIBEL_TESTCASE_NAMING)) {
			return store(javaProject).getBoolean(PreferenceConstants.FLEXIBEL_TESTCASE_NAMING);
		}
		return store(javaProject).getDefaultBoolean(PreferenceConstants.FLEXIBEL_TESTCASE_NAMING);
	}
	
	public void setShouldUseFlexibleTestCaseNaming(IJavaProject javaProject, boolean shouldUseFlexibleNaming) {
		store(javaProject).setValue(PreferenceConstants.FLEXIBEL_TESTCASE_NAMING, shouldUseFlexibleNaming);
	}

	public String getTestPackagePrefix(IJavaProject javaProject) {
		return getStringValue(PreferenceConstants.TEST_PACKAGE_PREFIX, javaProject);
	}
	
	public void setTestPackagePrefix(IJavaProject javaProject, String packagePrefix) {
		store(javaProject).setValue(PreferenceConstants.TEST_PACKAGE_PREFIX, packagePrefix);
	}

	public String getTestPackageSuffix(IJavaProject javaProject) {
		return getStringValue(PreferenceConstants.TEST_PACKAGE_SUFFIX, javaProject);
	}
	
	public void setTestPackageSuffix(IJavaProject javaProject, String packageSuffix) {
		store(javaProject).setValue(PreferenceConstants.TEST_PACKAGE_SUFFIX, packageSuffix);
	}

	public IPreferenceStore store(IJavaProject javaProject) {
		IPreferenceStore resultStore = null;
		if(preferenceMap.containsKey(javaProject)) {
			resultStore =  preferenceMap.get(javaProject);
		} else {
			ProjectScope projectScopeContext = new ProjectScope(javaProject.getProject());
			ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(projectScopeContext, MoreUnitPlugin.PLUGIN_ID);
			preferenceStore.setSearchContexts( new IScopeContext[] { projectScopeContext });
			preferenceMap.put(javaProject, preferenceStore);
			
			resultStore = preferenceStore;
		}
		
		if(resultStore.getBoolean(PreferenceConstants.USE_PROJECT_SPECIFIC_SETTINGS))
			return resultStore;
		
		return workbenchStore;
	}
	
	public static void clearProjectCach() {
		synchronized (preferenceMap) {
			preferenceMap.clear();
		}
	}
	
	/**
	 * Tries to get the sourcefolder for the unittests.
	 * If there are several projects for the tests in the project specific settings,
	 * this method chooses the first project from the settings.
	 */
	public IPackageFragmentRoot getJUnitSourceFolder(IJavaProject javaProject) {
		// check for project specific settings
		List<SourceFolderMapping> mappingList = Preferences.instance.getSourceMappingList(javaProject);
		if(mappingList != null && mappingList.size() > 0)
			return mappingList.get(0).getTestFolder();

		// check for workspace settings
		try {
			String junitFolder = getJunitDirectoryFromPreferences(javaProject);
			
			IPackageFragmentRoot[] packageFragmentRoots = javaProject.getPackageFragmentRoots();
			for (IPackageFragmentRoot packageFragmentRoot : packageFragmentRoots) {
				if(packageFragmentRoot.getElementName().equals(junitFolder)) {
					return packageFragmentRoot;
				}
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}

		return null;
	}

}

// $Log: not supported by cvs2svn $
// Revision 1.13  2008/03/24 18:32:59  gianasista
// Preferences with scopes
//
// Revision 1.12  2008/03/21 18:20:17  gianasista
// First version of new property page with source folder mapping
//
// Revision 1.11  2008/03/10 19:49:05  gianasista
// New property page for test source folder configuration
//
// Revision 1.10  2008/02/29 21:31:58  gianasista
// Minor refactorings
//
// Revision 1.9  2008/02/27 09:03:52  channingwalton
// corrected a misspelt method
//
// Revision 1.8  2008/02/20 19:22:25  gianasista
// Removed comments
//
// Revision 1.7  2008/02/04 20:04:32  gianasista
// Bugfix: project specific settings
//
// Revision 1.6  2008/01/23 19:32:24  gianasista
// Remove console logs
//
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
