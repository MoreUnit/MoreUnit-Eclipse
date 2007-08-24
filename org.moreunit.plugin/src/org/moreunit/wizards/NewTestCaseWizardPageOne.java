package org.moreunit.wizards;

import org.eclipse.jdt.internal.junit.wizards.WizardMessages;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author vera
 *
 * 24.08.2007 20:14:28
 */
public class NewTestCaseWizardPageOne extends org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageOne {

	public NewTestCaseWizardPageOne(NewTestCaseWizardPageTwo page2) {
		super(page2);
	}
	
	@Override
	protected void createJUnit4Controls(Composite composite, int nColumns) {
		Composite inner= new Composite(composite, SWT.NONE);
		inner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, nColumns, 1));
		GridLayout layout= new GridLayout(2, false);
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		inner.setLayout(layout);
		
		SelectionAdapter listener= new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean isSelected= ((Button) e.widget).getSelection();
				//internalSetJUnit4(isSelected);
				System.out.println("widgetSelection");
			}
		};
		
		Button junti3Toggle= new Button(inner, SWT.RADIO);
		junti3Toggle.setText(WizardMessages.NewTestCaseWizardPageOne_junit3_radio_label);
		junti3Toggle.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		junti3Toggle.setSelection(false);
		junti3Toggle.setEnabled(true);
		
		Button fJUnit4Toggle= new Button(inner, SWT.RADIO);
		fJUnit4Toggle.setText(WizardMessages.NewTestCaseWizardPageOne_junit4_radio_label);
		fJUnit4Toggle.setSelection(false);
		fJUnit4Toggle.setEnabled(true);
		fJUnit4Toggle.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		fJUnit4Toggle.addSelectionListener(listener);
		
		Button testNgToggle= new Button(inner, SWT.RADIO);
		testNgToggle.setText("TestNG");
		testNgToggle.setSelection(false);
		testNgToggle.setEnabled(true);
		testNgToggle.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		testNgToggle.addSelectionListener(listener);
	}

}
