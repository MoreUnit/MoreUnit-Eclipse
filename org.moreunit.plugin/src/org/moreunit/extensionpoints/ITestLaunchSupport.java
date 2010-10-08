package org.moreunit.extensionpoints;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.moreunit.preferences.PreferenceConstants;

public interface ITestLaunchSupport
{

    public enum TestType
    {
        JUNIT_3(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3), JUNIT_4(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4), TESTNG(PreferenceConstants.TEST_TYPE_VALUE_TESTNG);

        private final String preferenceConstant;

        private TestType(String type)
        {
            this.preferenceConstant = type;
        }

        public static TestType fromPreferenceConstant(String type)
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

    public enum Cardinality
    {
        ONE, SEVERAL;

        public static Cardinality fromElementCount(int count)
        {
            if(count <= 0)
            {
                throw new IllegalArgumentException("Unsupported cardinality: " + count);
            }
            return count == 1 ? ONE : SEVERAL;
        }
    }

    boolean isLaunchSupported(TestType testType, Class< ? extends IJavaElement> elementType, Cardinality cardinality);

    ILaunchShortcut getShortcut();
}
