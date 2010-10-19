package org.moreunit.launch;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.extensionpoints.ITestLaunchSupport;
import org.moreunit.extensionpoints.TestType;
import org.moreunit.extensionpoints.ITestLaunchSupport.Cardinality;
import org.moreunit.log.LogHandler;

public class AdditionalTestLaunchShortcutProvider
{
    private static final String EXTENSION_ID = MoreUnitPlugin.PLUGIN_ID + ".testLaunchSupportAddition";

    private static class InstanceHolder
    {
        private static final AdditionalTestLaunchShortcutProvider INSTANCE = new AdditionalTestLaunchShortcutProvider();
    }

    // for testing only
    AdditionalTestLaunchShortcutProvider()
    {
    }

    public static AdditionalTestLaunchShortcutProvider getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    public boolean isShortcutFor(final String testType, final Class< ? extends IJavaElement> elementType, final Cardinality cardinality)
    {
        return null != getShorcutFor(testType, elementType, cardinality);
    }

    public ILaunchShortcut getShorcutFor(final String testType, final Class< ? extends IJavaElement> elementType, final int elementCount)
    {
        return getShorcutFor(testType, elementType, Cardinality.fromElementCount(elementCount));
    }

    /**
     * Returns first additional shortcut that supports the given test launch
     * situation.
     */
    public ILaunchShortcut getShorcutFor(final String testType, final Class< ? extends IJavaElement> elementType, final Cardinality cardinality)
    {
        Set<ITestLaunchSupport> supports = getTestLaunchSupports();

        final Set<ILaunchShortcut> container = new HashSet<ILaunchShortcut>(1);

        for (final ITestLaunchSupport support : supports)
        {
            if(! container.isEmpty())
            {
                break;
            }

            SafeRunner.run(new ISafeRunnable()
            {
                public void handleException(Throwable throwable)
                {
                    LogHandler.getInstance().handleExceptionLog("Error running extension: " + support.getClass(), throwable);
                }

                public void run() throws Exception
                {
                    LogHandler.getInstance().handleInfoLog("Running extension: " + support.getClass() + ".getShortcut()");
                    if(support.isLaunchSupported(TestType.fromPreferenceConstant(testType), elementType, cardinality))
                    {
                        container.add(support.getShortcut());
                    }
                }
            });
        }

        return container.isEmpty() ? null : container.iterator().next();
    }

    private Set<ITestLaunchSupport> getTestLaunchSupports()
    {
        IConfigurationElement[] configurations = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);

        Set<ITestLaunchSupport> supports = new HashSet<ITestLaunchSupport>();
        for (IConfigurationElement configuration : configurations)
        {
            Object extension = null;
            try
            {
                extension = configuration.createExecutableExtension("class");
            }
            catch (CoreException e)
            {
                LogHandler.getInstance().handleWarnLog("Error in extension point " + EXTENSION_ID + ": " + e.getMessage());
                continue;
            }

            if(! (extension instanceof ITestLaunchSupport))
            {
                LogHandler.getInstance().handleWarnLog("Extension point " + EXTENSION_ID + " does not support class: " + extension.getClass());
                continue;
            }

            supports.add((ITestLaunchSupport) extension);
        }

        return supports;
    }
}
