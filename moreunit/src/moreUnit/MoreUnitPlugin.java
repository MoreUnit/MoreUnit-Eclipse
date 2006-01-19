package moreUnit;

import moreUnit.listener.JavaCodeChangeListener;

import org.eclipse.ui.plugin.*;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MoreUnitPlugin extends AbstractUIPlugin {
	
	public static final String JUNIT_PATH_PREFERENCE = "junit_path";

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
		preferenceStore.setDefault(JUNIT_PATH_PREFERENCE, "junit");
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
		return getPreferenceStore().getString(JUNIT_PATH_PREFERENCE);
	}
}

// $Log: not supported by cvs2svn $
