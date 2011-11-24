package org.moreunit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringLengthComparatorTest
{
    @Test
    public void testLongerAStringIsGreater() throws Exception
    {
        assertTrue(0 < new StringLengthComparator().compare("Long", ""));
    }

    @Test
    public void testLongerBStringIsGreater() throws Exception
    {
        assertTrue(0 > new StringLengthComparator().compare("", "Long"));
    }

    @Test
    public void testEqualStrings() throws Exception
    {
        assertEquals(0, new StringLengthComparator().compare("Long", "Long"));
    }
}
