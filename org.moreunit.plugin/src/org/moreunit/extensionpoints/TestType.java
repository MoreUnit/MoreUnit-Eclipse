/**
 * MoreUnit-Plugin for Eclipse V3.5.
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
package org.moreunit.extensionpoints;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;

/**
 * This enum holds the test types.
 * <p>
 * <dt><b>Changes:</b></dt>
 * <dd>19.10.2010 Gro Refactoring: Merge with class from Nicolas</dd>
 * <p>
 * @author Nicolas Demengel, Andreas Groll
 * @version 19.10.2010
 * @since 1.5
 */
public enum TestType {

	/**
	 * JUnit version 3 test method.
	 */
	JUNIT_3(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3),
	/**
	 * JUnit version 4 test method.
	 */
	JUNIT_4(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4),
	/**
	 * TESTNG test method.
	 */
	TESTNG(PreferenceConstants.TEST_TYPE_VALUE_TESTNG);

	/**
	 * Internal value from MoreUnit preferences.
	 */
	private final String preferenceValue;

	/**
	 * Returns the MoreUnit test method type for a compilation unit.
	 * @param compilationUnit CompilationUnit.
	 * @return Test type for CompilationUnit.
	 */
	public static TestType getTestType(final ICompilationUnit compilationUnit) {

		return getTestType(compilationUnit.getJavaProject());
	}

    /**
     * Returns the MoreUnit test method type for a java project.
     * @param javaProject Java project.
     * @return Test type for Java project.
     */
    public static TestType getTestType(final IJavaProject javaProject) {

        String typeName = Preferences.getInstance().getTestType(javaProject);
        return TestType.fromPreferenceConstant(typeName);
    }

	/**
	 * Returns the enum preferenceValue from the MoreUnit preference string.
	 * @param moreUnitName MoreUnit preference string.
	 * @return TestType.
	 */
	public static TestType fromPreferenceConstant(final String moreUnitName) {

		for (TestType t : values()) {
			if (t.preferenceValue.equalsIgnoreCase(moreUnitName)) {
				return t;
			}
		}

		throw new IllegalArgumentException("Not a valid test type name: " + moreUnitName);
	}

	/**
	 * Constructor for TestType, only private access permitted.
	 * @param preferenceValue Internal preferenceValue.
	 */
	private TestType(final String preferenceValue) {

		this.preferenceValue = preferenceValue;
	}
}
