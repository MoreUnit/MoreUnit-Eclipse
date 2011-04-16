package org.moreunit.mock.templates;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.templates.TemplateException;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.model.CodeTemplate;
import org.moreunit.mock.model.MockingTemplate;

import com.google.inject.Inject;

public class TemplateProcessor
{
    private final ContextFactory contextFactory;
    private final SourceFormatter sourceFormatter;

    @Inject
    public TemplateProcessor(ContextFactory contextFactory, SourceFormatter sourceFormatter)
    {
        this.contextFactory = contextFactory;
        this.sourceFormatter = sourceFormatter;
    }

    public void applyTemplate(MockingTemplate mockingTemplate, Dependencies dependencies, IType classUnderTest, IType testCase) throws MockingTemplateException
    {
        ICompilationUnit testCaseCu = testCase.getCompilationUnit();
        try
        {
            ICompilationUnit workingCopy = createWorkingCopy(testCaseCu);

            MockingContext context = contextFactory.createMockingContext(dependencies, classUnderTest, workingCopy);
            if(! context.hasDependenciesToMock())
            {
                throw new NoDependenciesToMockException(classUnderTest);
            }

            context.prepareContext(mockingTemplate, this);

            applyTemplate(mockingTemplate, context);

            setSource(testCaseCu, sourceFormatter.getFormattedSource(workingCopy));
        }
        catch (Exception e)
        {
            if(e instanceof MockingTemplateException)
            {
                throw (MockingTemplateException) e;
            }
            throw new MockingTemplateException(e);
        }
    }

    private ICompilationUnit createWorkingCopy(ICompilationUnit compilationUnit) throws JavaModelException
    {
        if(! compilationUnit.isOpen())
        {
            compilationUnit.open(new NullProgressMonitor());
        }
        return compilationUnit.getWorkingCopy(new NullProgressMonitor());
    }

    private void applyTemplate(MockingTemplate mockingTemplate, MockingContext context) throws JavaModelException, BadLocationException, TemplateException, MockingTemplateException
    {
        for (CodeTemplate codeTemplate : mockingTemplate.codeTemplates())
        {
            if(codeTemplate.isIncluded(context))
            {
                applyTemplate(codeTemplate, context);
            }
        }
    }

    void applyTemplate(final CodeTemplate codeTemplate, MockingContext globalContext) throws JavaModelException, BadLocationException, org.eclipse.jface.text.templates.TemplateException, MockingTemplateException
    {
        EclipseTemplate eclipseTemplate = globalContext.preEvaluate(codeTemplate);
        contextFactory.createEclipseTemplateContext(globalContext).evaluate(eclipseTemplate);
    }

    private void setSource(ICompilationUnit compilationUnit, String source) throws JavaModelException
    {
        ICompilationUnit wc = createWorkingCopy(compilationUnit);
        wc.getBuffer().setContents(source);
        wc.commitWorkingCopy(false, new NullProgressMonitor());
    }
}
