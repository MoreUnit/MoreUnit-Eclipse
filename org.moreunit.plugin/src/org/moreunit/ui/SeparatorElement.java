package org.moreunit.ui;

import org.eclipse.swt.graphics.Image;

public class SeparatorElement implements TreeActionElement<Void>
{
    @Override
    public boolean provideElement()
    {
        return false;
    }

    @Override
    public Void execute()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Image getImage()
    {
        return null;
    }

    @Override
    public String getText()
    {
        return "_________________";
    }
}
