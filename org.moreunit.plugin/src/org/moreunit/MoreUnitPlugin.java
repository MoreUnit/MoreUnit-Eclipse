package org.moreunit;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.moreunit.annotation.AnnotationUpdateListener;
import org.moreunit.annotation.MoreUnitAnnotationModel;
import org.moreunit.log.LogHandler;
import org.moreunit.util.MoreUnitContants;
import org.moreunit.util.PluginTools;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MoreUnitPlugin extends AbstractUIPlugin
{

    // The shared instance.
    private static MoreUnitPlugin plugin;

    public static final String PLUGIN_ID = "org.moreunit";

    /**
     * The constructor.
     */
    public MoreUnitPlugin()
    {
        setInstance(this);
    }
    
    private static void setInstance(MoreUnitPlugin instance) {
        plugin = instance;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        PlatformUI.getWorkbench().getWorkbenchWindows()[0].getPartService().addPartListener(new AnnotationUpdateListener());
        MoreUnitAnnotationModel.attachForAllOpenEditor();
        removeMarkerFromOlderMoreUnitVersions();
    }

    /*
     * This methods should get removed some versions later. Marker could be
     * around from older versions of moreUnit (marker were declarated
     * persistent).
     */
    private void removeMarkerFromOlderMoreUnitVersions()
    {
        List<IJavaProject> javaProjectsFromWorkspace = PluginTools.getJavaProjectsFromWorkspace();
        for (IJavaProject javaProject : javaProjectsFromWorkspace)
        {
            try
            {
                javaProject.getProject().deleteMarkers(MoreUnitContants.TEST_CASE_MARKER, true, IResource.DEPTH_INFINITE);
            }
            catch (CoreException e)
            {
                LogHandler.getInstance().handleExceptionLog(e);
            }
        }
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception
    {
        super.stop(context);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     */
    public static MoreUnitPlugin getDefault()
    {
        if(plugin == null)
            LogHandler.getInstance().handleWarnLog("MoreUnitPlugin.getDefault() is null!");

        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin("org.moreunit", path);
    }
}

// $Log: not supported by cvs2svn $
// Revision 1.9  2010/02/06 21:03:39  gianasista
// Organize Imports
//
// Revision 1.8  2009/04/05 19:14:27  gianasista
// code formatter
//
// Revision 1.7 2009/03/16 20:37:32 gianasista
// Bugfix, when accessing a java project
//
// Revision 1.6 2009/02/15 17:28:09 gianasista
// annotations instead of marker
//
// Revision 1.5 2008/02/29 21:28:57 gianasista
// Marker update gets synchronized with the open editors
//
// Revision 1.4 2007/09/02 19:25:22 gianasista
// TestNG support
//
// Revision 1.3 2006/09/18 20:00:03 channingwalton
// the CVS substitions broke with my last check in because I put newlines in
// them
//
// Revision 1.2 2006/09/18 19:56:02 channingwalton
// Fixed bug [ 1537839 ] moreunit cannot find test class if it is in wrong
// package.Also found a classcast exception in UnitDecorator whicj I've guarded
// for.Fixed the Class wizard icon
//
// Revision 1.1.1.1 2006/08/13 14:31:15 gianasista
// initial
//
// Revision 1.1 2006/06/22 20:22:28 gianasista
// package rename
//
// Revision 1.1 2006/06/19 20:08:48 gianasista
// CVS Refactoring
//
// Revision 1.11 2006/06/11 20:05:41 gianasista
// Organize Imports
//
// Revision 1.10 2006/05/21 20:42:50 gianasista
// Moved initialization of preferenceStore
//
// Revision 1.9 2006/05/21 10:57:52 gianasista
// moved prefs to Preferences class
//
// Revision 1.8 2006/05/20 16:04:05 gianasista
// Integration of switchunit preferences
//
// Revision 1.7 2006/05/12 17:51:11 gianasista
// Added comments, preferences (Lists of testcase prefixes, suffixes)
//
// Revision 1.6 2006/04/30 10:20:31 gianasista
// getDefault was not null-safe
//
// Revision 1.5 2006/04/14 17:11:56 gianasista
// Suffix for testcasename ist configurable (+Tests)
//
// Revision 1.4 2006/02/19 21:48:47 gianasista
// Dialog to ask user of refactoring should be performed on corresponding tests
// (configurable via properties)
//
// Revision 1.3 2006/01/19 21:39:44 gianasista
// Added CVS-commit-logging to all java-files
//
