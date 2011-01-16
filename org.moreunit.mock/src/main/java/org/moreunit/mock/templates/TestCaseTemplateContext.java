package org.moreunit.mock.templates;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.corext.template.java.JavaContext;
import org.eclipse.jdt.internal.corext.template.java.JavaContextType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.TemplateContextType;

public class TestCaseTemplateContext extends JavaContext
{
    public static final String CONTEXT_KEY = "org.moreunit.mock.templates.TestCaseTemplateContext";

    private static final TemplateContextType TYPE = JavaPlugin.getDefault().getTemplateContextRegistry().getContextType(JavaContextType.ID_ALL);

    public TestCaseTemplateContext(IDocument document, int insertionOffset, ICompilationUnit compilationUnit)
    {
        super(TYPE, document, insertionOffset, 0, compilationUnit);
        setReadOnly(false);
    }

    @Override
    public String getKey()
    {
        return CONTEXT_KEY;
    }

    // TODO Nicolas: override evaluate() to provide variables
}
