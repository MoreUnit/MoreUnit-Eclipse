package org.moreunit.preferences;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

/**
 * @author vera
 *
 * 03.06.2006 15:34:50
 */
public class StringListEditor extends ListEditor {
	
	private Composite parent;

	
	public StringListEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		this.parent = parent;
	}

	@Override
	protected String createList(String[] items) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < items.length; i++) {
			if (i > 0) {
				buffer.append(",");
			}
			buffer.append(items[i]);
		}
		return buffer.toString();
	}

	@Override
	protected String getNewInputObject() {
		
		InputDialog dialog = new InputDialog(getShell(), "moreUnit", "Please enter a new name:", "Test", new IInputValidator() {
			public String isValid(String newText) {
				return getListControl(parent).indexOf(newText) == -1 ? null : newText + " is already in the list.";
			}

		});
		if (dialog.open() == Window.OK) {
			return dialog.getValue();
		}
		return null;
	}
	

	@Override
	protected String[] parseString(String stringList) {
		return stringList.split(",");
	}

}
