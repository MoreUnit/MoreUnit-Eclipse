package org.moreunit.wizards;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(context.getClassUnderTest()).isEqualTo(classUnderTest);
    }

    @Test
    public void should_return_created_test_case()
    {
        IType testCase = mock(IType.class);
        context.setCreatedTestCase(testCase);

        assertThat(context.getCreatedTestCase()).isEqualTo(testCase);
    }

    @Test
    public void should_return_test_case_package_from_page_one()
    {
        IPackageFragment packageFragment = mock(IPackageFragment.class);
        when(pageOne.getTestCasePackage()).thenReturn(packageFragment);

        assertThat(context.getTestCasePackage()).isEqualTo(packageFragment);
    }

    @Test
    public void should_return_test_type_from_page_one()
    {
        TestType testType = mock(TestType.class);
        when(pageOne.getTestType()).thenReturn(testType);

        assertThat(context.getTestType()).isEqualTo(testType);
    }

    @Test
    public void should_store_and_retrieve_client_values()
    {
        context.put("key1", "value1");
        context.put("key2", 42);

        String val1 = context.get("key1");
        Integer val2 = context.get("key2");

        assertThat(val1).isEqualTo("value1");
        assertThat(val2).isEqualTo(42);
        assertThat((Object) context.get("unknown_key")).isNull();
    }
}
