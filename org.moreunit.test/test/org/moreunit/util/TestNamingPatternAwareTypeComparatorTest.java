package org.moreunit.util;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertTrue;

import org.eclipse.jdt.core.IType;
import org.junit.Test;
import org.moreunit.preferences.Preferences;

public class TestNamingPatternAwareTypeComparatorTest
{

    @Test
    public void testCompareWithTestPrefixes()
    {
        Preferences preferences = new PreferencesMock(new String[] {}, new String[] { "Test" });
        TestNamingPatternAwareTypeComparator comparator = new TestNamingPatternAwareTypeComparator(preferences);
        assertTrue(0 > comparator.compare(mockType("HelloTest"), mockType("HelloOtherTest")));
    }

    private IType mockType(String typeName)
    {
        IType mock = createNiceMock(IType.class);
        expect(mock.getElementName()).andStubReturn(typeName);
        replay(mock);
        return mock;
    }
}
