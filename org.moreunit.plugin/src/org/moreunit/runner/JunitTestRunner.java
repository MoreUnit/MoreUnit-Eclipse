package org.moreunit.runner;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * A runner for launching JUnit tests.
 */
public class JunitTestRunner
{

    private IJavaElement testElement;

    public JunitTestRunner(IJavaElement testElement)
    {
        this.testElement = testElement;
    }

    public void runTest()
    {
        new JUnitLaunchShortcut().launch(createSelection(), ILaunchManager.RUN_MODE);
    }

    private IStructuredSelection createSelection()
    {
        return new StructuredSelection(testElement);
    }

}
