package org.moreunit.mock.it;

import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.JUNIT4,
                testClassNameTemplate = "${srcFile}Test"))
public class JMockitTest extends MockingTestCase
{
    public JMockitTest()
    {
        super("JMockit", "org.moreunit.mock.jmockitDefault");
    }
}
