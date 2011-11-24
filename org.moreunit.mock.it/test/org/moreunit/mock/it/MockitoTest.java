package org.moreunit.mock.it;

import org.eclipse.jdt.ui.JavaUI;
import org.junit.Rule;
import org.junit.Test;
import org.moreunit.mock.BindingOverridingRule;
import org.moreunit.mock.ConfigurableWizardFactory;
import org.moreunit.mock.actions.MockDependenciesAction;
import org.moreunit.mock.wizard.WizardFactory;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.TestContextRule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

@Context(mainSrc = "Mockito_all_dependencies.cut.java.txt",
        testSrc = "Mockito_all_dependencies.test.java.txt")
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
    public void should_mock_all_dependencies_from_class_under_test() throws Exception
    {
        JavaUI.openInEditor(context.getCompilationUnit("te.st.SomeConcept"));

        mockDependenciesAction.execute(null);

        context.assertCompilationUnit("te.st.SomeConceptTest").hasSameSourceAsIn("Mockito_all_dependencies.expected.java.txt");
    }

    @Test
    public void should_mock_all_dependencies_from_test_class() throws Exception
    {
        JavaUI.openInEditor(context.getCompilationUnit("te.st.SomeConceptTest"));

        mockDependenciesAction.execute(null);

        context.assertCompilationUnit("te.st.SomeConceptTest").hasSameSourceAsIn("Mockito_all_dependencies.expected.java.txt");
    }
}
