package org.moreunit.mock.templates;

import org.eclipse.jdt.internal.corext.template.java.JavaContextType;
import org.eclipse.jface.text.templates.Template;

public class TestCaseTemplate extends Template
{
    public TestCaseTemplate(CodeTemplate codeTemplate)
    {
        super(TestCaseTemplateContext.CONTEXT_KEY, "", JavaContextType.ID_ALL, codeTemplate.pattern(), false);
    }
}
