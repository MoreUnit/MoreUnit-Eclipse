package org.moreunit.wizards;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.moreunit.extensionpoints.INewTestCaseWizardContext;
import org.moreunit.extensionpoints.TestType;

public class NewTestCaseWizardContext implements INewTestCaseWizardContext
{
    private final IType classUnderTest;
    private final MoreUnitWizardPageOne pageOne;
    private final Map<String, Object> clientValues;
    private IType createdTestCase;

    public NewTestCaseWizardContext(IType classUnderTest, MoreUnitWizardPageOne pageOne)
    {
        this.classUnderTest = classUnderTest;
        this.pageOne = pageOne;
        this.clientValues = new HashMap<String, Object>();
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
        return pageOne.getTestCasePackage();
    }

    @Override
    public TestType getTestType()
    {
        return pageOne.getTestType();
    }

    void setCreatedTestCase(IType createdTestCase)
    {
        this.createdTestCase = createdTestCase;
    }

    public void put(String key, Object value)
    {
        clientValues.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key)
    {
        return (T) clientValues.get(key);
    }
}
