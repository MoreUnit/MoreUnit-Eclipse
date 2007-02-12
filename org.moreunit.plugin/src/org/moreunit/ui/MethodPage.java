package org.moreunit.ui;

import java.util.Iterator;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.elements.MethodTreeContentProvider;
import org.moreunit.elements.TestCaseTypeFacade;

/**
 * @author vera
 *
 */
public class MethodPage extends Page implements IElementChangedListener{
	
	ListViewer listViewer;
	EditorPartFacade editorPartFacade;
	TreeViewer treeViewer;
	
	Action addTestAction;
	Action filterPrivateAction;
	Action filterGetterAction;
	
	private MethodTreeContentProvider methodTreeContentProvider;
	
	public MethodPage(EditorPartFacade editorPartFacade) {
		super();
		
		this.editorPartFacade = editorPartFacade;
		JavaCore.addElementChangedListener(this);
	}

	@Override
	public void createControl(Composite parent) {
		treeViewer = new TreeViewer(parent);
		methodTreeContentProvider = new MethodTreeContentProvider(editorPartFacade.getCompilationUnit().findPrimaryType());
		treeViewer.setContentProvider(methodTreeContentProvider);
		treeViewer.setLabelProvider(new JavaElementLabelProvider());
		treeViewer.setInput(this);
		
		createMenu();
		createToolbar();
	}
	
	public IType getInputType() {
		return editorPartFacade.getCompilationUnit().findPrimaryType();
	}

	private void createMenu() {
//		addTestAction = new Action("Add...") {
//			@Override
//			public void run() {
//				addItem();
//			}
//		};
//		addTestAction.setImageDescriptor(MoreUnitPlugin.getImageDescriptor("icons/add.png"));
//		IMenuManager menuManager = getSite().getActionBars().getMenuManager();
//		menuManager.add(addTestAction);
	}
	
	private void createToolbar() {
		filterPrivateAction = new Action("", Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				actionFilterPrivateMethods();
			}
		};
		filterPrivateAction.setImageDescriptor(MoreUnitPlugin.getImageDescriptor("icons/private.gif"));
		filterPrivateAction.setChecked(true);
		
		filterGetterAction = new Action("", Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				actionFilterGetterMethods();
			}
		};
		filterGetterAction.setImageDescriptor(MoreUnitPlugin.getImageDescriptor("icons/getter.gif"));
		filterGetterAction.setChecked(true);
		
		addTestAction = new Action("Add...") {
			@Override
			public void run() {
				addItem();
			}
		};
		addTestAction.setImageDescriptor(MoreUnitPlugin.getImageDescriptor("icons/add.png"));
		IToolBarManager toolBarManager = getSite().getActionBars().getToolBarManager();
		toolBarManager.add(filterPrivateAction);
		toolBarManager.add(filterGetterAction);
		toolBarManager.add(addTestAction);
		
	}
	
	private void actionFilterPrivateMethods() {
		methodTreeContentProvider.setPrivateFiltered(filterPrivateAction.isChecked());
		updateUI();
	}
	
	private void actionFilterGetterMethods() {
		methodTreeContentProvider.setGetterFiltered(filterGetterAction.isChecked());
		updateUI();
	}

	private void addItem() {
		ITreeSelection selection = (ITreeSelection) treeViewer.getSelection();
		if(selection.isEmpty())
			return;
		
		ClassTypeFacade classTypeFacade = new ClassTypeFacade(editorPartFacade.getEditorPart());
		IType typeOfTestCaseClassFromJavaFile = classTypeFacade.getOneCorrespondingTestCase(true);

		if(typeOfTestCaseClassFromJavaFile == null || !typeOfTestCaseClassFromJavaFile.exists())
			return;

		TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(typeOfTestCaseClassFromJavaFile.getCompilationUnit());
		for (Iterator<IMethod> allSelected = selection.iterator(); allSelected.hasNext();) {
			IMethod selectedMethod = (IMethod) allSelected.next();
			testCaseTypeFacade.createTestMethodForMethod(selectedMethod);
		}
		
		updateUI();
	}
	
	@Override
	public Control getControl() {
		if(treeViewer != null)
			return treeViewer.getControl();
		
		return null;
	}

	@Override
	public void setFocus() {
		getControl().setFocus();
	}

	public void updateUI(){
		treeViewer.refresh();
	}

	public void elementChanged(ElementChangedEvent event) {
		int type = event.getDelta().getElement().getElementType();
		switch(type) {
			case(IJavaElement.COMPILATION_UNIT): updateUIafterElementChangedEvent(); break;
			default: {}
		}
	}
	
	private void updateUIafterElementChangedEvent() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				updateUI();
			}
		});
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		JavaCore.removeElementChangedListener(this);
	}

}