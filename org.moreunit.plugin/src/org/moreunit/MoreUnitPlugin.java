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
import org.moreunit.util.FeatureDetector;
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

    private AnnotationUpdateListener annotationUpdateListener;

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
        FeatureDetector.setBundleContext(context);
        annotationUpdateListener = new AnnotationUpdateListener();
        PlatformUI.getWorkbench().getWorkbenchWindows()[0].getPartService().addPartListener(annotationUpdateListener);
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
        annotationUpdateListener.dispose();
        PlatformUI.getWorkbench().getWorkbenchWindows()[0].getPartService().removePartListener(annotationUpdateListener);
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