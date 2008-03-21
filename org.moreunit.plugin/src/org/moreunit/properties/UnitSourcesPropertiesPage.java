package org.moreunit.properties;


import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.moreunit.preferences.Preferences;
import org.moreunit.util.SearchScopeSingelton;

/**
 * @author vera
 *
 * 01.03.2008 14:36:45
 */
public class UnitSourcesPropertiesPage extends PropertyPage {
	
	/*
	private TreeViewer sourceFolderTree;
	private Button addButton;
	private Button removeButton;
	private UnitSourcesContentProvider unitSourcesContentProvider;
	*/

	public UnitSourcesPropertiesPage() {
	}

	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		/*
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		createLabel(parent);
		createTreeViewer(parent);
		createButtons(parent);
		*/
		return parent;
	}
	
	private void createTreeViewer(Composite composite) {
		/*
		sourceFolderTree = new TreeViewer(composite);
		unitSourcesContentProvider = new UnitSourcesContentProvider(getJavaProject());
		sourceFolderTree.setContentProvider(unitSourcesContentProvider);
		sourceFolderTree.setLabelProvider(new UnitSourceFolderLabelProvider());
		sourceFolderTree.setInput(getJavaProject());
		GridData layoutData = new GridData();
		layoutData.widthHint = 250;
		layoutData.heightHint = 200;
		sourceFolderTree.getControl().setLayoutData(layoutData);
		*/
	}

	private void createLabel(Composite composite) {
		/*
		Label label = new Label(composite, SWT.LEFT);
		label.setText("Projects with tests for " + getJavaProject().getElementName());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		
		label.setLayoutData(layoutData);
		*/
	}
	
	private void createButtons(Composite composite) {
		/*
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		buttonComposite.setFont(composite.getFont());
		
		addButton = new Button(buttonComposite, SWT.PUSH);
		addButton.setFont(composite.getFont());
		addButton.setText("Add");
		addButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent selectionEvent) {
				addButtonClicked();
			}
		});
		
		removeButton = new Button(buttonComposite, SWT.PUSH);
		removeButton.setFont(composite.getFont());
		removeButton.setText("Remove");
		removeButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				removeButtonClicked();
			}
		});
		
		FillLayout buttonBoxLayout = new FillLayout(SWT.VERTICAL);
		buttonComposite.setLayout(buttonBoxLayout);
		
		GridData layoutData = new GridData();
		layoutData.widthHint = 100;
		buttonComposite.setLayoutData(layoutData);
		*/
	}
	
	private IJavaProject getJavaProject() {
		if (getElement() instanceof IJavaProject) {
			return (IJavaProject) getElement();
		}
		return JavaCore.create((IProject) getElement());
	}

	public void widgetDefaultSelected(SelectionEvent selectionEvent) {
	}

	private void addButtonClicked() {
		//new AddUnitSourceFolderWizard(this).open();
	}
	
	public void handlePerformFinishFromAddUnitSourceFolderWizard(List<IPackageFragmentRoot> folderToAdd) {
		/*
		if(folderToAdd.size() > 0) {
			unitSourcesContentProvider.add(folderToAdd);
			sourceFolderTree.refresh();
		}
		*/
	}
	
	private void removeButtonClicked() {
		/*
		TreeSelection selection = (TreeSelection) sourceFolderTree.getSelection();
		for(Object singleSelection : selection.toList()) {
			if(unitSourcesContentProvider.remove((IPackageFragmentRoot) singleSelection))
				sourceFolderTree.refresh();
		}
		*/
	}
	
	/*
	public List<IPackageFragmentRoot> getListOfUnitSourceFolder() {
		return unitSourcesContentProvider.getListOfUnitSourceFolder();
	}
	*/
	
	@Override
	public boolean performOk() {
		/*
		Preferences.getInstance().setTestSourceFolder(getJavaProject(), unitSourcesContentProvider.getListOfUnitSourceFolder());
		SearchScopeSingelton.getInstance().recalculateSearchScope(getJavaProject());
		*/
		return super.performOk();
	}
	
	@Override
	protected void performApply() {
		/*
		Preferences.getInstance().setTestSourceFolder(getJavaProject(), unitSourcesContentProvider.getListOfUnitSourceFolder());
		*/
		super.performApply();
	}
}