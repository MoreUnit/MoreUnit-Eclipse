package org.moreunit.test.context;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class TestContextRuleWithSuperClassAnnotationTest extends AnnotatedSuperClass
{
    @RegisterExtension
    public TestContextRule context = new TestContextRule(new ConfigExtractorThatDoesNotInteractWithWorkspace());

    @Test
    public void should_load_superclass_context_when_class_context() throws Exception
    {
        assertEquals(context.getCompilationUnitHandler("ClassUnderTestFromSuperclass").getInitialSource(), "Content of ClassUnderTestFromSuperclass.txt");
        assertEquals(context.getCompilationUnitHandler("TestCaseFromSuperclass").getInitialSource(), "Content of TestCaseFromSuperclass.txt");
    }
}
