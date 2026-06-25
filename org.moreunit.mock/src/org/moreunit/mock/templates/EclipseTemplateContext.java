package org.moreunit.mock.templates;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.template.java.JavaContext;
import org.eclipse.jdt.internal.corext.template.java.JavaContextType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;

/**
 * Contains Eclipse-specific logic to evaluate {@link Template}s.
 */
@SuppressWarnings("restriction")
public class EclipseTemplateContext
{
    /**
     * Forces {@link CustomJavaContext} to evaluate {@link EclipseTemplate}.
     */
    public static final String CONTEXT_KEY = "org.moreunit.mock.templates.TestCaseTemplateContext";

    /**
     * Tells Eclipse's templating mechanism to evaluate templates as Java code.
     */
    public static final String CONTEXT_TYPE = JavaContextType.ID_ALL;

    private final MockingContext globalContext;
    private final ICompilationUnit compilationUnit;

    public EclipseTemplateContext(MockingContext globalContext)
    {
        this.globalContext = globalContext;
        this.compilationUnit = globalContext.testCaseCompilationUnit;
    }

    public void evaluate(EclipseTemplate eclipseTemplate) throws MockingTemplateException, JavaModelException, BadLocationException, TemplateException
    {
        IDocument document = new Document(compilationUnit.getSource());
        int insertionOffset = eclipseTemplate.getInsertionOffset(globalContext);

        CustomJavaContext localContext = new CustomJavaContext(document, insertionOffset, compilationUnit);

        Template template = eclipseTemplate.template();
        if(! localContext.canEvaluate(template))
        {
            throw new MockingTemplateException("Cannot evaluate template: " + template.getPattern(), false);
        }

        // this may already modify the compilation unit (e.g. add imports)
        TemplateBuffer templateBuffer = localContext.evaluate(template);

        // takes such modifications into account
        reconcile(compilationUnit);

        // apply source edits that are in templateBuffer
        updateSource(templateBuffer, eclipseTemplate);

        // takes those last modifications into account
        reconcile(compilationUnit);
    }

    private void reconcile(ICompilationUnit compilationUnit) throws JavaModelException
    {
        compilationUnit.reconcile(ICompilationUnit.NO_AST, false, null, null);
    }

    private void updateSource(TemplateBuffer templateBuffer, EclipseTemplate eclipseTemplate) throws JavaModelException, MockingTemplateException, BadLocationException
    {
        IDocument document = new Document(compilationUnit.getSource());
        int insertionOffset = eclipseTemplate.getInsertionOffset(globalContext);

        document.replace(insertionOffset, 0, templateBuffer.getString());

        compilationUnit.getBuffer().setContents(document.get());
    }

    /**
     * Context in charge to evaluate patterns as Java code and to provide
     * utility functions such as <code>${...:newType(...)}</code>.
     */
    private static class CustomJavaContext extends JavaContext
    {
        private static final TemplateContextType TYPE = JavaPlugin.getDefault().getTemplateContextRegistry().getContextType(CONTEXT_TYPE);

        public CustomJavaContext(IDocument document, int insertionOffset, ICompilationUnit compilationUnit)
        {
            super(TYPE, document, insertionOffset, 0, compilationUnit);
            setReadOnly(false);
        }

        @Override
        public String getKey()
        {
            return CONTEXT_KEY;
        }
    }
}
