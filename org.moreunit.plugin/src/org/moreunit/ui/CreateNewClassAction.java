package org.moreunit.ui;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public abstract class CreateNewClassAction implements TreeActionElement<IType>
{
    private static final ImageDescriptor IMG_DESC = new JavaElementImageDescriptor(JavaPluginImages.DESC_TOOL_NEWCLASS, 0, new Point(22, 16));

    public boolean provideElement()
    {
        return true;
    }

    abstract public IType execute();

    public Image getImage()
    {
        return JavaPlugin.getImageDescriptorRegistry().get(IMG_DESC);
    }

    public String getText()
    {
        return "New Class...";
    }
}
