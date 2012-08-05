package org.moreunit.mock.it;

import org.eclipse.jdt.ui.JavaUI;
import org.junit.Test;
import org.moreunit.mock.UiTestCase;
import org.moreunit.mock.actions.MockDependenciesAction;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

import com.google.inject.Inject;

@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.JUNIT4,
                testClassSuffixes = "Test"))
public class OpenWizardAndSelectDependenciesTest extends UiTestCase
{
    private final String expectationQualifier = "Mockito_post_1.9";
    private final String templateId = "org.moreunit.mock.mockitoWithAnnotationsAndJUnitRunner1.9";

    @Inject
    private MockDependenciesAction mockDependenciesAction;

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
        context.assertCompilationUnit("te.st.SomeConceptTest").hasSameSourceAsIn(expectationQualifier + "_all_dependencies.expected.java.txt");
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
        context.assertCompilationUnit("te.st.SomeConceptTest").hasSameSourceAsIn(expectationQualifier + "_all_dependencies.expected.java.txt");
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
        context.assertCompilationUnit("te.st.SomeConceptTest").hasSameSourceAsIn(expectationQualifier + "_some_dependencies.expected.java.txt");
    }
}
