package org.moreunit.mock.it;

import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

/**
 * Note that the expected result does not test for the presence of
 * "import static org.easymock.EasyMock.createNiceMock;", since the test project
 * is not able to resolve this dependency.
 */
@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.JUNIT3,
                testClassSuffixes = "Test"))
public class EasymockWithJunit3Test extends MockingTestCase
{
    public EasymockWithJunit3Test()
    {
        super("Easymock_junit3", "org.moreunit.mock.easymockDefault");
    }
}
