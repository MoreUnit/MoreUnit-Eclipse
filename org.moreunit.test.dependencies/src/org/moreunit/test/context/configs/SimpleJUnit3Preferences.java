package org.moreunit.test.context.configs;

import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Preferences(
        testType = TestType.JUNIT3,
        testClassSuffixes = "Test",
        testMethodPrefix = true)
public class SimpleJUnit3Preferences
{
}
