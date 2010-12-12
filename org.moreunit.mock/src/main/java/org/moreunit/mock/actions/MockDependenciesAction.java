package org.moreunit.mock.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.mock.MoreUnitMockPlugin;
import org.moreunit.mock.POC;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.templates.MockingTemplate;
import org.moreunit.mock.templates.MockingTemplateStore;

public class MockDependenciesAction implements IEditorActionDelegate
{
    private static final MoreUnitMockPlugin MOCK_PLUGIN = MoreUnitMockPlugin.getDefault();

    private final MockingTemplateStore mockingTemplateStore;
    private final String defaultTemplateId;
    private ICompilationUnit compilationUnit;
    private Logger logger;

    public MockDependenciesAction()
    {
        this(MOCK_PLUGIN.getTemplateStore(), MOCK_PLUGIN.getDefaultTemplateId(), MOCK_PLUGIN.getLogger());
    }

    public MockDependenciesAction(MockingTemplateStore mockingTemplateStore, String defaultTemplateId, Logger logger)
    {
        this.defaultTemplateId = defaultTemplateId;
        this.mockingTemplateStore = mockingTemplateStore;
        this.logger = logger;
    }

    public void setActiveEditor(IAction action, IEditorPart targetEditor)
    {
        compilationUnit = createCompilationUnitFrom(targetEditor);
        System.out.println(compilationUnit);
    }

    private ICompilationUnit createCompilationUnitFrom(IEditorPart editorPart)
    {
        IFile file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
        return JavaCore.createCompilationUnitFrom(file);
    }

    public void run(IAction action)
    {
        final boolean pocActive = false;
        if(pocActive)
        {
            new POC().test();
            logger.warn("POC is active");
            return;
        }

        POC templateApplicator = new POC();

        MockingTemplate template = mockingTemplateStore.get(defaultTemplateId);
        logger.debug(template.toString());

        if(TypeFacade.isTestCase(compilationUnit.findPrimaryType()))
        {
            templateApplicator.applyTemplate(template, compilationUnit.findPrimaryType());
        }
        else
        {
            IType testCase = new ClassTypeFacade(compilationUnit).getOneCorrespondingTestCase(true, "Mock dependencies in...");
            if(testCase != null)
            {
                templateApplicator.applyTemplate(template, testCase.getCompilationUnit().findPrimaryType());
            }
        }
    }

    public void selectionChanged(IAction arg0, ISelection arg1)
    {
        // nothing to do
    }
}
