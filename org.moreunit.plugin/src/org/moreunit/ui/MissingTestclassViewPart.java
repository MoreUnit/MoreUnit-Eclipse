package org.moreunit.ui;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.util.PluginTools;

/**
 * @author zach
 */
public class MissingTestclassViewPart extends PageBookView
{
    private MissingClassPage missingClassPage;

    public MissingTestclassViewPart()
    {
        super();
    }

    @Override
    protected IPage createDefaultPage(PageBook book)
    {
        MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage("");
        return page;
    }

    @Override
    protected PageRec doCreatePage(IWorkbenchPart workbenchPart)
    {
        if(missingClassPage == null)
        {
            EditorPartFacade editorPartFacade = new EditorPartFacade((IEditorPart) workbenchPart);
            missingClassPage = new MissingClassPage(editorPartFacade.getCompilationUnit());
        }
        initPage(missingClassPage);
        missingClassPage.createControl(getPageBook());
        return new PageRec(workbenchPart, missingClassPage);
    }

    @Override
    protected void doDestroyPage(IWorkbenchPart arg0, PageRec arg1)
    {
    }

    @Override
    protected IWorkbenchPart getBootstrapPart()
    {
        return null;
    }

    @Override
    protected boolean isImportant(IWorkbenchPart workbenchPart)
    {
        return (PluginTools.isJavaFile(workbenchPart));
    }

    public boolean refreshWasProjectWasPressed()
    {
        return true;
    }

}
