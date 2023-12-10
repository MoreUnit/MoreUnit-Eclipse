package org.moreunit.ui;

import java.util.Arrays;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.dialogs.OpenResourceDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.util.PluginTools;

/**
 * @author vera
 */
public class MissingTestmethodViewPart extends PageBookView
{

    MethodPage activePage;

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
        MessagePage page = new EmptyPage();
        initPage(page);
        page.createControl(book);
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

    static class EmptyPage extends MessagePage {
        private Composite fControl;

        @Override
        public void createControl(Composite parent) {
            Color background= parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);

            Composite composite= new Composite(parent, SWT.NONE);
            composite.setLayout(new GridLayout(1, false));

            composite.setBackground(background);

            Link link= new Link(composite, SWT.NONE);
            link.setText("No Java editor opened. Open a <a>Java file</a>...");
            link.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
            link.setBackground(background);
            link.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    openResource();
                }
            });

            fControl= composite;
        }

        protected void openResource()
        {
            final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if(window == null)
            {
                return;
            }
            final Shell parent = window.getShell();
            final IContainer input = ResourcesPlugin.getWorkspace().getRoot();

            final OpenResourceDialog dialog = new OpenResourceDialog(parent, input, IResource.FILE);
            dialog.setInitialPattern("*.java");
            if(dialog.open() != Window.OK)
            {
                return;
            }

            Object[] result = dialog.getResult();
            if(result == null)
            {
                return;
            }
            var files = Arrays.stream(result).filter(IFile.class::isInstance).map(IFile.class::cast).toList();
            if(files.isEmpty())
            {
                return;
            }
            final IWorkbenchPage page = window.getActivePage();
            if(page == null)
            {
                return;
            }

            try
            {
                for (IFile iFile : files)
                {
                    IDE.openEditor(page, iFile, true);
                }
            }
            catch (final PartInitException e)
            {
                // ignore
            }
        }

        @Override
        public Control getControl() {
            return fControl;
        }

        @Override
        public void setFocus() {
            if (fControl != null)
                fControl.setFocus();
        }

    }

}
