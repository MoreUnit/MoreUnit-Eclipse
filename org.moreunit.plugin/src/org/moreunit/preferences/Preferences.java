package org.moreunit.preferences;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.log.LogHandler;
import org.moreunit.properties.ProjectProperties;
import org.moreunit.util.BaseTools;
import org.moreunit.util.PluginTools;

import com.bdaum.overlayPages.FieldEditorOverlayPage;
import com.bdaum.overlayPages.PropertyStore;

public class Preferences {

	private static Map<IJavaProject, IPreferenceStore> preferenceMap = new HashMap<IJavaProject, IPreferenceStore>();

	private static final IPreferenceStore workbenchStore = MoreUnitPlugin.getDefault().getPreferenceStore();
	
	private static final Preferences instance = new Preferences();
	
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
	
	private static boolean hasProjectSpecificSettings(IJavaProject javaProject) {
		if(javaProject == null)
			return false;
		
		try {
			String propertyValue = javaProject.getProject().getPersistentProperty(getQualifiedNameForKey(FieldEditorOverlayPage.USEPROJECTSETTINGS));
			if(BaseTools.isStringTrimmedEmpty(propertyValue))
				return false;
			
			return Boolean.parseBoolean(propertyValue);
		} catch (CoreException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
		
		return false;
	}
	
	public void setTestSourceFolder(IJavaProject javaProject, List<IPackageFragmentRoot> testSourceFolderList) {
		store(javaProject).setValue(PreferenceConstants.UNIT_SOURCE_FOLDER, PluginTools.convertSourceFoldersToString(testSourceFolderList));
	}
	
	public List<IPackageFragmentRoot> getTestSourceFolder(IJavaProject javaProject) {
		String sourceFolderString = store(javaProject).getString(PreferenceConstants.UNIT_SOURCE_FOLDER);
		return PluginTools.convertStringToSourceFolderList(sourceFolderString);
	}
	
	private static QualifiedName getQualifiedNameForKey(String key) {
		return new QualifiedName(PreferenceConstants.PREF_PAGE_ID, key);
	}

	protected Preferences() {
		super();
	}

	public String getJunitDirectoryFromPreferences(IJavaProject javaProject) {
		return store(javaProject).getString(PreferenceConstants.PREF_JUNIT_PATH);
	}

	public String[] getPrefixes(IJavaProject javaProject) {
		return getValues(PreferenceConstants.PREFIXES, javaProject);
	}

	public String[] getSuffixes(IJavaProject javaProject) {
		return getValues(PreferenceConstants.SUFFIXES, javaProject);
	}

	public String getTestSuperClass(IJavaProject javaProject) {
		return getStringValue(PreferenceConstants.TEST_SUPERCLASS, javaProject);
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

	public String getTestPackagePrefix(IJavaProject javaProject) {
		return getStringValue(PreferenceConstants.TEST_PACKAGE_PREFIX, javaProject);
	}

	public String getTestPackageSuffix(IJavaProject javaProject) {
		return getStringValue(PreferenceConstants.TEST_PACKAGE_SUFFIX, javaProject);
	}

	private String[] getValues(final String listPreference, IJavaProject javaProject) {
		if (store(javaProject).contains(listPreference)) {
			String prefValue = store(javaProject).getString(listPreference);
			if((prefValue == null) || (prefValue.length() == 0)) {
				return new String[0];
			}
			return prefValue.split(",");
		}
		return store(javaProject).getDefaultString(listPreference).split(",");
	}

	private IPreferenceStore store(IJavaProject javaProject) {
		if(!hasProjectSpecificSettings(javaProject))
			return MoreUnitPlugin.getDefault().getPreferenceStore();
		
		return getProjectSpecificStore(javaProject);
	}
	
	private IPreferenceStore getProjectSpecificStore(IJavaProject javaProject) {
		IPreferenceStore result = Preferences.preferenceMap.get(javaProject);
		if(result != null)
			return result;
		
		result = new PropertyStore(javaProject.getProject(), workbenchStore, PreferenceConstants.PREF_PAGE_ID);
		preferenceMap.put(javaProject, result);
		
		return result;
		
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
		List<IPackageFragmentRoot> unitSourceFolderList = Preferences.instance.getTestSourceFolder(javaProject);
		if(unitSourceFolderList != null && unitSourceFolderList.size() > 0)
			return unitSourceFolderList.get(0);

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
