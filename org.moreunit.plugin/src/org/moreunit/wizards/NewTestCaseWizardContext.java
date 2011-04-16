package org.moreunit.wizards;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.moreunit.extensionpoints.INewTestCaseWizardContext;

public class NewTestCaseWizardContext implements INewTestCaseWizardContext
{
    private final IType classUnderTest;
    private final IPackageFragment testCasePackage;
    private IType createdTestCase;

    public NewTestCaseWizardContext(IType classUnderTest, IPackageFragment testCasePackage)
    {
        this.classUnderTest = classUnderTest;
        this.testCasePackage = testCasePackage;
    }

    public IType getClassUnderTest()
    {
        return classUnderTest;
    }

    public IType getCreatedTestCase()
    {
        return createdTestCase;
    }

    public IPackageFragment getTestCasePackage()
    {
        return testCasePackage;
    }

    void setCreatedTestCase(IType createdTestCase)
    {
        this.createdTestCase = createdTestCase;
    }
}
