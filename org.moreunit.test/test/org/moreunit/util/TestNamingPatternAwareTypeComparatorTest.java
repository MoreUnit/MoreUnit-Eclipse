package org.moreunit.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IType;
import org.junit.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

public class TestNamingPatternAwareTypeComparatorTest extends ContextTestCase
{

    @Project(name="aProject")
    @Preferences(testClassSuffixes="Test")
    @Test
    public void should_compare_classnames_stripped_from_test_suffix()
    {
        TestNamingPatternAwareTypeComparator comparator = new TestNamingPatternAwareTypeComparator(org.moreunit.preferences.Preferences.getInstance());
        assertThat(comparator.compare(mockType("HelloTest"), mockType("HelloOtherTest"))).isLessThan(0);
    }

    private IType mockType(String typeName)
    {
        IType mock = mock(IType.class);
        when(mock.getElementName()).thenReturn(typeName);
        return mock;
    }
}
