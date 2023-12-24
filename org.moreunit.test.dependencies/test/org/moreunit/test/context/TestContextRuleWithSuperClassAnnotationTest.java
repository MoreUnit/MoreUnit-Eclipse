package org.moreunit.test.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;

public class TestContextRuleWithSuperClassAnnotationTest extends AnnotatedSuperClass
{
    @Rule
    public TestContextRule context = new TestContextRule(new ConfigExtractorThatDoesNotInteractWithWorkspace());

    @Test
    public void should_load_superclass_context_when_class_context() throws Exception
    {
        assertThat(context.getCompilationUnitHandler("ClassUnderTestFromSuperclass").getInitialSource()).isEqualTo("Content of ClassUnderTestFromSuperclass.txt");
        assertThat(context.getCompilationUnitHandler("TestCaseFromSuperclass").getInitialSource()).isEqualTo("Content of TestCaseFromSuperclass.txt");
    }
}
