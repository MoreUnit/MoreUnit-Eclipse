package org.moreunit.mock.it;

import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.TESTNG,
                testClassNameTemplate = "${srcFile}Test"))
public class PowerMockitoWithTestNgTest extends MockingTestCase
{
    public PowerMockitoWithTestNgTest()
    {
        super("PowerMockito_testng", "org.moreunit.mock.mockitoWithPowermockAndAnnotations");
    }
}
