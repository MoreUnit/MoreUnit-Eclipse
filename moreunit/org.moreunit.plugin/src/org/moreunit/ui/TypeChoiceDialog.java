package org.moreunit.ui;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

public class TypeChoiceDialog extends Dialog {

	private final IType[]	types;
	private int				selected;
	private Combo			choices;

	public TypeChoiceDialog(IType[] types) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.types = types;
	}

	protected Control createDialogArea(Composite parent) {
		getShell().setText("SwitchUnit");

		Composite composite = (Composite) super.createDialogArea(parent);

		Label label = new Label(composite, SWT.WRAP);
		label.setText("Please select a class to switch to:");
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());

		choices = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (int i = 0; i < types.length; i++) {
			IType element = types[i];
			choices.add(element.getFullyQualifiedName());
		}
		choices.select(0);
		choices.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selected = choices.getSelectionIndex();
			}
		});
		data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER);
		choices.setLayoutData(data);
		return composite;
	}

	public IType getChoice() {
		if (open() == Window.CANCEL) {
			return null;
		}
		return types[selected];
	}

}
