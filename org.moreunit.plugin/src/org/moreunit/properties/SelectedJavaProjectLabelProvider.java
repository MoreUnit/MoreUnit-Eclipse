package org.moreunit.properties;

import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.moreunit.util.StringConstants;

public class SelectedJavaProjectLabelProvider extends LabelProvider implements ITableLabelProvider {

	private JavaElementLabelProvider javaElementLabelProvider = new JavaElementLabelProvider();
	
	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof SelectedJavaProject) {
			return javaElementLabelProvider.getImage(((SelectedJavaProject)element).getJavaProject());
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0) {
			SelectedJavaProject row = (SelectedJavaProject) element;
			return row.getProjectName();
		}
		return StringConstants.EMPTY_STRING;
	}
	
	@Override
	public String getText(Object element) {
		SelectedJavaProject row = (SelectedJavaProject) element;
		return row.getProjectName();
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.2  2006/10/02 18:22:23  channingwalton
// added actions for jumping from views. added some tests for project properties. improved some of the text
//
// Revision 1.1  2006/10/01 13:02:44  channingwalton
// Implementation for [ 1556583 ] Extend testcase matching across whole workspace
//
//