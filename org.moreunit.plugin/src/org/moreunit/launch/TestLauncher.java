package org.moreunit.launch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.moreunit.extensionpoints.ITestLaunchSupport.Cardinality;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.PreferenceConstants;

public class TestLauncher
{
    private static final String CONFIG_PROPERTY_CLASS = "class";
    private static final String JUNIT_EXTENSION_NAMESPACE_ID = "org.eclipse.jdt.junit";
    private static final String TESTNG_EXTENSION_NAMESPACE_ID = "org.testng.eclipse";
    private static final Map<String, String> EXTENSIONS_BY_TEST_TYPE;
    static
    {
        EXTENSIONS_BY_TEST_TYPE = new HashMap<String, String>();
        EXTENSIONS_BY_TEST_TYPE.put(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3, JUNIT_EXTENSION_NAMESPACE_ID);
        EXTENSIONS_BY_TEST_TYPE.put(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, JUNIT_EXTENSION_NAMESPACE_ID);
        EXTENSIONS_BY_TEST_TYPE.put(PreferenceConstants.TEST_TYPE_VALUE_TESTNG, TESTNG_EXTENSION_NAMESPACE_ID);
    }

    private final AdditionalTestLaunchShortcutProvider additionalShortcutProvider;

    public TestLauncher()
    {
        this(AdditionalTestLaunchShortcutProvider.getInstance());
    }

    public TestLauncher(AdditionalTestLaunchShortcutProvider additionalShortcutProvider)
    {
        this.additionalShortcutProvider = additionalShortcutProvider;
    }

    public void launch(String testType, Collection< ? extends IMember> testMembers)
    {
        ILaunchShortcut launchShortcut = getLaunchShortcut(testType, testMembers);
        if(launchShortcut == null)
        {
            LogHandler.getInstance().handleWarnLog("No launch shortcut found for: (" + testType + ", " + testMembers + ")");
        }
        else
        {
            launchShortcut.launch(createSelection(testMembers), ILaunchManager.RUN_MODE);
        }
    }

    private ILaunchShortcut getLaunchShortcut(String testType, Collection< ? extends IMember> testMembers)
    {
        ILaunchShortcut shortcut = getAdditionalShortcutFromPluginExtension(testType, testMembers);
        if(shortcut != null)
        {
            return shortcut;
        }

        String testExtensionNamespaceId = EXTENSIONS_BY_TEST_TYPE.get(testType);

        // returns our own launch shortcut, capable of running a test selection
        if(testMembers.size() > 1 && JUNIT_EXTENSION_NAMESPACE_ID.equals(testExtensionNamespaceId))
        {
            return new JUnitTestSelectionLaunchShortcut();
        }

        return getShortcutFromDedicatedTestExtension(testExtensionNamespaceId);
    }

    private ILaunchShortcut getAdditionalShortcutFromPluginExtension(String testType, Collection< ? extends IMember> testMembers)
    {
        Class< ? extends IMember> memberClass = testMembers.iterator().next().getClass();
        return additionalShortcutProvider.getShorcutFor(testType, memberClass, Cardinality.fromElementCount(testMembers.size()));
    }

    private ILaunchShortcut getShortcutFromDedicatedTestExtension(String testExtensionNamespaceId)
    {
        IExtension testExtension = getTestExtension(testExtensionNamespaceId);
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

    private IExtension getTestExtension(String testExtensionNamespaceId)
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

    protected final IStructuredSelection createSelection(Collection< ? extends IMember> members)
    {
        return new StructuredSelection(new ArrayList<IMember>(members));
    }

}
