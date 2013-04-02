package org.moreunit.extensionpoints;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;

/**
 * The context of a new test case creation.
 */
public interface INewTestCaseWizardContext
{
    /**
     * Returns the class for which the creation of a new test case has been
     * requested.
     * 
     * @return the class under test
     */
    IType getClassUnderTest();

    /**
     * Returns the newly created test case, if available.
     * 
     * @return the test case, or <tt>null</tt> if the test case creation has not
     *         completed.
     */
    IType getCreatedTestCase();

    /**
     * Returns the package in which the test case (will be/has been) created.
     * 
     * @return the test case package fragment
     */
    IPackageFragment getTestCasePackage();

    /**
     * The test library used in the new test class.
     */
    TestType getTestType();

    /**
     * Stores <tt>value</tt> into this context under the given <tt>key</tt>.
     */
    void put(String key, Object value);

    /**
     * Retrieves the value corresponding to the given key, or returns
     * <tt>null</tt> if it does not exist.
     * 
     * @param <T> the expected type of the value, to which it will be cast
     * @param key the key under which the value has been stored
     * @return the required value or <tt>null</tt>
     */
    <T> T get(String key);
}
