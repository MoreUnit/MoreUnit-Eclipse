package org.moreunit.mock.it;

import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.JUNIT4,
                testClassNameTemplate = "${srcFile}Test"))
public class Jmock2ForJunit4Test extends MockingTestCase
{
    public Jmock2ForJunit4Test()
    {
        super("Jmock2_junit4", "org.moreunit.mock.jmock2ForJUnit4");
    }
}
