package org.moreunit.ui;

import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.util.PluginTools;

/**
 * @author vera
 */
public class MissingTestmethodViewPart extends PageBookView
{

    MethodPage activePage;

    public MissingTestmethodViewPart()
    {
        super();

        setTitleImage(MoreUnitPlugin.getImageDescriptor("icons/moreunitLogo.gif").createImage());
    }

    @Override
    public void createPartControl(Composite parent)
    {
        super.createPartControl(parent);
    }

    @Override
    public void setFocus()
    {
    }

    @Override
    protected IPage createDefaultPage(PageBook book)
    {
        MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage("kein Outline");
        return page;
    }

    @Override
    protected PageRec doCreatePage(IWorkbenchPart part)
    {
        if(activePage == null)
        {
            activePage = new MethodPage(new EditorPartFacade((IEditorPart) part));
            initPage(activePage);
            activePage.createControl(getPageBook());
        }
        else
        {
            activePage.setNewEditorPartFacade(new EditorPartFacade((IEditorPart) part));
            initPage(activePage);
        }
        return new PageRec(part, activePage);
    }

    @Override
    protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord)
    {
        pageRecord.dispose();
        if(activePage != null)
        {
            activePage.dispose();
            activePage = null;
        }
    }

    @Override
    protected IWorkbenchPart getBootstrapPart()
    {
        return null;
    }

    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return (PluginTools.isJavaFile(part));
    }

    @Override
    public void partOpened(IWorkbenchPart part)
    {
        super.partOpened(part);

        // on startup the view should become synchronized with the open file
        if(part instanceof MissingTestmethodViewPart)
        {
            IEditorPart openEditorPart = PluginTools.getOpenEditorPart();
            if(openEditorPart != null)
            {
                super.partActivated(openEditorPart);
                if(activePage != null)
                    activePage.updateUI();
            }
        }
    }

    @Override
    public void partActivated(IWorkbenchPart part)
    {
        if(activePage == null)
        {
            super.partActivated(part);
        }
        else if(PluginTools.isJavaFile(part))
        {
            // only if a different java file is activated, do something
            EditorPartFacade editorPartFacade = new EditorPartFacade((IEditorPart) part);
            IType primaryType = editorPartFacade.getCompilationUnit().findPrimaryType();
            if(primaryType != null && ! primaryType.equals(activePage.getInputType()))
            {
                activePage.setNewEditorPartFacade(editorPartFacade);
                initPage(activePage);
            }
        }
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part)
    {
        if(part instanceof EditorPart)
        {

            partActivated(part);
            if(activePage != null)
            {
                activePage.updateUI();
            }

            // Bugfix for #2869899
            if(getCurrentPage() != activePage && isImportant(part))
            {
                PageRec pageRec = getPageRec(activePage);
                showPageRec(pageRec);
            }
        }
    }

    @Override
    public void partClosed(IWorkbenchPart part)
    {
        super.partClosed(part);

        if(part instanceof IEditorPart)
        {
            IEditorPart openEditorPart = PluginTools.getOpenEditorPart();
            if(openEditorPart != null && activePage != null)
            {
                super.partActivated(openEditorPart);
                activePage.updateUI();
            }
        }
    }
}
