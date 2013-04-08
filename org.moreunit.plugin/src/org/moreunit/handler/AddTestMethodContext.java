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
package org.moreunit.handler;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.moreunit.extensionpoints.IAddTestMethodContext;
import org.moreunit.preferences.Preferences;

/**
 * Holds test context.
 * <p>
 * <dt><b>Changes:</b></dt>
 * <dd>16.06.2010 Gro Adapted to modified interface
 * {@link IAddTestMethodContext},
 * {@link IAddTestMethodContext#isNewTestClassCreated()}</dd>
 * <dd>23.09.2010 Gro Adapted to modified interface
 * {@link IAddTestMethodContext}, {@link IAddTestMethodContext#getPreferences()}
 * </dd>
 * <dd>30.09.2010 Gro Methode {@link #toString()} improved</dd>
 * 
 * @author vera, andreas
 * @version 30.09.2010
 */
public class AddTestMethodContext implements IAddTestMethodContext
{

    /*
     * Final member variables.
     */
    private final ICompilationUnit testClassCompilationUnit;
    private final ICompilationUnit classUnderTestCompilationUnit;
    private final IMethod methodUnderTest;
    private final boolean newTestClassCreated;

    /*
     * MoreUnit Preferences.
     */
    private Preferences preferences = null;

    /*
     * This may be modified, if clients replaced test method.
     */
    private IMethod testMethod;

    /**
     * Constructor for AddTestMethodContext.
     * 
     * @param newTestMethod Test method.
     * @param methodUnderTest Method under test.
     */
    public AddTestMethodContext(IMethod testMethod, IMethod methodUnderTest)
    {
        this(testMethod.getCompilationUnit(), testMethod, methodUnderTest.getCompilationUnit(), methodUnderTest, false);
    }

    /**
     * Constructor for AddTestMethodContext.
     * 
     * @param newTestMethod Test method.
     * @param methodUnderTest Method under test.
     * @param newTestClass New test class created?
     */
    public AddTestMethodContext(IMethod testMethod, IMethod methodUnderTest, boolean newTestClassCreated)
    {
        this(testMethod.getCompilationUnit(), testMethod, methodUnderTest.getCompilationUnit(), methodUnderTest, newTestClassCreated);
    }

    /**
     * Constructor for AddTestMethodContext.
     * 
     * @param testClassCompilationUnit Test class.
     * @param newTestMethod Test method.
     * @param classUnderTestCompilationUnit Class under test.
     * @param methodUnderTest Method under test.
     */
    public AddTestMethodContext(ICompilationUnit testClassCompilationUnit, IMethod testMethod, ICompilationUnit classUnderTestCompilationUnit, IMethod methodUnderTest)
    {
        this(testClassCompilationUnit, testMethod, classUnderTestCompilationUnit, methodUnderTest, false);
    }

    /**
     * Constructor for AddTestMethodContext.
     * 
     * @param testClassCompilationUnit Test class.
     * @param newTestMethod Test method.
     * @param classUnderTestCompilationUnit Class under test.
     * @param methodUnderTest Method under test.
     * @param newTestClass New test class created?
     */
    public AddTestMethodContext(ICompilationUnit testClassCompilationUnit, IMethod testMethod, ICompilationUnit classUnderTestCompilationUnit, IMethod methodUnderTest, boolean newTestClassCreated)
    {
        this.testClassCompilationUnit = testClassCompilationUnit;
        this.testMethod = testMethod;
        this.classUnderTestCompilationUnit = classUnderTestCompilationUnit;
        this.methodUnderTest = methodUnderTest;
        this.newTestClassCreated = newTestClassCreated;
    }

    /**
     * {@inheritDoc}
     */
    public ICompilationUnit getTestClass()
    {
        return testClassCompilationUnit;
    }

    /**
     * {@inheritDoc}
     */
    public IMethod getTestMethod()
    {
        return testMethod;
    }

    /**
     * {@inheritDoc}
     */
    public void setTestMethod(IMethod testMethod)
    {
        this.testMethod = testMethod;
    }

    /**
     * {@inheritDoc}
     */
    public ICompilationUnit getClassUnderTest()
    {
        return classUnderTestCompilationUnit;
    }

    /**
     * {@inheritDoc}
     */
    public IMethod getMethodUnderTest()
    {
        return methodUnderTest;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNewTestClassCreated()
    {
        return newTestClassCreated;
    }

    /**
     * {@inheritDoc}
     */
    public Preferences getPreferences()
    {
        return preferences;
    }

    /**
     * {@inheritDoc}
     */
    public void setPreferences(Preferences preferences)
    {
        this.preferences = preferences;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AddTestMethodContext [");
        builder.append("\n  classUnderTestCompilationUnit=");
        builder.append(classUnderTestCompilationUnit.getElementName());
        builder.append("\n  methodUnderTest=");
        builder.append(methodUnderTest.getElementName());
        builder.append("\n  testMethod=");
        builder.append(testMethod.getElementName());
        builder.append("\n  testClassCompilationUnit=");
        builder.append(testClassCompilationUnit.getElementName());
        builder.append("\n  isNewTestClassCreated=");
        builder.append(isNewTestClassCreated());
        builder.append("\n]");
        return builder.toString();
    }
}
