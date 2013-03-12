package org.moreunit.mock.it;

import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.JUNIT3,
                testClassNameTemplate = "${srcFile}Test"))
public class Jmock2DefaultTest extends MockingTestCase
{
    public Jmock2DefaultTest()
    {
        super("Jmock2_default", "org.moreunit.mock.jmock2Default");
    }
}
