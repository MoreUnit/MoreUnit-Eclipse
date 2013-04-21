package org.moreunit.test.context.configs;

import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Preferences(
    testSrcFolder = "src/test/java",
    testType = TestType.JUNIT4,
    testClassNameTemplate = "${srcFile}Test")
public class SimpleMavenJUnit4Preferences
{
}
