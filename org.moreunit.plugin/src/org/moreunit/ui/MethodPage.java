package org.moreunit.ui;

import java.util.Iterator;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
public class MethodPage extends Page {
	
	ListViewer listViewer;
	EditorPartFacade editorPartFacade;
	TreeViewer treeViewer;
	
	Action addTestAction;
	
	public MethodPage(EditorPartFacade editorPartFacade) {
		super();
		
		this.editorPartFacade = editorPartFacade;
	}

	@Override
	public void createControl(Composite parent) {
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new MethodTreeContentProvider(editorPartFacade.getCompilationUnit().findPrimaryType()));
		treeViewer.setLabelProvider(new JavaElementLabelProvider());
		treeViewer.setInput(this);
		
		createMenu();
	}
	
	public IType getInputType() {
		return editorPartFacade.getCompilationUnit().findPrimaryType();
	}

	private void createMenu() {
		addTestAction = new Action("Add...") {
			@Override
			public void run() {
				addItem();
			}
		};
		addTestAction.setImageDescriptor(MoreUnitPlugin.getImageDescriptor("icons/add.png"));
		IMenuManager menuManager = getSite().getActionBars().getMenuManager();
		menuManager.add(addTestAction);
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

}