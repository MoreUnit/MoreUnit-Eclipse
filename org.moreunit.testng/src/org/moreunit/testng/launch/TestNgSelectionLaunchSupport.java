package org.moreunit.testng.launch;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.moreunit.extensionpoints.ITestLaunchSupport;

public class TestNgSelectionLaunchSupport implements ITestLaunchSupport
{

    public boolean isLaunchSupported(TestType testType, Class< ? extends IJavaElement> elementType, Cardinality cardinality)
    {
        return TestType.TESTNG == testType && IType.class.isAssignableFrom(elementType) && Cardinality.SEVERAL == cardinality;
    }

    public ILaunchShortcut getShortcut()
    {
        return new TestNgSelectionLaunchShortcut();
    }

}
