package org.moreunit.mock.it;

import org.eclipse.jdt.ui.JavaUI;
import org.junit.Test;
import org.moreunit.mock.UiTestCase;
import org.moreunit.mock.WizardDriver.MockDependenciesPageIsOpenAction;
import org.moreunit.mock.WizardDriver.MockDependenciesWizardDriver;
import org.moreunit.mock.actions.MockDependenciesAction;
import org.moreunit.test.context.Context;

import com.google.inject.Inject;

@Context(mainSrc = "Mockito_all_dependencies.cut.java.txt",
        testSrc = "Mockito_all_dependencies.test.java.txt")
public class MockitoTest extends UiTestCase
{
    @Inject
    private MockDependenciesAction mockDependenciesAction;
    
    @Test
    public void should_mock_all_dependencies_from_class_under_test() throws Exception
    {
        // given
        whenMockDependenciesPageIsOpen_checkAllElements();

        // when
        JavaUI.openInEditor(context.getCompilationUnit("te.st.SomeConcept"));
        mockDependenciesAction.execute(null);

        // then
        context.assertCompilationUnit("te.st.SomeConceptTest").hasSameSourceAsIn("Mockito_all_dependencies.expected.java.txt");
    }

    @Test
    public void should_mock_all_dependencies_from_test_class() throws Exception
    {
        // given
        whenMockDependenciesPageIsOpen_checkAllElements();

        // when
        JavaUI.openInEditor(context.getCompilationUnit("te.st.SomeConceptTest"));
        mockDependenciesAction.execute(null);

        // then
        context.assertCompilationUnit("te.st.SomeConceptTest").hasSameSourceAsIn("Mockito_all_dependencies.expected.java.txt");
    }

    private void whenMockDependenciesPageIsOpen_checkAllElements()
    {
        wizardDriver.whenMockDependenciesPageIsOpen(new MockDependenciesPageIsOpenAction()
        {
            public void execute(MockDependenciesWizardDriver driver)
            {
                driver.checkAllElements();
            }
        });
    }
}
