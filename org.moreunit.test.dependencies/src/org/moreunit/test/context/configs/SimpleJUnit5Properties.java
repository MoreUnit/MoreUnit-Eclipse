package org.moreunit.test.context.configs;

import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;

@Properties(
        testType = TestType.JUNIT5,
        testClassNameTemplate = "${srcFile}Test")
public class SimpleJUnit5Properties
{
}
