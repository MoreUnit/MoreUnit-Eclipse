package org.moreunit.mock.templates;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateException;

public class TemplateProcessor
{
    public void applyTemplate(MockingTemplate mockingTemplate, IType type) throws org.moreunit.mock.templates.TemplateException
    {
        ICompilationUnit compilationUnit = type.getCompilationUnit();
        try
        {
            open(compilationUnit);
            compilationUnit.becomeWorkingCopy(new NullProgressMonitor());

            for (CodeTemplate codeTemplate : mockingTemplate.codeTemplates())
            {
                // TODO Nicolas: hack so I can already use the plug-in
                if(Part.BEFORE_INSTANCE_METHOD == Part.fromId(codeTemplate.part()))
                {
                    codeTemplate = new CodeTemplate(codeTemplate.id(), codeTemplate.part(), "@${beforeAnnotation:newType(org.junit.Before)} public void setUp() {" + codeTemplate.pattern() + "}");
                }

                applyTemplate(codeTemplate, compilationUnit);
            }

            compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
        }
        catch (Exception e)
        {
            try
            {
                compilationUnit.discardWorkingCopy();
            }
            catch (JavaModelException jme)
            {
                // ignored TODO Nicolas: log
            }
            throw new org.moreunit.mock.templates.TemplateException(e);
        }
    }

    private void open(ICompilationUnit compilationUnit) throws JavaModelException
    {
        if(! compilationUnit.isOpen())
        {
            compilationUnit.open(new NullProgressMonitor());
        }
    }

    private void applyTemplate(final CodeTemplate codeTemplate, ICompilationUnit compilationUnit) throws JavaModelException, BadLocationException, org.eclipse.jface.text.templates.TemplateException, org.moreunit.mock.templates.TemplateException
    {
        IDocument document = new Document(compilationUnit.getSource());
        int insertionOffset = getInsertionOffset(codeTemplate, compilationUnit);
        TestCaseTemplateContext context = new TestCaseTemplateContext(document, insertionOffset, compilationUnit);

        Template template = new TestCaseTemplate(codeTemplate);
        if(! context.canEvaluate(template))
        {
            throw new org.moreunit.mock.templates.TemplateException("Cannot evaluate the template");
        }

        // this may already modify the document (e.g. add imports)
        TemplateBuffer templateBuffer = context.evaluate(template);
        reconcile(compilationUnit);

        // takes modifications made by context.evaluate() into account
        document = new Document(compilationUnit.getSource());
        insertionOffset = getInsertionOffset(codeTemplate, compilationUnit);

        document.replace(insertionOffset, 0, templateBuffer.getString());

        compilationUnit.getBuffer().setContents(document.get());
        reconcile(compilationUnit);
    }

    private void reconcile(ICompilationUnit compilationUnit) throws JavaModelException
    {
        compilationUnit.reconcile(ICompilationUnit.NO_AST, true, null, null);
    }

    // TODO Nicolas: rewrite this in an object way
    private int getInsertionOffset(CodeTemplate codeTemplate, ICompilationUnit compilationUnit) throws JavaModelException, TemplateException
    {
        IType type = compilationUnit.findPrimaryType();
        Integer offset = null;

        Part part = Part.fromId(codeTemplate.part());
        switch (part)
        {
        case TEST_CLASS_ANNOTATION:
            offset = type.getSourceRange().getOffset();
            break;
        case TEST_CLASS_FIELDS:
            offset = afterLastFieldOffset(type);
            break;
        case BEFORE_INSTANCE_METHOD:
            offset = beforeFirstMethodOffset(type);
            break;
        default:
            throw new TemplateException("Unsupported part: " + part);
        }

        return offset != null ? offset : firstMemberOffset(type);
    }

    private Integer beforeFirstMethodOffset(IType type) throws JavaModelException
    {
        IMethod[] methods = type.getMethods();
        if(methods.length != 0)
        {
            return methods[0].getSourceRange().getOffset();
        }
        return null;
    }

    private Integer afterLastFieldOffset(IType type) throws JavaModelException
    {
        IField[] fields = type.getFields();
        if(fields.length != 0)
        {
            ISourceRange fieldRange = fields[fields.length - 1].getSourceRange();
            return fieldRange.getOffset() + fieldRange.getLength();
        }
        return null;
    }

    private int firstMemberOffset(IType type) throws JavaModelException
    {
        return type.getNameRange().getOffset() + type.getNameRange().getLength() + 3;
    }
}
