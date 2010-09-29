/**
 * JavaPropertiesOutline-Plugin for Eclipse V3.3.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License - v 1.0.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Eclipse Public License for more details.
 * 
 * Autor: Andreas Groll
 * Datum: 22.09.2010
 */
package org.moreunit.extension.handler;

/**
 * This enum holds the test types.
 * <p>
 * <b>&copy; AG, D-49326 Melle 2010</b>
 * <p>
 * <dl>
 * <dt><b>Changes:</b></dt>
 * </dl>
 * <p>
 * @author Andreas Groll
 * @version 22.09.2010
 * @since 1.5
 */
public enum TestType {

	/**
	 * JUnit version 3 test method.
	 */
	JUnit3(0),
	/**
	 * JUnit version 4 test method.
	 */
	JUnit4(1),
	/**
	 * TestNG test method.
	 */
	TestNG(2);

	/**
	 * Internal counter.
	 */
	final int value;

	/**
	 * Constructor for TestType, only private access permitted.
	 * @param value Internal value.
	 */
	private TestType(final int value) {

		this.value = value;
	}

	/**
	 * Returns the enum value from the MoreUnit preference string.
	 * @param moreUnitName MoreUnit preference string.
	 * @return TestType.
	 */
	public static TestType get(final String moreUnitName) {

		for (TestType t : values()) {
			if (t.name().equalsIgnoreCase(moreUnitName)) {
				return t;
			}
		}

		throw new IllegalArgumentException("Not a valid test type name: " + moreUnitName);
	}
}
