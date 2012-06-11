package org.moreunit.mock.it;

import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.TESTNG,
                testClassSuffixes = "Test"))
public class MockitoWithTestNgTest extends MockingTestCase
{
    public MockitoWithTestNgTest()
    {
        super("Mockito_testNg", "org.moreunit.mock.mockitoWithAnnotationsAndJUnitRunner1.9");
    }
}
