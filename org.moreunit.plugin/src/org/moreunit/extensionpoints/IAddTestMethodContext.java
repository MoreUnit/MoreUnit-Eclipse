/**
 * MoreUnit-Plugin for Eclipse V3.5.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License - v 1.0.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See Eclipse Public License for more details.
 */
package org.moreunit.extensionpoints;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;

/**
 * Holds test context, that will be used by extension-point
 * <code>addTestmethodParticipator</code> to give implementors the chance to
 * modify the created test method.
 * <p>
 * <dt><b>Changes:</b></dt>
 * <dd>16.06.2010 Gro Method added {@link #isNewTestClassCreated()}</dd>
 * <dd>23.09.2010 Gro Methods added {@link #getPreferences()},
 * {@link #setPreferences(Preferences)}</dd>
 * 
 * @author vera, andreas
 * @version 23.09.2010
 */
public interface IAddTestMethodContext
{
    /**
     * Returns the test class, that is the class under test.
     * 
     * @return Test class.
     */
    ICompilationUnit getTestClass();

    /**
     * Returns the test method, that is the method implementing the test case.
     * 
     * @return Test method.
     */
    IMethod getTestMethod();

    /**
     * Return the class under test.
     * 
     * @return Class under test.
     */
    ICompilationUnit getClassUnderTest();

    /**
     * Returns the method under test.
     * 
     * @return Method under test.
     */
    IMethod getMethodUnderTest();

    /**
     * Sets the test method.
     * <p>
     * Updates the context, after the test method has been replaced from an
     * extension.
     * 
     * @param testMethod Testmethod.
     */
    void setTestMethod(IMethod testMethod);

    /**
     * Is a new test class created?
     * 
     * @return New test class?
     */
    boolean isNewTestClassCreated();

    /**
     * Returns the value of the MoreUnit Preferences, see {@link Preferences},
     * {@link PreferenceConstants}.
     * <p>
     * It is not permitted, that clients modify the preferences as this can lead
     * to unpredictable results.
     * 
     * @return Preferences.
     */
    Preferences getPreferences();

    /**
     * Sets the value of the MoreUnit preferences, see {@link Preferences},
     * {@link PreferenceConstants}.
     * 
     * @param preferences Preferences.
     */
    void setPreferences(Preferences preferences);
}
