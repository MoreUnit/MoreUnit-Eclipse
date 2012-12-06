package org.moreunit.mock.it;

import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.JUNIT4,
                testClassSuffixes = "Test"))
public class PowerMockitoWithJunit4Test extends MockingTestCase
{
    public PowerMockitoWithJunit4Test()
    {
        super("PowerMockito_junit4", "org.moreunit.mock.mockitoWithPowermockAndAnnotations");
    }
}
