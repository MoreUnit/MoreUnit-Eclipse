package org.moreunit.test.context.configs;

import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;

@Project(
        mainCls = "org:SomeClass",
        mainSrcFolder = "src/main/java",
        testCls = "org:SomeClassTest",
        testSrcFolder = "src/test/java",
        properties = @Properties(
                testType = TestType.JUNIT4,
                testClassSuffixes = "Test"
        ))
public class SimpleMavenJUnit4Project
{
}
