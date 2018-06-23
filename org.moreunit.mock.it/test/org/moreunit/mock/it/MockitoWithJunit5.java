package org.moreunit.mock.it;

import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.JUNIT5,
                testClassNameTemplate = "${srcFile}Test"))
public class MockitoWithJunit5 extends MockingTestCase
{
    public MockitoWithJunit5()
    {
        super("Mockito_junit5", "org.moreunit.mock.mockitoWithAnnotationsAndJUnitRunner1.9");
    }
}
