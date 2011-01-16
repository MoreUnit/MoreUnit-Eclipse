package org.moreunit.mock.templates;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
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
import org.eclipse.text.edits.InsertEdit;

public class TemplateApplicator
{
    public void applyTemplate(MockingTemplate mockingTemplate, IType type) throws TemplateException
    {
        try
        {
            ICompilationUnit compilationUnit = type.getCompilationUnit();

            open(compilationUnit);

            String source = getSource(compilationUnit);
            IDocument document = new Document(source);

            for (CodeTemplate codeTemplate : mockingTemplate.codeTemplates())
            {
                applyTemplate(codeTemplate, type, document, source);
            }

            commitChanges(document, compilationUnit);
        }
        catch (Exception e)
        {
            throw new TemplateException("Could not apply " + mockingTemplate + " to " + type.getElementName(), e);
        }
    }

    private void open(ICompilationUnit compilationUnit) throws JavaModelException
    {
        if(! compilationUnit.isOpen())
        {
            compilationUnit.open(new NullProgressMonitor());
        }
    }

    private String getSource(ICompilationUnit compilationUnit) throws JavaModelException
    {
        return compilationUnit.getSource();
    }

    private void applyTemplate(final CodeTemplate codeTemplate, IType type, IDocument document, String source) throws JavaModelException, BadLocationException, org.eclipse.jface.text.templates.TemplateException
    {
        TemplateContextType contextType = JavaPlugin.getDefault().getTemplateContextRegistry().getContextType(JavaContextType.ID_ALL);

        JavaContext javaContext = new JavaContext(contextType, document, 0, source.length(), type.getCompilationUnit())
        {
            @Override
            public String getKey()
            {
                return codeTemplate.id();
            }
        };

        Template template = new Template(codeTemplate.id(), "", JavaContextType.ID_ALL, codeTemplate.pattern(), false);

        if(! javaContext.canEvaluate(template))
        {
            System.err.println("Cannot evaluate the template");
            return;
        }

        TemplateBuffer templateBuffer = javaContext.evaluate(template);

        if(templateBuffer == null)
        {
            System.err.println("null templateBuffer");
            return;
        }

        int offset = type.getSourceRange().getOffset();
        new InsertEdit(offset, templateBuffer.getString()).apply(document);

        System.out.println(templateBuffer.getString());
    }

    private void commitChanges(IDocument document, ICompilationUnit compilationUnit) throws JavaModelException
    {
        ICompilationUnit wc = compilationUnit.getWorkingCopy(new NullProgressMonitor());
        wc.getBuffer().setContents(document.get());
        wc.reconcile(ICompilationUnit.NO_AST, false, null, null);
        wc.commitWorkingCopy(true, new NullProgressMonitor());
    }
}
