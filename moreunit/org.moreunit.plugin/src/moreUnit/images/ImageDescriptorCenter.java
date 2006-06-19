package moreUnit.images;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author vera
 * 20.11.2005
 */
public class ImageDescriptorCenter {
	
	private static ImageDescriptor testCaseLabelDescriptor;
	
	public static ImageDescriptor getTestCaseLabelImageDescriptor() {
		if(testCaseLabelDescriptor == null)
			testCaseLabelDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin("moreUnit", "icons/classHasTest.gif");
		
		return testCaseLabelDescriptor;
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//