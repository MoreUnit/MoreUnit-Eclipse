package org.moreunit.wizards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.extensionpoints.TestType;

public class NewTestCaseWizardContextTest
{
    private IType classUnderTest;
    private MoreUnitWizardPageOne pageOne;
    private NewTestCaseWizardContext context;

    @BeforeEach
    public void setUp()
    {
        classUnderTest = mock(IType.class);
        pageOne = mock(MoreUnitWizardPageOne.class);
        context = new NewTestCaseWizardContext(classUnderTest, pageOne);
    }

    @Test
    public void should_return_class_under_test()
    {
        assertEquals(classUnderTest, context.getClassUnderTest());
    }

    @Test
    public void should_return_created_test_case()
    {
        IType testCase = mock(IType.class);
        context.setCreatedTestCase(testCase);

        assertEquals(testCase, context.getCreatedTestCase());
    }

    @Test
    public void should_return_test_case_package_from_page_one()
    {
        IPackageFragment packageFragment = mock(IPackageFragment.class);
        when(pageOne.getTestCasePackage()).thenReturn(packageFragment);

        assertEquals(packageFragment, context.getTestCasePackage());
    }

    @Test
    public void should_return_test_type_from_page_one()
    {
        TestType testType = mock(TestType.class);
        when(pageOne.getTestType()).thenReturn(testType);

        assertEquals(testType, context.getTestType());
    }

    @Test
    public void should_store_and_retrieve_client_values()
    {
        context.put("key1", "value1");
        context.put("key2", 42);

        String val1 = context.get("key1");
        Integer val2 = context.get("key2");

        assertEquals("value1", val1);
        assertEquals(Integer.valueOf(42), val2);
        assertNull(context.get("unknown_key"));
    }
}
