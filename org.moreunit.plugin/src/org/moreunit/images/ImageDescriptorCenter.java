package org.moreunit.images;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author vera 20.11.2005
 */
public class ImageDescriptorCenter
{

    private static ImageDescriptor testCaseLabelDescriptor;

    public static ImageDescriptor getTestCaseLabelImageDescriptor()
    {
        if(testCaseLabelDescriptor == null)
            testCaseLabelDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin("org.moreunit", "icons/classHasTest.gif");

        return testCaseLabelDescriptor;
    }
}

// $Log: not supported by cvs2svn $
// Revision 1.1.1.1 2006/08/13 14:31:16 gianasista
// initial
//
// Revision 1.1 2006/06/22 20:22:29 gianasista
// package rename
//
// Revision 1.1 2006/06/19 20:08:48 gianasista
// CVS Refactoring
//
// Revision 1.2 2006/01/19 21:39:44 gianasista
// Added CVS-commit-logging to all java-files
//
