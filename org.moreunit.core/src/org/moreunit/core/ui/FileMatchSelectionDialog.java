package org.moreunit.core.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.moreunit.core.log.Logger;

public class FileMatchSelectionDialog<T extends IAdaptable> extends PopupDialog implements DisposeListener
{
    private TreeViewer treeViewer;
    private T selectedElement;
    private final ITreeContentAndDefaultSelectionProvider contentProvider;
    private final Logger logger;

    public FileMatchSelectionDialog(String titleText, ITreeContentAndDefaultSelectionProvider contentProvider, Logger logger)
    {
        this(titleText, null, contentProvider, logger);
    }

    public FileMatchSelectionDialog(String titleText, String infoText, ITreeContentAndDefaultSelectionProvider contentProvider, Logger logger)
    {
        super(getDefaultShell(), PopupDialog.INFOPOPUP_SHELLSTYLE, true, false, false, false, false, titleText, infoText);

        this.contentProvider = contentProvider;
        this.logger = logger;
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
        tree.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if(e.character == SWT.ESC)
                    close();
            }
        });

        tree.addSelectionListener(new SelectionAdapter()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
                handleElementSelected();
            }
        });

        tree.addMouseMoveListener(new MouseMoveListener()
        {
            TreeItem fLastItem = null;

            public void mouseMove(MouseEvent e)
            {
                if(tree.equals(e.getSource()))
                {
                    TreeItem o = tree.getItem(new Point(e.x, e.y));
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
                        handleElementSelected();
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

    @SuppressWarnings("unchecked")
    private void handleElementSelected()
    {
        Object selectedElement = getSelectedElement();
        if(selectedElement instanceof TreeActionElement)
        {
            TreeActionElement<T> action = (TreeActionElement<T>) selectedElement;
            if(! action.provideElement())
            {
                return;
            }

            selectedElement = action.execute();
        }

        close();

        this.selectedElement = (T) selectedElement;
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
        viewer.setLabelProvider(new FileLabelProvider());
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
        if(loopShell == null)
        {
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
            catch (Exception e)
            {
                logger.error(e);
            }
        }
        display.update();
    }

    private static class FileLabelProvider extends LabelProvider
    {
        private final Image fileIcon = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);

        @Override
        public Image getImage(Object element)
        {
            if(element instanceof TreeActionElement)
            {
                return ((TreeActionElement< ? >) element).getImage();
            }
            else if(element instanceof IFile)
            {
                return fileIcon;
            }
            return super.getImage(element);
        }

        @Override
        public String getText(Object element)
        {
            if(element instanceof TreeActionElement)
            {
                return ((TreeActionElement< ? >) element).getText();
            }
            else if(element instanceof IFile)
            {
                IFile file = (IFile) element;
                return String.format("%s - %s", file.getName(), file.getFullPath().removeLastSegments(1));
            }
            return super.getText(element);
        }
    }
}
