package moreUnit;

import moreUnit.listener.JavaCodeChangeListener;
import moreUnit.log.LogHandler;
import moreUnit.preferences.PreferenceConstants;

import org.eclipse.ui.plugin.*;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MoreUnitPlugin extends AbstractUIPlugin {
	
	public static final String PREF_JUNIT_PATH = "junit_path";
	public static final String PREF_JUNIT_PATH_DEFAULT = "junit";
	public static final String SHOW_REFACTORING_DIALOG = "show_refactoring_dialog";

	//The shared instance.
	private static MoreUnitPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public MoreUnitPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		JavaCore.addElementChangedListener(new JavaCodeChangeListener());
		
		IPreferenceStore preferenceStore = getPreferenceStore();
		preferenceStore.setDefault(PREF_JUNIT_PATH, PREF_JUNIT_PATH_DEFAULT);
		preferenceStore.setDefault(SHOW_REFACTORING_DIALOG, true);
		
		preferenceStore.setDefault(PreferenceConstants.PREFIXES, PreferenceConstants.DEFAULT_QUALIFIERS);
		preferenceStore.setDefault(PreferenceConstants.SUFFIXES, PreferenceConstants.DEFAULT_QUALIFIERS);
		preferenceStore.setDefault(PreferenceConstants.USE_WIZARDS, PreferenceConstants.DEFAULT_USE_WIZARDS);
		preferenceStore.setDefault(PreferenceConstants.SWITCH_TO_MATCHING_METHOD, PreferenceConstants.DEFAULT_SWITCH_TO_MATCHING_METHOD);
		preferenceStore.setDefault(PreferenceConstants.TEST_PACKAGE_PREFIX, PreferenceConstants.DEFAULT_TEST_PACKAGE_PREFIX);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MoreUnitPlugin getDefault() {
		if(plugin == null)
			LogHandler.getInstance().handleWarnLog("MoreUnitPlugin.getDefault() is null!");
		
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("moreUnit", path);
	}
	
	public String getJunitDirectoryFromPreferences() {
		return getPreferenceStore().getString(PREF_JUNIT_PATH);
	}
	
	public boolean getShowRefactoringDialogFromPreferences() {
		return getPreferenceStore().getBoolean(SHOW_REFACTORING_DIALOG);
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.7  2006/05/12 17:51:11  gianasista
// Added comments, preferences (Lists of testcase prefixes, suffixes)
//
// Revision 1.6  2006/04/30 10:20:31  gianasista
// getDefault was not null-safe
//
// Revision 1.5  2006/04/14 17:11:56  gianasista
// Suffix for testcasename ist configurable (+Tests)
//
// Revision 1.4  2006/02/19 21:48:47  gianasista
// Dialog to ask user of refactoring should be performed on corresponding tests (configurable via properties)
//
// Revision 1.3  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//
