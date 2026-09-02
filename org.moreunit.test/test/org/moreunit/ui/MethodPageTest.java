package org.moreunit.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IActionBars;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.IPageSite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.elements.TestmethodCreator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.moreunit.elements.MethodTreeContentProvider;
import org.moreunit.properties.SwtPageTestCase;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit3Project;

/**
 * Tests {@link MethodPage} with real SWT widgets.
 */
@Context(SimpleJUnit3Project.class)
public class MethodPageTest extends SwtPageTestCase
{
    private MethodPage page;
    private EditorPartFacade editorPartFacade;

    @BeforeEach
    public void createPage()
    {
        IType cut = context.getPrimaryTypeHandler("org.SomeClass").get();
        editorPartFacade = facadeShowing(cut);

        page = new MethodPage(editorPartFacade);
    }

    @AfterEach
    public void disposePage()
    {
        // the page registers itself as JavaCore element changed listener:
        // it must always be disposed, otherwise stale pages NPE later
        if(page != null)
        {
            try
            {
                page.dispose();
            }
            catch (RuntimeException e)
            {
                // ignore
            }
        }
    }

    private void createPageControl()
    {
        IPageSite site = mock(IPageSite.class);
        IActionBars actionBars = mock(IActionBars.class);
        IToolBarManager toolBarManager = new ToolBarManager();
        when(site.getActionBars()).thenReturn(actionBars);
        when(actionBars.getToolBarManager()).thenReturn(toolBarManager);
        page.init(site);

        page.createControl(shell);
    }

    private EditorPartFacade facadeShowing(IType type)
    {
        IFile file = (IFile) type.getCompilationUnit().getResource();
        IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn(file);
        IEditorPart editorPart = mock(IEditorPart.class);
        when(editorPart.getEditorInput()).thenReturn(editorInput);
        return new EditorPartFacade(editorPart);
    }

    private void flushDisplayEvents()
    {
        while (display.readAndDispatch())
        {
        }
    }

    @Test
    public void should_return_null_control_when_not_created_yet()
    {
        assertNull(page.getControl());
    }

    @Test
    public void should_create_tree_control_and_expose_input_type()
    {
        createPageControl();

        assertNotNull(page.getControl());
        assertEquals(editorPartFacade.getCompilationUnit().findPrimaryType(), page.getInputType());

        page.setFocus();
    }

    @Test
    public void should_filter_private_methods_when_corresponding_action_is_run()
    {
        createPageControl();
        context.getPrimaryTypeHandler("org.SomeClass").addMethod("private String secretOperation()", "return \"secret\";");
        page.updateUI();

        Action filterPrivateAction = (Action) getField(page, "filterPrivateAction");
        filterPrivateAction.setChecked(true);
        filterPrivateAction.run();

        assertTrue(filterPrivateAction.isChecked());
        MethodTreeContentProvider contentProvider = (MethodTreeContentProvider) getField(page, "methodTreeContentProvider");
        TreeViewer treeViewer = (TreeViewer) getField(page, "treeViewer");
        Object[] shownElements = contentProvider.getElements(treeViewer.getInput());
        for (Object element : shownElements)
        {
            IJavaElement javaElement = (IJavaElement) element;
            assertFalse("secretOperation".equals(javaElement.getElementName()));
        }
    }

    @Test
    public void should_filter_getter_methods_when_corresponding_action_is_run()
    {
        createPageControl();
        IType cut = context.getPrimaryTypeHandler("org.SomeClass").get();
        try
        {
            cut.createField("private String name;", null, false, new org.eclipse.core.runtime.NullProgressMonitor());
        }
        catch (org.eclipse.core.runtime.CoreException e)
        {
            throw new RuntimeException(e);
        }
        context.getPrimaryTypeHandler("org.SomeClass").addMethod("public String getName()", "return \"name\";");
        page.updateUI();

        Action filterGetterAction = (Action) getField(page, "filterGetterAction");
        filterGetterAction.setChecked(true);
        filterGetterAction.run();

        assertTrue(filterGetterAction.isChecked());
        MethodTreeContentProvider contentProvider = (MethodTreeContentProvider) getField(page, "methodTreeContentProvider");
        TreeViewer treeViewer = (TreeViewer) getField(page, "treeViewer");
        Object[] shownElements = contentProvider.getElements(treeViewer.getInput());
        for (Object element : shownElements)
        {
            IJavaElement javaElement = (IJavaElement) element;
            assertFalse("getName".equals(javaElement.getElementName()));
        }
    }

    @Test
    public void should_update_ui_when_a_compilation_unit_changed()
    {
        createPageControl();
        TreeViewer treeViewer = (TreeViewer) getField(page, "treeViewer");
        assertFalse(treeViewer.getControl().isDisposed());

        page.elementChanged(eventWithElementType(IJavaElement.COMPILATION_UNIT));
        flushDisplayEvents();

        assertFalse(treeViewer.getControl().isDisposed());
    }

    @Test
    public void should_ignore_element_changes_of_other_types()
    {
        createPageControl();

        page.elementChanged(eventWithElementType(IJavaElement.TYPE));

        // no asynchronous update should have been scheduled
        assertFalse(((TreeViewer) getField(page, "treeViewer")).getControl().isDisposed());
    }

    @Test
    public void should_replace_editor_part_facade()
    {
        createPageControl();

        IType testType = context.getPrimaryTypeHandler("org.SomeClassTest").get();
        page.setNewEditorPartFacade(facadeShowing(testType));

        assertEquals(testType, page.getInputType());
    }

    @Test
    public void should_dispose_without_error()
    {
        createPageControl();

        page.dispose();

        assertTrue(page.getControl().isDisposed());
    }

    private ElementChangedEvent eventWithElementType(int elementType)
    {
        IJavaElement element = mock(IJavaElement.class);
        when(element.getElementType()).thenReturn(elementType);
        IJavaElementDelta delta = mock(IJavaElementDelta.class);
        when(delta.getElement()).thenReturn(element);
        return new ElementChangedEvent(delta, ElementChangedEvent.POST_CHANGE);
    }

    @Test
    public void should_do_nothing_when_add_test_action_runs_without_selection() throws Exception
    {
        createPageControl();

        invoke(page, "addItem");

        assertEquals(0, context.getPrimaryTypeHandler("org.SomeClassTest").get().getMethods().length);
    }

    @Test
    public void should_create_test_method_for_the_selected_method_when_add_test_action_runs() throws Exception
    {
        TestmethodCreator.discardExtensions = true;
        createPageControl();
        context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumberOne()", "return 1;");
        page.updateUI();
        flushDisplayEvents();

        TreeViewer treeViewer = (TreeViewer) getField(page, "treeViewer");
        org.eclipse.swt.widgets.Tree tree = treeViewer.getTree();
        org.eclipse.swt.widgets.TreeItem item = treeItemShowing(tree, "getNumberOne");
        assertNotNull(item);
        tree.select(item);

        invoke(page, "addItem");

        org.eclipse.jdt.core.IMethod[] testMethods = context.getPrimaryTypeHandler("org.SomeClassTest").get().getMethods();
        assertEquals(1, testMethods.length);
        assertEquals("testGetNumberOne", testMethods[0].getElementName());
    }

    @Test
    public void should_open_editor_on_double_click() throws Exception
    {
        createPageControl();
        context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumberOne()", "return 1;");
        page.updateUI();
        flushDisplayEvents();

        TreeViewer treeViewer = (TreeViewer) getField(page, "treeViewer");
        org.eclipse.swt.widgets.Tree tree = treeViewer.getTree();
        org.eclipse.swt.widgets.TreeItem item = treeItemShowing(tree, "getNumberOne");
        assertNotNull(item);
        tree.select(item);

        org.eclipse.ui.IWorkbenchPage workbenchPage = org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try
        {
            page.doubleClick(new DoubleClickEvent(treeViewer, treeViewer.getSelection()));

            flushDisplayEvents();
            org.eclipse.ui.IEditorPart editor = workbenchPage.getActiveEditor();
            assertNotNull(editor, "double click should have opened an editor");
        }
        finally
        {
            workbenchPage.closeAllEditors(false);
        }
    }

    private org.eclipse.swt.widgets.TreeItem treeItemShowing(org.eclipse.swt.widgets.Tree tree, String text)
    {
        for (org.eclipse.swt.widgets.TreeItem item : tree.getItems())
        {
            if(item.getText().contains(text))
            {
                return item;
            }
        }
        return null;
    }
}
