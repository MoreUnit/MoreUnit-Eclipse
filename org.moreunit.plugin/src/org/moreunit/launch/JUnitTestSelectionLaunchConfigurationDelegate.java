package org.moreunit.launch;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.junit.launcher.JUnitLaunchConfigurationDelegate;

/**
 * A modified version of {@link JUnitLaunchConfigurationDelegate} that can
 * launch a collection of tests (at the time of this writing,
 * JUnitLaunchConfigurationDelegate can only run all tests of a project, without
 * any chance to choose which ones).
 */
class JUnitTestSelectionLaunchConfigurationDelegate extends JUnitLaunchConfigurationDelegate
{
    private final Collection<IType> testCasesToRun;

    JUnitTestSelectionLaunchConfigurationDelegate(Collection< ? extends IMember> testsToRun)
    {
        testCasesToRun = getTestCases(testsToRun);
    }

    private Collection<IType> getTestCases(Collection< ? extends IMember> testsToRun)
    {
        Collection<IType> testCases = new LinkedHashSet<IType>();
        for (IMember testMember : testsToRun)
        {
            testCases.add(testMember instanceof IType ? (IType) testMember : testMember.getDeclaringType());
        }
        return testCases;
    }

    @Override
    protected IMember[] evaluateTests(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException
    {
        return testCasesToRun.toArray(new IMember[testCasesToRun.size()]);
    }
}
