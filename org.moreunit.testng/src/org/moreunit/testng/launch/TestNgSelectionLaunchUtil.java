package org.moreunit.testng.launch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.util.Util;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.search.internal.ui.text.FileSearchQuery;
import org.eclipse.search.internal.ui.text.FileSearchResult;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.ui.PlatformUI;
import org.testng.eclipse.TestNGPlugin;
import org.testng.eclipse.TestNGPluginConstants;
import org.testng.eclipse.launch.TestNGLaunchConfigurationConstants;
import org.testng.eclipse.launch.TestNGLaunchConfigurationConstants.LaunchType;
import org.testng.eclipse.ui.RunInfo;
import org.testng.eclipse.ui.util.ConfigurationHelper;
import org.testng.eclipse.util.param.ParameterSolver;
import org.testng.internal.AnnotationTypeEnum;

/**
 * A partial copy of org.testng.eclipse.util.LaunchUtil utility class that adds
 * support for launching any selection of test case classes.
 */
public class TestNgSelectionLaunchUtil
{
    private static final List<String> EMPTY_ARRAY_PARAM = new ArrayList<String>();

    public static void launchTypesConfiguration(IJavaProject ijp, IType[] types, String mode)
    {
        launchTypeBasedConfiguration(ijp, createConfigurationName(types), types, mode);
    }

    private static String createConfigurationName(IType[] types)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.length; i++)
        {
            if(sb.length() != 0)
            {
                sb.append(", ");
            }
            sb.append(types[i].getElementName());
        }
        return sb.toString();
    }

    /* Following methods are copied from org.testng.eclipse.util.LaunchUtil */

    private static void launchTypeBasedConfiguration(IJavaProject ijp, String confName, IType[] types, String mode)
    {
        Map<String, List<String>> classMethods = new HashMap<String, List<String>>();
        List<String> typeNames = new ArrayList<String>();

        for (int i = 0; i < types.length; i++)
        {
            typeNames.add(types[i].getFullyQualifiedName());
            classMethods.put(types[i].getFullyQualifiedName(), EMPTY_ARRAY_PARAM);
        }

        if(haveGroupsDependency(types))
        {
            groupDependencyWarning(confName, null);
        }

        ILaunchConfigurationWorkingCopy workingCopy = createLaunchConfiguration(ijp.getProject(), confName, null);

        workingCopy.setAttribute(TestNGLaunchConfigurationConstants.TYPE, LaunchType.CLASS.ordinal());
        workingCopy.setAttribute(TestNGLaunchConfigurationConstants.ALL_METHODS_LIST, ConfigurationHelper.toClassMethodsMap(classMethods));
        workingCopy.setAttribute(TestNGLaunchConfigurationConstants.CLASS_TEST_LIST, typeNames);

        // constant removed in TestNG Plugin 5.14.2.4
        // workingCopy.setAttribute(TestNGLaunchConfigurationConstants.TESTNG_COMPLIANCE_LEVEL_ATTR,
        // getQuickComplianceLevel(types));
        workingCopy.setAttribute(TestNGPlugin.PLUGIN_ID + ".COMPLIANCE_LEVEL", getQuickComplianceLevel(types));

        workingCopy.setAttribute(TestNGLaunchConfigurationConstants.PARAMS, solveParameters(types));
        workingCopy.setAttribute(TestNGLaunchConfigurationConstants.METHOD_TEST_LIST, EMPTY_ARRAY_PARAM);
        workingCopy.setAttribute(TestNGLaunchConfigurationConstants.PACKAGE_TEST_LIST, EMPTY_ARRAY_PARAM);

        runConfig(workingCopy, mode);
    }

    private static boolean haveGroupsDependency(IType[] types)
    {
        ICompilationUnit[] units = new ICompilationUnit[types.length];
        for (int i = 0; i < types.length; i++)
        {
            units[i] = types[i].getCompilationUnit();
        }

        return haveGroupsDependency(units);
    }

    private static boolean haveGroupsDependency(ICompilationUnit[] units)
    {
        List<IResource> resources = new ArrayList<IResource>();
        for (int i = 0; i < units.length; i++)
        {
            try
            {
                resources.add(units[i].getCorrespondingResource());
            }
            catch (JavaModelException jmex)
            {
                ;
            }
        }
        IResource[] scopeResources = resources.toArray(new IResource[resources.size()]);
        ISearchQuery query = new FileSearchQuery("@Test\\(.*\\s*dependsOnGroups\\s*=.*", true /* regexp */, true /* casesensitive */, FileTextSearchScope.newSearchScope(scopeResources, new String[] { "*.java" }, false));
        query.run(new NullProgressMonitor());
        FileSearchResult result = (FileSearchResult) query.getSearchResult();
        Object[] elements = result.getElements();

        return elements != null && elements.length > 0;
    }

    private static void groupDependencyWarning(String elementName, Set< ? > groups)
    {
        ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "WARNING", elementName + " defines group dependencies that will be ignored. To reliably test methods with group dependencies use a suite definition.", new Status(IStatus.WARNING, TestNGPlugin.PLUGIN_ID, 3333, elementName + " uses group dependencies " + (groups != null ? groups.toString() : "") + " which due to a plugin limitation will be ignored", null));
    }

    private static ILaunchConfigurationWorkingCopy createLaunchConfiguration(IProject project, String confName, RunInfo runInfo)
    {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfiguration config = ConfigurationHelper.findConfiguration(launchManager, project, confName, runInfo);

        ILaunchConfigurationWorkingCopy configWC = null;
        if(null != config)
        {
            try
            {
                configWC = config.getWorkingCopy();
            }
            catch (CoreException cex)
            {
                TestNGPlugin.log(new Status(IStatus.ERROR, TestNGPlugin.PLUGIN_ID, TestNGPluginConstants.LAUNCH_ERROR, "Cannot create working copy of existing launcher " + config.getName(), cex));
            }
        }
        if(null == configWC)
        {
            if(confName == null && runInfo != null)
            {
                confName = runInfo.getClassName() + "." + runInfo.getMethodName();
            }
            configWC = ConfigurationHelper.createBasicConfiguration(launchManager, project, confName);
        }

        return configWC;
    }

    private static Map<String, String> solveParameters(IJavaElement[] javaElements)
    {
        @SuppressWarnings("unchecked")
        Map<String, String> result = ParameterSolver.solveParameters(javaElements);
        return result != null ? result : new HashMap<String, String>();
    }

    /**
     * Uses the Eclipse search support to look for @Test annotation and decide
     * if the compliance level should be set to JDK or JAVADOC.
     */
    private static String getQuickComplianceLevel(IType[] types)
    {
        List<IResource> resources = new ArrayList<IResource>();
        for (int i = 0; i < types.length; i++)
        {
            try
            {
                resources.add(types[i].getCompilationUnit().getCorrespondingResource());
            }
            catch (JavaModelException jmex)
            {
                ;
            }
        }
        IResource[] scopeResources = resources.toArray(new IResource[resources.size()]);
        ISearchQuery query = new FileSearchQuery("@(Test|Before|After|Factory)(\\(.+)?", true /* regexp */, true /* casesensitive */, FileTextSearchScope.newSearchScope(scopeResources, getJavaLikeExtensions(), false));
        query.run(new NullProgressMonitor());
        FileSearchResult result = (FileSearchResult) query.getSearchResult();
        Object[] elements = result.getElements();

        // constants removed in TestNG plugin 5.14.2.4
        // return elements != null && elements.length > 0 ?
        // TestNG.JDK_ANNOTATION_TYPE : TestNG.JAVADOC_ANNOTATION_TYPE;
        return elements != null && elements.length > 0 ? AnnotationTypeEnum.JDK.getName() : AnnotationTypeEnum.JAVADOC.getName();
    }

    private static String[] getJavaLikeExtensions()
    {
        char[][] exts = Util.getJavaLikeExtensions();
        if(exts != null && exts.length > 0)
        {
            String[] extStrs = new String[exts.length];
            for (int i = 0; i < exts.length; i++)
            {
                extStrs[i] = "*." + String.valueOf(exts[i]);
            }
            return extStrs;
        }
        else
        {
            return new String[] { "*.java" };
        }
    }

    private static void runConfig(ILaunchConfigurationWorkingCopy launchConfiguration, String runMode)
    {
        ILaunchConfiguration conf = save(launchConfiguration);

        if(null != conf)
        {
            // try {
            // Map attrs= conf.getAttributes();
            // System.out.println("Launch attrs:" + attrs);
            // }
            // catch(CoreException cex) { ; }

            DebugUITools.launch(conf, runMode);
        }
    }

    private static ILaunchConfiguration save(ILaunchConfigurationWorkingCopy launchWorkingCopy)
    {
        if(null == launchWorkingCopy)
            return null;

        try
        {
            return launchWorkingCopy.doSave();
        }
        catch (CoreException cex)
        {
            TestNGPlugin.log(cex);
        }

        return null;
    }

}
