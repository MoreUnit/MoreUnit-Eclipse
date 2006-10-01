package org.moreunit.properties;

import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

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
		return "";
	}
}

// $Log: not supported by cvs2svn $
//