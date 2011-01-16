package org.moreunit.mock.actions;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.moreunit.mock.elements.TypeFacadeFactory;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.templates.MockingTemplate;
import org.moreunit.mock.templates.MockingTemplateStore;
import org.moreunit.mock.templates.TemplateException;
import org.moreunit.mock.templates.TemplateProcessor;
import org.moreunit.mock.utils.ConversionUtils;

import com.google.inject.Inject;

public class MockDependenciesAction implements IEditorActionDelegate
{
    private final MockingTemplateStore mockingTemplateStore;
    private final TemplateProcessor templateApplicator;
    private final ConversionUtils conversionUtils;
    private final TypeFacadeFactory facadeFactory;
    private final Logger logger;
    private ICompilationUnit compilationUnit;

    @Inject
    public MockDependenciesAction(MockingTemplateStore mockingTemplateStore, TemplateProcessor templateApplicator, ConversionUtils conversionUtils, TypeFacadeFactory facadeFactory, Logger logger)
    {
        this.mockingTemplateStore = mockingTemplateStore;
        this.templateApplicator = templateApplicator;
        this.conversionUtils = conversionUtils;
        this.facadeFactory = facadeFactory;
        this.logger = logger;
    }

    public void setActiveEditor(IAction action, IEditorPart targetEditor)
    {
        compilationUnit = targetEditor == null ? null : conversionUtils.getCompilationUnit(targetEditor);
    }

    public void run(IAction action)
    {
        MockingTemplate template = getTemplate();
        if(template == null)
        {
            // MSG
            return;
        }

        IType testCase = getTestCaseType(compilationUnit);
        if(testCase != null) // selection canceled by user
        {
            try
            {
                templateApplicator.applyTemplate(template, testCase);
            }
            catch (TemplateException e)
            {
                // MSG
                logger.error("Could not apply " + template + " to " + testCase.getElementName(), e);
            }
        }
    }

    private MockingTemplate getTemplate()
    {
        final String templateId = MockingTemplateStore.DEFAULT_TEMPLATE;

        MockingTemplate template = mockingTemplateStore.get(templateId);
        if(template == null)
        {
            logger.error("Template not found: " + templateId);
        }

        logger.debug("MockDependenciesAction: retrieved template: " + template);
        return template;
    }

    private IType getTestCaseType(ICompilationUnit editedCompilationUnit)
    {
        if(facadeFactory.isTestCase(editedCompilationUnit))
        {
            return editedCompilationUnit.findPrimaryType();
        }
        else
        {
            return facadeFactory.createClassFacade(editedCompilationUnit).getOneCorrespondingTestCase(true, "Mock dependencies in...");
        }
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
        // nothing to do
    }
}
