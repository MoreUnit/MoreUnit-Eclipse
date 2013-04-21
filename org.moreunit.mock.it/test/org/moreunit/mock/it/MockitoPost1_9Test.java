package org.moreunit.mock.it;

import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.JUNIT4,
                testClassNameTemplate = "${srcFile}Test"))
public class MockitoPost1_9Test extends MockingTestCase
{
    public MockitoPost1_9Test()
    {
        super("Mockito_post_1.9", "org.moreunit.mock.mockitoWithAnnotationsAndJUnitRunner1.9");
    }
}
