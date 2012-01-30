package org.moreunit.test.context.configs;

import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;

@Properties(testType = TestType.JUNIT3,
        testClassSuffixes = "Test",
        testMethodPrefix = true)
public class SimpleJUnit3Properties
{
}
