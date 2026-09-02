package org.moreunit.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.properties.SwtPageTestCase;
import org.moreunit.test.support.DialogHelper;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit3Project;

/**
 * Tests {@link MissingTestmethodViewPart} (and its {@code EmptyPage}) with
 * real SWT widgets.
 */
@Context(SimpleJUnit3Project.class)
public class MissingTestmethodViewPartTest extends SwtPageTestCase
{
    private static final String OUTLINE_VIEW_ID = "org.eclipse.ui.views.ContentOutline";

    private TestableViewPart view;
    private IType cutType;
    private IViewPart siteProviderView;

    private static class TestableViewPart extends MissingTestmethodViewPart
    {
        TestableViewPart(IViewSite site)
        {
            setSite(site);
        }

        Object createPageFor(IWorkbenchPart part)
        {
            return doCreatePage(part);
        }

        Object createAndDestroyPageFor(IWorkbenchPart part)
        {
            org.eclipse.ui.part.PageBookView.PageRec rec = doCreatePage(part);
            Object page = rec == null ? null : rec.page;
            if(rec != null)
            {
                doDestroyPage(part, rec);
            }
            return page;
        }

        boolean isPartImportant(IWorkbenchPart part)
        {
            return isImportant(part);
        }

        Object bootstrapPart()
        {
            return getBootstrapPart();
        }

    }

    @BeforeEach
    public void createView() throws PartInitException
    {
        cutType = context.getPrimaryTypeHandler("org.SomeClass").get();
        // a real IViewSite (a PageSite cannot be built on a mock) is required
        // to initialize the pages of this PageBookView
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        siteProviderView = page.showView(OUTLINE_VIEW_ID);
        view = new TestableViewPart(siteProviderView.getViewSite());
    }

    @AfterEach
    public void disposeView()
    {
        if(view != null)
        {
            view.dispose();
        }
        siteProviderView = null;
    }

    private IEditorPart mockEditorPartShowing(IType type)
    {
        IFile file = (IFile) type.getCompilationUnit().getResource();
        IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn(file);
        IEditorPart editorPart = mock(IEditorPart.class);
        when(editorPart.getEditorInput()).thenReturn(editorInput);
        return editorPart;
    }

    @Test
    public void should_create_default_page_book_with_empty_page()
    {
        view.createPartControl(shell);

        IPage currentPage = view.getCurrentPage();
        assertNotNull(currentPage);
    }

    @Test
    public void should_not_be_important_for_non_editor_parts()
    {
        assertFalse(view.isPartImportant(mock(IWorkbenchPart.class)));
    }

    @Test
    public void should_not_be_important_for_editors_not_showing_a_file()
    {
        IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn(null);
        IEditorPart editorPart = mock(IEditorPart.class);
        when(editorPart.getEditorInput()).thenReturn(editorInput);

        assertFalse(view.isPartImportant(editorPart));
    }

    @Test
    public void should_not_be_important_for_editors_not_showing_a_java_file()
    {
        IFile file = (IFile) cutType.getCompilationUnit().getResource();
        IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn(file.getWorkspace().getRoot().getFile(file.getFullPath().removeFileExtension().addFileExtension("txt")));
        IEditorPart editorPart = mock(IEditorPart.class);
        when(editorPart.getEditorInput()).thenReturn(editorInput);

        assertFalse(view.isPartImportant(editorPart));
    }

    @Test
    public void should_be_important_for_editors_showing_a_java_file()
    {
        assertTrue(view.isPartImportant(mockEditorPartShowing(cutType)));
    }

    @Test
    public void should_have_no_bootstrap_part()
    {
        assertNull(view.bootstrapPart());
    }

    @Test
    public void should_create_method_page_for_java_editor()
    {
        view.createPartControl(shell);

        IEditorPart editorPart = mockEditorPartShowing(cutType);
        Object page = view.createAndDestroyPageFor(editorPart);

        assertEquals(cutType, ((MethodPage) page).getInputType());
    }

    @Test
    public void should_reuse_active_page_for_another_editor()
    {
        view.createPartControl(shell);

        IEditorPart editorPart = mockEditorPartShowing(cutType);
        view.createPageFor(editorPart);
        Object activePage = getField(view, "activePage");

        IEditorPart otherEditorPart = mockEditorPartShowing(cutType);
        view.createPageFor(otherEditorPart);
        Object activePageAfter = getField(view, "activePage");

        assertSame(activePage, activePageAfter);
    }

    @Test
    public void should_destroy_page_and_reset_active_page()
    {
        view.createPartControl(shell);

        IEditorPart editorPart = mockEditorPartShowing(cutType);
        view.createPageFor(editorPart);
        assertNotNull(getField(view, "activePage"));

        view.createAndDestroyPageFor(editorPart);

        assertNull(getField(view, "activePage"));
    }

    @Test
    public void should_handle_part_activated_with_non_important_part()
    {
        view.createPartControl(shell);

        // must not throw and must not create a page
        view.partActivated(mock(IWorkbenchPart.class));

        assertNull(getField(view, "activePage"));
    }

    @Test
    public void should_update_page_when_a_different_java_editor_is_activated()
    {
        view.createPartControl(shell);

        IEditorPart firstEditor = mockEditorPartShowing(cutType);
        view.createPageFor(firstEditor);
        MethodPage activePage = (MethodPage) getField(view, "activePage");

        IType testType = context.getPrimaryTypeHandler("org.SomeClassTest").get();
        view.partActivated(mockEditorPartShowing(testType));

        assertEquals(testType, activePage.getInputType());
    }

    @Test
    public void should_handle_part_activated_with_same_java_editor()
    {
        view.createPartControl(shell);

        IEditorPart firstEditor = mockEditorPartShowing(cutType);
        view.createPageFor(firstEditor);
        MethodPage activePage = (MethodPage) getField(view, "activePage");

        // same file: nothing should change
        view.partActivated(mockEditorPartShowing(cutType));

        assertEquals(cutType, activePage.getInputType());
    }

    @Test
    public void should_handle_editor_part_brought_to_top()
    {
        view.createPartControl(shell);

        EditorPart editorPart = mock(EditorPart.class);
        IFile file = (IFile) cutType.getCompilationUnit().getResource();
        IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn(file);
        when(editorPart.getEditorInput()).thenReturn(editorInput);

        view.partBroughtToTop(editorPart);

        assertNotNull(getField(view, "activePage"));
    }

    @Test
    public void should_ignore_part_brought_to_top_for_non_editor_parts()
    {
        view.createPartControl(shell);

        view.partBroughtToTop(mock(IWorkbenchPart.class));

        assertNull(getField(view, "activePage"));
    }

    @Test
    public void should_handle_part_opened_of_any_part()
    {
        view.createPartControl(shell);

        view.partOpened(mock(IWorkbenchPart.class));
        view.partOpened(view);

        assertNotSame(view, view.getCurrentPage());
    }

    @Test
    public void should_handle_part_closed_of_editors()
    {
        view.createPartControl(shell);

        view.partClosed(mock(IEditorPart.class));
        view.partClosed(mock(IWorkbenchPart.class));
    }

    private void awaitActiveEditor(IWorkbenchPage page) throws InterruptedException
    {
        long deadline = System.currentTimeMillis() + 10_000;
        while (page.getActiveEditor() == null && System.currentTimeMillis() < deadline)
        {
            while (Display.getDefault().readAndDispatch())
            {
            }
            Thread.sleep(20);
        }
    }

    @Test
    public void should_synchronize_with_the_open_editor_when_the_view_is_opened() throws Exception
    {
        view.createPartControl(shell);

        IFile file = (IFile) cutType.getCompilationUnit().getResource();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart editor = page.openEditor(new org.eclipse.ui.part.FileEditorInput(file), "org.eclipse.ui.DefaultTextEditor", true);
        try
        {
            assertNotNull(editor);
            awaitActiveEditor(page);

            view.partOpened(view);

            Object activePage = getField(view, "activePage");
            assertNotNull(activePage, "the view should have created a page for the open editor");
            assertEquals(cutType, ((MethodPage) activePage).getInputType());
        }
        finally
        {
            page.closeAllEditors(false);
        }
    }

    @Test
    public void should_refresh_with_the_remaining_editor_when_another_editor_is_closed() throws Exception
    {
        view.createPartControl(shell);

        IFile file = (IFile) cutType.getCompilationUnit().getResource();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart editor = page.openEditor(new org.eclipse.ui.part.FileEditorInput(file), "org.eclipse.ui.DefaultTextEditor", true);
        try
        {
            assertNotNull(editor);
            awaitActiveEditor(page);

            // create the page for a mocked editor showing the test case
            IType testType = context.getPrimaryTypeHandler("org.SomeClassTest").get();
            view.partActivated(mockEditorPartShowing(testType));
            MethodPage activePage = (MethodPage) getField(view, "activePage");
            assertEquals(testType, activePage.getInputType());

            // closing an editor that has no page must not throw, whatever the
            // currently active editor is
            view.partClosed(mock(IEditorPart.class));
            view.partClosed(mock(IWorkbenchPart.class));

            assertNotNull(getField(view, "activePage"));
        }
        finally
        {
            page.closeAllEditors(false);
        }
    }

    @Test
    public void should_open_and_close_resource_dialog_from_empty_page()
    {
        view.createPartControl(shell);

        IPage defaultPage = view.getCurrentPage();
        assertNotNull(defaultPage);

        Display workbenchDisplay = PlatformUI.getWorkbench().getDisplay();
        java.util.Set<Shell> knownShells = org.moreunit.test.support.DialogHelper.knownShells(workbenchDisplay);
        knownShells.remove(shell); // ignore the test shell, only look for new popups
        workbenchDisplay.asyncExec(org.moreunit.test.support.DialogHelper.closerFor(workbenchDisplay, knownShells, Shell::close, 300));

        // opens the (modal) OpenResourceDialog; the closer closes it right away
        invoke(defaultPage, "openResource");

        assertFalse(shell.isDisposed());
    }
}
