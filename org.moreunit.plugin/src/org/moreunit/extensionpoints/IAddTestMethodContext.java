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

/**
 * Holds test context, that will be used by extension-point
 * <code>addTestmethodParticipator</code> to give implementors the chance to
 * modify the created test method.
 * 
 * @author vera, extended andreas 16.06.2010
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
     * @return New test class?
     */
    boolean isNewTestClassCreated();
}
