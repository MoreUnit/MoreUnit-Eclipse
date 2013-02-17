package org.moreunit.core.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.moreunit.core.MoreUnitCore;

public class ImageRegistry
{
    private static final ImageDescriptor TESTED_FILE_INDICATOR = AbstractUIPlugin.imageDescriptorFromPlugin(MoreUnitCore.PLUGIN_ID, "icons/testedFileIndicator.gif");

    public ImageDescriptor getTestedFileIndicator()
    {
        return TESTED_FILE_INDICATOR;
    }
}
