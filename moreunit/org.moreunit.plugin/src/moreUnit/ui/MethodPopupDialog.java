package moreUnit.ui;


/**
 * @author vera
 * 31.03.2006 21:45:19
 */
public class MethodPopupDialog { 
//extends PopupDialog {
	
//	List methods;
//	TreeViewer fTreeViewer;
//	IEditorPart editorPart;
//
//	public MethodPopupDialog(Shell parent, int shellStyle, boolean takeFocusOnOpen, boolean persistBounds, boolean showDialogMenu, boolean showPersistAction, String titleText, String infoText, List methods, IEditorPart editorPart) {
//		super(parent, shellStyle, takeFocusOnOpen, persistBounds, showDialogMenu,
//				showPersistAction, titleText, infoText);
//		
//		this.methods = methods;
//		this.editorPart = editorPart;
//		
//		create();
//	}
//	
//	protected Control createDialogArea(Composite parent) {
//		fTreeViewer = createTreeView(parent);
//		final Tree tree= fTreeViewer.getTree();
//		tree.addKeyListener(new KeyListener() {
//			public void keyPressed(KeyEvent e)  {
//				if (e.character == 0x1B) // ESC
//					dispose();
//			}
//			public void keyReleased(KeyEvent e) {
//				// do nothing
//			}
//		});
//
//		tree.addSelectionListener(new SelectionListener() {
//			public void widgetSelected(SelectionEvent e) {
//				// do nothing
//			}
//			public void widgetDefaultSelected(SelectionEvent e) {
//				gotoSelectedElement();
//			}
//		});
//
//		tree.addMouseMoveListener(new MouseMoveListener()	 {
//			TreeItem fLastItem= null;
//			public void mouseMove(MouseEvent e) {
//				if (tree.equals(e.getSource())) {
//					Object o= tree.getItem(new Point(e.x, e.y));
//					if (o instanceof TreeItem) {
//						if (!o.equals(fLastItem)) {
//							fLastItem= (TreeItem)o;
//							tree.setSelection(new TreeItem[] { fLastItem });
//						} else if (e.y < tree.getItemHeight() / 4) {
//							// Scroll up
//							Point p= tree.toDisplay(e.x, e.y);
//							Item item= fTreeViewer.scrollUp(p.x, p.y);
//							if (item instanceof TreeItem) {
//								fLastItem= (TreeItem)item;
//								tree.setSelection(new TreeItem[] { fLastItem });
//							}
//						} else if (e.y > tree.getBounds().height - tree.getItemHeight() / 4) {
//							// Scroll down
//							Point p= tree.toDisplay(e.x, e.y);
//							Item item= fTreeViewer.scrollDown(p.x, p.y);
//							if (item instanceof TreeItem) {
//								fLastItem= (TreeItem)item;
//								tree.setSelection(new TreeItem[] { fLastItem });
//							}
//						}
//					}
//				}
//			}
//		});
//
//		tree.addMouseListener(new MouseAdapter() {
//			public void mouseUp(MouseEvent e) {
//
//				if (tree.getSelectionCount() < 1)
//					return;
//
//				if (e.button != 1)
//					return;
//
//				if (tree.equals(e.getSource())) {
//					Object o= tree.getItem(new Point(e.x, e.y));
//					TreeItem selection= tree.getSelection()[0];
//					if (selection.equals(o))
//						gotoSelectedElement();
//				}
//			}
//		});
//
//		return fTreeViewer.getControl();
//	}
//
//	private TreeViewer createTreeView(Composite parent) {
//		Tree tree= new Tree(parent, SWT.SINGLE);
//		GridData gd= new GridData(GridData.FILL_BOTH);
//		gd.heightHint= tree.getItemHeight() * 12;
//		tree.setLayoutData(gd);
//		
//		final TreeViewer treeViewer= new TreeViewer(tree);
//		treeViewer.setLabelProvider(new JavaElementLabelProvider());
//		treeViewer.setContentProvider(new MethodContentProvider(methods));
//		
//		return treeViewer;
//	}
//	
//	private void gotoSelectedElement() {
//		Object selectedElement= getSelectedElement();
//		if (selectedElement != null) {
//			dispose();
//			JavaUI.revealInEditor(editorPart, (IJavaElement)selectedElement);
//		}		
//	}
//	
//	protected Object getSelectedElement() {
//		if (fTreeViewer == null)
//			return null;
//
//		return ((IStructuredSelection) fTreeViewer.getSelection()).getFirstElement();
//	}
//	
//	public final void dispose() {
//		close();
//	}

}


// $Log: not supported by cvs2svn $
// Revision 1.2  2006/04/20 19:17:03  gianasista
// PopupDialog isn't there anymore (since 3.2RC1)
//
// Revision 1.1  2006/04/14 17:14:22  gianasista
// Refactoring Support with dialog
//