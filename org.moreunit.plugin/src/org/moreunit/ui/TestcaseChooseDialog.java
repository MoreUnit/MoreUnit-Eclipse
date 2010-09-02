package org.moreunit.ui;

import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.moreunit.log.LogHandler;
import org.moreunit.util.StringConstants;

/**
 * TODO delete this class as soon as Feature Requests 3036484 is completed.
 * @author vera
 */
public class TestcaseChooseDialog extends PopupDialog implements DisposeListener
{

    private TreeViewer treeViewer;
    private Set<IType> testcaseSet;
    private Object selectedElement;

    public TestcaseChooseDialog(String titleText, String infoText, Set<IType> testcaseSet)
    {
        //super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.PRIMARY_MODAL, true, false, false, false, StringConstants.EMPTY_STRING, StringConstants.EMPTY_STRING);
        super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.PRIMARY_MODAL, true, false, false, false, false, StringConstants.EMPTY_STRING, StringConstants.EMPTY_STRING);

        this.testcaseSet = testcaseSet;
        setInfoText(StringConstants.EMPTY_STRING);
        create();
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        treeViewer = createTreeViewer(parent);

        final Tree tree = treeViewer.getTree();
        tree.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                if(e.character == SWT.ESC)
                    close();
            }

            public void keyReleased(KeyEvent e)
            {
                // do nothing
            }
        });

        tree.addSelectionListener(new SelectionListener()
        {
            public void widgetSelected(SelectionEvent e)
            {
                // do nothing
            }

            public void widgetDefaultSelected(SelectionEvent e)
            {
                Object selectedElement = getSelectedElement();
                close();
                handleElementSelected(selectedElement);
            }
        });

        tree.addMouseMoveListener(new MouseMoveListener()
        {
            TreeItem fLastItem = null;

            public void mouseMove(MouseEvent e)
            {
                if(tree.equals(e.getSource()))
                {
                    //Object o = tree.getItem(new Point(e.x, e.y));
                    TreeItem o = tree.getItem(new Point(e.x, e.y));
                    //if(o instanceof TreeItem)
                    //{
                        if(! o.equals(fLastItem))
                        {
                            fLastItem = o;
                            tree.setSelection(new TreeItem[] { fLastItem });
                        }
                        else if(e.y < tree.getItemHeight() / 4)
                        {
                            // Scroll up
                            Point p = tree.toDisplay(e.x, e.y);
                            Item item = treeViewer.scrollUp(p.x, p.y);
                            if(item instanceof TreeItem)
                            {
                                fLastItem = (TreeItem) item;
                                tree.setSelection(new TreeItem[] { fLastItem });
                            }
                        }
                        else if(e.y > tree.getBounds().height - tree.getItemHeight() / 4)
                        {
                            // Scroll down
                            Point p = tree.toDisplay(e.x, e.y);
                            Item item = treeViewer.scrollDown(p.x, p.y);
                            if(item instanceof TreeItem)
                            {
                                fLastItem = (TreeItem) item;
                                tree.setSelection(new TreeItem[] { fLastItem });
                            }
                        }
                    //}
                }
            }
        });

        tree.addMouseListener(new MouseAdapter()
        {
            public void mouseUp(MouseEvent e)
            {

                if(tree.getSelectionCount() < 1)
                    return;

                if(e.button != 1)
                    return;

                if(tree.equals(e.getSource()))
                {
                    Object o = tree.getItem(new Point(e.x, e.y));
                    TreeItem selection = tree.getSelection()[0];
                    if(selection.equals(o))
                    {
                        Object selectedElement = getSelectedElement();
                        close();
                        handleElementSelected(selectedElement);
                    }
                }
            }
        });

        addDisposeListener(this);
        return treeViewer.getControl();
    }

    public Object getSelectedElement()
    {
        if(treeViewer == null)
            return null;

        return ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
    }

    private void handleElementSelected(Object selectedElement)
    {
        this.selectedElement = selectedElement;
    }

    private void addDisposeListener(DisposeListener listener)
    {
        getShell().addDisposeListener(listener);
    }

    public void widgetDisposed(DisposeEvent e)
    {
        treeViewer = null;
    }

    protected TreeViewer createTreeViewer(Composite parent)
    {
        TreeViewer viewer = new TreeViewer(parent);
        viewer.setContentProvider(new TestCaseContentProvider(testcaseSet));
        viewer.setLabelProvider(new JavaElementLabelProvider());
        viewer.setInput(this);
        return viewer;
    }

    //private class TestCaseContentProvider implements ITreeContentProvider
    private static class TestCaseContentProvider implements ITreeContentProvider
    {

        Object[] resultList;

        public TestCaseContentProvider(Set<IType> testcaseSet)
        {
            resultList = testcaseSet.toArray();
        }

        public Object[] getChildren(Object parentElement)
        {
            return null;
        }

        public Object getParent(Object element)
        {
            return null;
        }

        public boolean hasChildren(Object element)
        {
            return false;
        }

        public Object[] getElements(Object inputElement)
        {
            return resultList;
        }

        public void dispose()
        {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }

    }

    public IType getChoice()
    {
        open();
        runEventLoop(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        return (IType) selectedElement;
    }

    private void runEventLoop(Shell loopShell)
    {
        
        // NullSafe
        if (loopShell == null) {
            return;
        }
        
        Display display = loopShell.getDisplay();

        while (loopShell != null && ! loopShell.isDisposed() && treeViewer != null)
        {
            try
            {
                if(! display.readAndDispatch())
                {
                    display.sleep();
                }
            }
            catch (Throwable e)
            {
                LogHandler.getInstance().handleWarnLog(e.getMessage());
            }
        }
        display.update();
    }

}
