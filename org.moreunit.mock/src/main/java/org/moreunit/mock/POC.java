package org.moreunit.mock;

import java.io.InputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.moreunit.mock.templates.CodeTemplate;
import org.moreunit.mock.templates.MockingTemplate;
import org.moreunit.mock.templates.MockingTemplates;
import org.moreunit.mock.templates.TemplateApplicator;
import org.moreunit.mock.templates.TemplateException;
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

        try
        {
            new TemplateApplicator().applyTemplate(templates.iterator().next(), type);
        }
        catch (TemplateException e)
        {
            e.printStackTrace();
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
}
