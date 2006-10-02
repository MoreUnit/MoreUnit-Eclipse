package org.moreunit.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

public class ProjectPropertiesPage extends PropertyPage {

	private CheckboxTableViewer	tableViewer;

	@Override
	protected Control createContents(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		createLabel(composite);
		createTableViewer(composite);

		Dialog.applyDialogFont(composite);
		return composite;
	}

	private void createLabel(Composite composite) {
		Label label = new Label(composite, SWT.LEFT);
		label.setText("Projects with tests for " + getProject().getName());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
	}

	private void createTableViewer(Composite parent) {
		tableViewer = CheckboxTableViewer.newCheckList(parent, SWT.NONE);
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		tableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				SelectedJavaProject row = (SelectedJavaProject) event.getElement();
				row.setSelected(event.getChecked());
				tableViewer.setChecked(row, row.isSelected());
			}
		});

		SelectedJavaProjectProvider provider = new SelectedJavaProjectProvider(getJavaProject());
		tableViewer.setContentProvider(provider);
		tableViewer.setLabelProvider(new SelectedJavaProjectLabelProvider());
		tableViewer.setComparator(new ViewerComparator());
		tableViewer.setInput(provider.getElements());
		tableViewer.setCheckedElements(provider.getCheckedElements());
	}

	@Override
	protected void performDefaults() {
		tableViewer.setAllChecked(false);
	}

	@Override
	public boolean performOk() {
		Object[] checkedElements = tableViewer.getCheckedElements();
		IJavaProject[] projects = new IJavaProject[checkedElements.length];
		for (int i = 0; i < checkedElements.length; i++) {
			projects[i] = ((SelectedJavaProject) checkedElements[i]).getJavaProject();
		}
		return ProjectProperties.instance().setTestProjects(getProject(), projects);
	}

	private IProject getProject() {
		return getJavaProject().getProject();
	}

	private IJavaProject getJavaProject() {
		return (IJavaProject) getElement();
	}

}

// $Log: not supported by cvs2svn $
// Revision 1.2 2006/10/01 14:11:11 channingwalton
// forgot one thing for Implementation for [ 1556583 ] Extend testcase matching across whole workspace
//
// Revision 1.1 2006/10/01 13:02:44 channingwalton
// Implementation for [ 1556583 ] Extend testcase matching across whole workspace
//
//
