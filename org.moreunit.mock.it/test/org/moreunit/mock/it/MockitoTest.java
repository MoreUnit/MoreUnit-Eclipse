package org.moreunit.mock.it;

import org.junit.Rule;
import org.junit.Test;
import org.moreunit.mock.BindingOverridingRule;
import org.moreunit.mock.ConfigurableWizardFactory;
import org.moreunit.mock.actions.MockDependenciesAction;
import org.moreunit.mock.wizard.WizardFactory;
import org.moreunit.test.Context;
import org.moreunit.test.TestContextRule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

public class MockitoTest
{
    @Rule
    public BindingOverridingRule bindingOverridingRule = new BindingOverridingRule(new AbstractModule()
    {
        @Override
        protected void configure()
        {
            bind(WizardFactory.class).toInstance(new ConfigurableWizardFactory()
            {
                @Override
                protected void whenMockDependenciesPageIsOpen()
                {
                    selectAllMockableElements();
                }
            });
        }
    });

    @Rule
    public TestContextRule context = new TestContextRule();

    @Inject
    private MockDependenciesAction mockDependenciesAction;

    @Test
    @Context(cutDefinition = "Mockito_all_dependencies.cut.java.txt", testCaseDefinition = "Mockito_all_dependencies.test.java.txt", expectedTestCase = "Mockito_all_dependencies.expected.java.txt")
    public void should_mock_all_dependencies() throws Exception
    {
        mockDependenciesAction.setCompilationUnit(context.getClassUnderTest().getCompilationUnit());
        mockDependenciesAction.execute();

        // JavaUI.openInEditor(cut);
        // mockDependenciesAction.run(null);

        context.assertExpectedTestCase();
    }
}
