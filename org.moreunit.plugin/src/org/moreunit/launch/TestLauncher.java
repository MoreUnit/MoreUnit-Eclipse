package org.moreunit.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.PreferenceConstants;

public class TestLauncher
{
    private static final String CONFIG_PROPERTY_CLASS = "class";
    private static final String JUNIT_EXTENSION_NAMESPACE_ID = "org.eclipse.jdt.junit";
    private static final String TESTNG_EXTENSION_NAMESPACE_ID = "org.testng.eclipse";

    private final String testExtensionNamespaceId;

    public TestLauncher(String testType)
    {
        if(isTestNgTestType(testType))
        {
            this.testExtensionNamespaceId = TESTNG_EXTENSION_NAMESPACE_ID;
        }
        else
        {
            this.testExtensionNamespaceId = JUNIT_EXTENSION_NAMESPACE_ID;
        }
    }

    private boolean isTestNgTestType(String testType)
    {
        return PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(testType);
    }

    public void launch(IJavaElement testElement)
    {
        ILaunchShortcut launchShortcut = getLaunchShortcut();
        if(launchShortcut == null)
        {
            LogHandler.getInstance().handleWarnLog("Launch shortcut not found: " + testExtensionNamespaceId);
        }
        else
        {
            launchShortcut.launch(createSelection(testElement), ILaunchManager.RUN_MODE);
        }
    }

    private ILaunchShortcut getLaunchShortcut()
    {
        IExtension testExtension = getTestExtension();
        if(testExtension == null)
        {
            LogHandler.getInstance().handleWarnLog("Extension not found: " + testExtensionNamespaceId);
            return null;
        }

        IConfigurationElement[] configurationElements = testExtension.getConfigurationElements();
        if(configurationElements.length == 0)
        {
            return null;
        }

        try
        {
            return (ILaunchShortcut) configurationElements[0].createExecutableExtension(CONFIG_PROPERTY_CLASS);
        }
        catch (CoreException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
            return null;
        }
    }

    private IExtension getTestExtension()
    {
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(IDebugUIConstants.PLUGIN_ID, IDebugUIConstants.EXTENSION_POINT_LAUNCH_SHORTCUTS);
        if(extensionPoint == null)
        {
            return null;
        }

        IExtension[] extensions = extensionPoint.getExtensions();
        for (int i = 0; i < extensions.length; i++)
        {
            IExtension currentExtension = extensions[i];
            if(testExtensionNamespaceId.equals(currentExtension.getNamespaceIdentifier()))
            {
                return currentExtension;
            }
        }
        return null;
    }

    protected final IStructuredSelection createSelection(IJavaElement javaElement)
    {
        return new StructuredSelection(javaElement);
    }

}
