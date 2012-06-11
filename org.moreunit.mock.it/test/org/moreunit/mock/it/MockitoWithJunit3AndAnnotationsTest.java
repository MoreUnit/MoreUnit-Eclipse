package org.moreunit.mock.it;

import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.JUNIT3,
                testClassSuffixes = "Test"))
public class MockitoWithJunit3AndAnnotationsTest extends MockingTestCase
{
    public MockitoWithJunit3AndAnnotationsTest()
    {
        super("Mockito_junit3_annotations", "org.moreunit.mock.mockitoWithAnnotationsAndJUnitRunner1.9");
    }
}
