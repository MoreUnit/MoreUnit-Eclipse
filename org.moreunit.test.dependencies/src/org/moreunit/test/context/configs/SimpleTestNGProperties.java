package org.moreunit.test.context.configs;

import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;

@Properties(testType = TestType.TESTNG,
            testClassSuffixes = "Test",
            testMethodPrefix = false)
public class SimpleTestNGProperties
{

}
