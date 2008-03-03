package org.moreunit.properties;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.moreunit.util.StringConstants;

/**
 * @author vera
 *
 * 03.03.2008 19:54:23
 */
public class UnitSourceFolderLabelProvider extends LabelProvider {
	
	private JavaElementLabelProvider baseLabelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT | JavaElementLabelProvider.SHOW_QUALIFIED | JavaElementLabelProvider.SHOW_ROOT);
	
	@Override
	public Image getImage(Object element) {
		return baseLabelProvider.getImage(element);
	}
	
	@Override
	public String getText(Object element) {
		if(element instanceof IPackageFragmentRoot) {
			IPackageFragmentRoot sourceFolder = (IPackageFragmentRoot) element;
			
			StringBuffer result = new StringBuffer();
			result.append(sourceFolder.getJavaProject().getElementName());
			result.append(StringConstants.SLASH);
			result.append(sourceFolder.getElementName());
			
			return result.toString();
		}
		
		return baseLabelProvider.getText(element);
	}

}
