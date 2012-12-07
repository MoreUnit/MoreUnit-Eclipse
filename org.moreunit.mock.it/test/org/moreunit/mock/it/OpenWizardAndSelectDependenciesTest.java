package org.moreunit.mock.it;

import org.eclipse.jdt.ui.JavaUI;
import org.junit.Test;
import org.moreunit.mock.UiTestCase;
import org.moreunit.mock.actions.MockDependenciesAction;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.TestType;

@Context(mainSrc = "SomeConcept.cut.java.txt",
        testSrc = "SomeConcept.test.java.txt",
        preferences = @Preferences(testType = TestType.JUNIT4,
                testClassSuffixes = { "Test", "TestWithParent" }))
public class OpenWizardAndSelectDependenciesTest extends UiTestCase
{
    private final String expectationQualifier = "Mockito_post_1.9";
    private final String templateId = "org.moreunit.mock.mockitoWithAnnotationsAndJUnitRunner1.9";

    private MockDependenciesAction mockDependenciesAction = new MockDependenciesAction();

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

    @Test
    @Context(mainSrc = "SomeConcept.cut.java.txt",
            testSrc = "SomeConcept.test_with_parent.test.java.txt",
            preferences = @Preferences(testType = TestType.JUNIT4,
                    testClassSuffixes = "TestWithParent"))
    public void should_place_type_annotation_at_the_right_place_when_test_case_has_parent_class() throws Exception
    {
        // given
        wizardDriver.whenMockDependenciesPageIsOpen()
                .selectTemplate(templateId)
                .checkAllElements()
                .done();

        // when
        JavaUI.openInEditor(context.getCompilationUnit("te.st.SomeConceptTestWithParent"));
        mockDependenciesAction.execute(null);

        // then
        context.assertCompilationUnit("te.st.SomeConceptTestWithParent").hasSameSourceAsIn(expectationQualifier + "_test_with_parent.expected.java.txt");
    }

    @Test
    @Context(mainSrc = "SomeConcept.cut.java.txt",
            testSrc = "SomeConcept.test_with_comment.test.java.txt",
            preferences = @Preferences(testType = TestType.JUNIT4,
                    testClassSuffixes = "TestWithComment"))
    public void should_place_type_annotation_at_the_right_place_when_test_case_has_comment() throws Exception
    {
        // given
        wizardDriver.whenMockDependenciesPageIsOpen()
                .selectTemplate(templateId)
                .checkAllElements()
                .done();

        // when
        JavaUI.openInEditor(context.getCompilationUnit("te.st.SomeConceptTestWithComment"));
        mockDependenciesAction.execute(null);

        // then
        context.assertCompilationUnit("te.st.SomeConceptTestWithComment").hasSameSourceAsIn(expectationQualifier + "_test_with_comment.expected.java.txt");
    }
}
