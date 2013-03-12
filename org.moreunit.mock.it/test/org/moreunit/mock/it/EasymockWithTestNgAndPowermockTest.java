package org.moreunit.mock.it;

import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.TESTNG,
                testClassNameTemplate = "${srcFile}Test"))
public class EasymockWithTestNgAndPowermockTest extends MockingTestCase
{
    public EasymockWithTestNgAndPowermockTest()
    {
        super("Easymock_testng_powermock", "org.moreunit.mock.easymockWithPowermockAndAnnotations");
    }
}
