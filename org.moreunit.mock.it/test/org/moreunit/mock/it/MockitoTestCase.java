package org.moreunit.mock.it;

import org.eclipse.jdt.ui.JavaUI;
import org.junit.Test;
import org.moreunit.mock.UiTestCase;
import org.moreunit.mock.actions.MockDependenciesAction;
import org.moreunit.test.context.Context;

import com.google.inject.Inject;

@Context(mainSrc = "Mockito.cut.java.txt",
        testSrc = "Mockito.test.java.txt")
public abstract class MockitoTestCase extends UiTestCase
{
    private final String expectationQualifier;
    private final String templateId;

    @Inject
    private MockDependenciesAction mockDependenciesAction;

    protected MockitoTestCase(String expectationQualifier, String templateId)
    {
        this.expectationQualifier = expectationQualifier;
        this.templateId = templateId;
    }

    @Test
    public void should_mock_all_dependencies_from_class_under_test() throws Exception
    {
        // given
        wizardDriver.whenMockDependenciesPageIsOpen()
                .selectTemplate(templateId)
                .checkAllElements()
                .done();

        // when
        JavaUI.openInEditor(context.getCompilationUnit("te.st.SomeConcept"));
        mockDependenciesAction.execute(null);

        // then
        context.assertCompilationUnit("te.st.SomeConceptTest").hasSameSourceAsIn("Mockito_" + expectationQualifier + "_all_dependencies.expected.java.txt");
    }

    @Test
    public void should_mock_all_dependencies_from_test_class() throws Exception
    {
        // given
        wizardDriver.whenMockDependenciesPageIsOpen()
                .selectTemplate(templateId)
                .checkAllElements()
                .done();

        // when
        JavaUI.openInEditor(context.getCompilationUnit("te.st.SomeConceptTest"));
        mockDependenciesAction.execute(null);

        // then
        context.assertCompilationUnit("te.st.SomeConceptTest").hasSameSourceAsIn("Mockito_" + expectationQualifier + "_all_dependencies.expected.java.txt");
    }

    @Test
    public void should_mock_some_dependencies() throws Exception
    {
        // given
        wizardDriver.whenMockDependenciesPageIsOpen()
                .selectTemplate(templateId)
                .checkElements("SomeConcept", "setSomeListOfThings", "runnable")
                .done();

        // when
        JavaUI.openInEditor(context.getCompilationUnit("te.st.SomeConcept"));
        mockDependenciesAction.execute(null);

        // then
        context.assertCompilationUnit("te.st.SomeConceptTest").hasSameSourceAsIn("Mockito_" + expectationQualifier + "_some_dependencies.expected.java.txt");
    }
}
