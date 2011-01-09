package org.moreunit.ui;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
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

public class ChooseDialog<T> extends PopupDialog implements DisposeListener
{

    private TreeViewer treeViewer;
    private T selectedElement;
    private final ITreeContentAndDefaultSelectionProvider contentProvider;

    public ChooseDialog(String titleText, ITreeContentAndDefaultSelectionProvider contentProvider)
    {
        super(getDefaultShell(), PopupDialog.INFOPOPUP_SHELLSTYLE, true, false, false, false, false, titleText, null);

        setInfoText(StringConstants.EMPTY_STRING);
        this.contentProvider = contentProvider;
        create();
        afterCreation();
    }

    private void afterCreation()
    {
        // selection must be set after creation to not mess up dialog layout
        treeViewer.setSelection(contentProvider.getDefaultSelection());
    }

    private static Shell getDefaultShell()
    {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
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
                T selectedElement = getSelectedElement();
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
                            fLastItem = (TreeItem) o;
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
                if(! tree.equals(e.getSource()))
                    close();

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
                        T selectedElement = getSelectedElement();
                        close();
                        handleElementSelected(selectedElement);
                    }
                }
            }
        });

        addDisposeListener(this);
        return treeViewer.getControl();
    }

    @SuppressWarnings("unchecked")
    public T getSelectedElement()
    {
        if(treeViewer == null)
            return null;

        return (T) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
    }

    private void handleElementSelected(T selectedElement)
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
        TreeViewer viewer = new TreeViewer(parent, SWT.NO_TRIM);
        viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(new LabelProvider());
        viewer.setInput(this);
        return viewer;
    }

    public T getChoice()
    {
        open();
        runEventLoop(getDefaultShell());
        return selectedElement;
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

    private static class LabelProvider extends JavaElementLabelProvider
    {
        @Override
        public String getText(Object element)
        {
            if(element instanceof IType)
            {
                IType type = (IType) element;
                return String.format("%s - %s", type.getElementName(), type.getPackageFragment().getElementName());
            }
            return super.getText(element);
        }
    }
}
