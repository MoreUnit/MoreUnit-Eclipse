package org.moreunit.mock.it;

import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.JUNIT4,
                testClassNameTemplate = "${srcFile}Test"))
public class JMockitWithoutJunitRunWithTest extends MockingTestCase
{
    public JMockitWithoutJunitRunWithTest()
    {
        super("JMockit_without_runwith", "org.moreunit.mock.jmockitWithoutJunitRunWith");
    }
}
