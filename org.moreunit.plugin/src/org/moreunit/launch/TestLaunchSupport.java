package org.moreunit.launch;

import java.util.Collection;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IMember;
import org.moreunit.preferences.PreferenceConstants;

public interface TestLaunchSupport
{

    enum TestType
    {
        JUNIT_3(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3), JUNIT_4(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4), TESTNG(PreferenceConstants.TEST_TYPE_VALUE_TESTNG);

        private final String preferenceConstant;

        private TestType(String type)
        {
            this.preferenceConstant = type;
        }

        static TestType fromPreferenceConstant(String type)
        {
            for (int i = 0; i < values().length; i++)
            {
                TestType testType = values()[i];
                if(testType.preferenceConstant.equals(type))
                {
                    return testType;
                }
            }
            throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    boolean isLaunchSupported(TestType testType, Collection< ? extends IMember> testMembers);

    ILaunchShortcut getShortcut();
}
