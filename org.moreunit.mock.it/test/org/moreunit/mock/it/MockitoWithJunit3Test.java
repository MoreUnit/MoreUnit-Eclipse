package org.moreunit.mock.it;

import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

/**
 * Note that the expected result does not test for the presence of
 * "import static org.mockito.Mockito.mock;", since the test project is not able
 * to resolve this dependency.
 */
@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.JUNIT3,
                testClassNameTemplate = "${srcFile}Test"))
public class MockitoWithJunit3Test extends MockingTestCase
{
    public MockitoWithJunit3Test()
    {
        super("Mockito_junit3", "org.moreunit.mock.mockitoWithoutAnnotations");
    }
}
