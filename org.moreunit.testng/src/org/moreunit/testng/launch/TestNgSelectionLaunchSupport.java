package org.moreunit.testng.launch;

import java.util.Collection;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IMember;
import org.moreunit.launch.TestLaunchSupport;

public class TestNgSelectionLaunchSupport implements TestLaunchSupport
{

    public boolean isLaunchSupported(TestType testType, Collection< ? extends IMember> testMembers)
    {
        return TestType.TESTNG == testType && testMembers.size() > 1;
    }

    public ILaunchShortcut getShortcut()
    {
        return new TestNgSelectionLaunchShortcut();
    }

}
