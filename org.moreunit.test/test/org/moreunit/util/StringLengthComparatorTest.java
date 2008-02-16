package org.moreunit.util;

import junit.framework.TestCase;

public class StringLengthComparatorTest extends TestCase {

	public void testLongerAStringIsGreater() throws Exception {
		assertTrue(0 < new StringLengthComparator().compare("Long", ""));
	}

	public void testLongerBStringIsGreater() throws Exception {
		assertTrue(0 > new StringLengthComparator().compare("", "Long"));
	}
	
	public void testEqualStrings() throws Exception {
		assertEquals(0,  new StringLengthComparator().compare("Long", "Long"));
	}
}
