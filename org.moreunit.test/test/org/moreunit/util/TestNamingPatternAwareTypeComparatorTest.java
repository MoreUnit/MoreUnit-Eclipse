package org.moreunit.util;

import static org.mockito.Mockito.*;
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
        IType mock = mock(IType.class);
        when(mock.getElementName()).thenReturn(typeName);
        return mock;
    }
}
