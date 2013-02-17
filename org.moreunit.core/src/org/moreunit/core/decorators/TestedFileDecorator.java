package org.moreunit.core.decorators;

import static org.moreunit.core.config.CoreModule.$;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.moreunit.core.log.Logger;
import org.moreunit.core.matching.DoesNotMatchConfigurationException;
import org.moreunit.core.resources.SrcFile;
import org.moreunit.core.ui.ImageRegistry;

public class TestedFileDecorator extends LabelProvider implements ILightweightLabelDecorator
{
    private final ImageRegistry imageRegistry;
    private final Logger logger;

    public TestedFileDecorator()
    {
        this($().getImageRegistry(), $().getLogger());
    }

    public TestedFileDecorator(ImageRegistry imageRegistry, Logger logger)
    {
        this.imageRegistry = imageRegistry;
        this.logger = logger;
    }

    @Override
    public void decorate(Object element, IDecoration decoration)
    {
        decorate($().getWorkspace().toSrcFile((IFile) element), decoration);
    }

    public void decorate(SrcFile file, IDecoration decoration)
    {
        try
        {
            if(file.hasDefaultSupport() && ! file.isTestFile() && file.hasCorrespondingFiles())
                decoration.addOverlay(imageRegistry.getTestedFileIndicator(), IDecoration.TOP_RIGHT);
        }
        catch (DoesNotMatchConfigurationException e)
        {
            // ignored: we don't want a pop-up to open for every file in error
            if(logger.debugEnabled())
                logger.debug(e.getPath() + " does not match source folder preferences");
        }
    }
}
