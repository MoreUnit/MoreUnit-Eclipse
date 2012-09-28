package org.moreunit;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.moreunit.annotation.AnnotationUpdateListener;
import org.moreunit.annotation.MoreUnitAnnotationModel;
import org.moreunit.core.log.DefaultLogger;
import org.moreunit.core.log.Logger;
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

    private static final String LOG_LEVEL_PROPERTY = "org.moreunit.log.level";

    private Logger logger;
    private AnnotationUpdateListener annotationUpdateListener;

    /**
     * The constructor.
     */
    public MoreUnitPlugin()
    {
        setInstance(this);
    }

    private static void setInstance(MoreUnitPlugin instance)
    {
        plugin = instance;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception
    {
        super.start(context);

        logger = new DefaultLogger(getLog(), PLUGIN_ID, LOG_LEVEL_PROPERTY);

        FeatureDetector.setBundleContext(context);
        annotationUpdateListener = new AnnotationUpdateListener();

        IPartService partService = getPartService();
        if(partService != null)
            partService.addPartListener(annotationUpdateListener);

        MoreUnitAnnotationModel.attachForAllOpenEditor();
        removeMarkerFromOlderMoreUnitVersions();
    }

    protected IPartService getPartService()
    {
        IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
        if(workbenchWindows.length > 0)
            return workbenchWindows[0].getPartService();

        return null;
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

        IPartService partService = getPartService();
        if(partService != null)
            partService.removePartListener(annotationUpdateListener);

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

    public Logger getLogger()
    {
        return logger;
    }
}
