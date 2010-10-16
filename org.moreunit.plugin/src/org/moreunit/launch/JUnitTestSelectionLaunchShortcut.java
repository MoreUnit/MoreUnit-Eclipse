package org.moreunit.launch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.junit.launcher.JUnitLaunchConfigurationDelegate;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.moreunit.log.LogHandler;

/**
 * A modified version of {@link JUnitLaunchShortcut} that can launch a
 * collection of tests (at the time of this writing, JUnitLaunchShortcut can
 * only run all tests of a project, without any chance to choose which ones).
 */
class JUnitTestSelectionLaunchShortcut extends JUnitLaunchShortcut
{

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    @SuppressWarnings("unchecked")
    @Override
    public void launch(ISelection selection, String mode)
    {
        // those cases are well supported by JUnitLaunchShortcut, we have no reason to treat them
        if(! (selection instanceof IStructuredSelection) || ((StructuredSelection) selection).size() < 2)
        {
            super.launch(selection, mode);
        }

        try
        {
            launch(((StructuredSelection) selection).toList(), mode);
        }
        catch (CoreException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }
    }

    private void launch(Collection< ? extends IMember> testsToRun, final String mode) throws CoreException
    {
        final ILaunchConfiguration config = getConfiguration(testsToRun);
        final ILaunchConfigurationDelegate delegate = getDelegate(testsToRun);

        Job job = new Job("MoreUnit")
        {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                monitor.beginTask("Launching JUnit", 100);
                try
                {
                    launch(config, delegate, mode, monitor);
                }
                catch (CoreException e)
                {
                    LogHandler.getInstance().handleExceptionLog(e);
                    return e.getStatus();
                }
                finally
                {
                    monitor.done();
                }
                return Status.OK_STATUS;
            }
        };
        job.setPriority(Job.INTERACTIVE);
        job.schedule();
    }

    private void launch(ILaunchConfiguration config, ILaunchConfigurationDelegate delegate, String mode, IProgressMonitor progressMonitor) throws CoreException
    {
        final ILaunch launch = new Launch(config, mode, null);
        launch.setAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING, getLaunchManager().getEncoding(config));
        getLaunchManager().addLaunch(launch);
        delegate.launch(config, mode, launch, progressMonitor);
    }

    private ILaunchConfiguration getConfiguration(Collection< ? extends IMember> testsToRun) throws CoreException
    {
        ILaunchConfigurationWorkingCopy wc = createDefaultLaunchConfig(testsToRun);
        ILaunchConfiguration existingConfig = findExistingLaunchConfiguration(wc);
        // create a new one if no existing found
        return existingConfig == null ? wc.doSave() : existingConfig;
    }

    private ILaunchConfigurationWorkingCopy createDefaultLaunchConfig(Collection< ? extends IMember> testsToRun) throws CoreException
    {
        IMember test = testsToRun.iterator().next();
        if(testsToRun.size() > 1)
        {
            return createLaunchConfiguration(test.getJavaProject());
        }
        else
        {
            return createLaunchConfiguration(test);
        }
    }

    private ILaunchConfiguration findExistingLaunchConfiguration(ILaunchConfigurationWorkingCopy template) throws CoreException
    {
        List<ILaunchConfiguration> candidateConfigs = findExistingLaunchConfigurations(template);
        return candidateConfigs.isEmpty() ? null : candidateConfigs.get(0);
    }

    // taken from org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut
    private List<ILaunchConfiguration> findExistingLaunchConfigurations(ILaunchConfigurationWorkingCopy temporary) throws CoreException
    {
        ILaunchConfigurationType configType = temporary.getType();

        ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(configType);
        String[] attributeToCompare = getAttributeNamesToCompare();

        ArrayList<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
        for (ILaunchConfiguration config : configs)
        {
            if(hasSameAttributes(config, temporary, attributeToCompare))
            {
                candidateConfigs.add(config);
            }
        }
        return candidateConfigs;
    }

    // taken from org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut
    private static boolean hasSameAttributes(ILaunchConfiguration config1, ILaunchConfiguration config2, String[] attributeToCompare)
    {
        try
        {
            for (String element : attributeToCompare)
            {
                String val1 = config1.getAttribute(element, EMPTY_STRING);
                String val2 = config2.getAttribute(element, EMPTY_STRING);
                if(! val1.equals(val2))
                {
                    return false;
                }
            }
            return true;
        }
        catch (CoreException e)
        {
            // ignore access problems here, return false
        }
        return false;
    }

    private ILaunchConfigurationDelegate getDelegate(final Collection< ? extends IMember> testsToRun)
    {
        return testsToRun.size() == 1 ? new JUnitLaunchConfigurationDelegate() : new JUnitTestSelectionLaunchConfigurationDelegate(testsToRun);
    }

    private ILaunchManager getLaunchManager()
    {
        return DebugPlugin.getDefault().getLaunchManager();
    }

}
