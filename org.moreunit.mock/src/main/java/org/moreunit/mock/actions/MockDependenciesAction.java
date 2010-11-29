package org.moreunit.mock.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.moreunit.mock.MoreUnitMockPlugin;
import org.moreunit.mock.POC;
import org.moreunit.mock.templates.MockingTemplate;
import org.moreunit.mock.templates.MockingTemplateStore;

public class MockDependenciesAction implements IEditorActionDelegate
{
    private final MockingTemplateStore mockingTemplateStore;
    private final MoreUnitMockPlugin mockPlugin;

    public MockDependenciesAction()
    {
        this(MoreUnitMockPlugin.getDefault().getTemplateStore(), MoreUnitMockPlugin.getDefault());
    }

    public MockDependenciesAction(MockingTemplateStore mockingTemplateStore, MoreUnitMockPlugin mockPlugin)
    {
        this.mockPlugin = mockPlugin;
        this.mockingTemplateStore = mockingTemplateStore;
    }

    public void setActiveEditor(IAction action, IEditorPart targetEditor)
    {
    }

    public void run(IAction action)
    {
        MockingTemplate template = mockingTemplateStore.get(mockPlugin.getDefaultTemplateId());
        System.out.println(template);
        new POC().test();
    }

    public void selectionChanged(IAction arg0, ISelection arg1)
    {
        // nothing to do
    }
}
