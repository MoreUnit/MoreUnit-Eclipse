package org.moreunit.mock;

import java.io.InputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
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
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.moreunit.mock.templates.CodeTemplate;
import org.moreunit.mock.templates.MockingTemplate;
import org.moreunit.mock.templates.MockingTemplates;
import org.moreunit.mock.templates.XmlTemplateDefinitionReader;

public class POC
{

    public void test()
    {
        MockingTemplates templates = readTemplates("/templates/");
        if(templates == null)
        {
            System.err.println("null templates");
            return;
        }
        printTemplates(templates);

        IType type = findType("pack.ATest");
        if(type == null)
        {
            System.err.println("null type");
            return;
        }

        for (CodeTemplate codeTemplate : templates.iterator().next().codeTemplates())
        {
            applyTemplate(type, codeTemplate);
        }
    }

    public MockingTemplates readTemplates(String templateDirectory)
    {
        InputStream is = getClass().getResourceAsStream(templateDirectory + "mockitoWithAnnotationsAndJUnitRunner.xml");
        if(is == null)
        {
            System.err.println("is null");
            return null;
        }

        try
        {
            return new XmlTemplateDefinitionReader().read(is);
        }
        catch (org.moreunit.mock.templates.TemplateException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private IType findType(String typeName)
    {
        try
        {
            IProject project = JavaPlugin.getWorkspace().getRoot().getProjects()[0];
            IJavaProject javaProject = JavaCore.create(project);
            return javaProject.findType(typeName);
        }
        catch (JavaModelException e2)
        {
            e2.printStackTrace();
        }
        return null;
    }

    private void printTemplates(MockingTemplates templates)
    {
        System.out.println("MockingTemplates: " + templates);
        for (MockingTemplate mockingTemplate : templates)
        {
            System.out.println(mockingTemplate);
            for (CodeTemplate codeTemplate : mockingTemplate.codeTemplates())
            {
                System.out.println(codeTemplate);
            }
        }
    }

    private void applyTemplate(IType type, final CodeTemplate codeTemplate)
    {
        ICompilationUnit compilationUnit = type.getCompilationUnit();

        System.err.println("open? " + compilationUnit.isOpen());
        if(! compilationUnit.isOpen())
        {
            try
            {
                compilationUnit.open(new NullProgressMonitor());
            }
            catch (JavaModelException e)
            {
                e.printStackTrace();
                return;
            }
        }

        System.err.println("open? " + compilationUnit.isOpen());
        try
        {
            Document document = new Document(getSource(compilationUnit));
            applyTemplate(type, codeTemplate, document);
            ICompilationUnit wc = compilationUnit.getWorkingCopy(new NullProgressMonitor());
            wc.getBuffer().setContents(document.get());
            wc.reconcile(ICompilationUnit.NO_AST, false, null, null);
            wc.commitWorkingCopy(true, new NullProgressMonitor());
        }
        catch (JavaModelException e1)
        {
            e1.printStackTrace();
            return;
        }
    }

    private void applyTemplate(IType type, final CodeTemplate codeTemplate, IDocument document) throws JavaModelException
    {
        TemplateContextType contextType = JavaPlugin.getDefault().getTemplateContextRegistry().getContextType(JavaContextType.ID_ALL);

        String source = getSource(type.getCompilationUnit());
        if(source == null)
        {
            System.err.println("null source");
            return;
        }

        JavaContext javaContext = new JavaContext(contextType, document, 0, source.length(), type.getCompilationUnit())
        {
            @Override
            public String getKey()
            {
                return codeTemplate.id();
            }
        };

        String pattern = codeTemplate.pattern();
        Template template = new Template(codeTemplate.id(), "", JavaContextType.ID_ALL, pattern, false);

        if(! javaContext.canEvaluate(template))
        {
            System.err.println("Cannot evaluate the template");
            return;
        }

        TemplateBuffer templateBuffer = null;
        try
        {
            templateBuffer = javaContext.evaluate(template);
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
        catch (TemplateException e)
        {
            e.printStackTrace();
        }

        if(templateBuffer == null)
        {
            System.err.println("null templateBuffer");
            return;
        }

        try
        {
            // int offset = type.getNameRange().getOffset();
            // System.out.println("name offset: " + offset);
            new InsertEdit(15, templateBuffer.getString()).apply(document);
        }
        catch (MalformedTreeException e)
        {
            e.printStackTrace();
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
        System.out.println(templateBuffer.getString());
    }

    private String getSource(ICompilationUnit cu)
    {
        try
        {
            return cu.getSource();
        }
        catch (JavaModelException e1)
        {
            e1.printStackTrace();
            return null;
        }
    }
}
