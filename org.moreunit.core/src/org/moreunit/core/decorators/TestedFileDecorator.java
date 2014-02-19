package org.moreunit.core.decorators;

import static org.moreunit.core.config.CoreModule.$;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.moreunit.core.MoreUnitCore;
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
                logger.info(e.getPath() + " does not match source folder preferences");
        }
        catch (Exception e)
        {
            // ignored: we don't want a pop-up to open for every file in error
            logger.error(e);
        }
    }

    /**
     * Returns the decorator if already instantiated by Eclipse. We can silently
     * ignore the case when it has not yet been created by Eclipse:
     * TestedFileDecorator.decorate() will be called soon or later
     */
    public static TestedFileDecorator getInstanceIfExisting()
    {
        return (TestedFileDecorator) MoreUnitCore.get().getWorkbench().getDecoratorManager().getBaseLabelProvider("org.moreunit.core.decorators.testedFileDecorator");
    }

    public void refreshIndicatorFor(Object... elements)
    {
        fireLabelProviderChanged(new LabelProviderChangedEvent(this, elements));
    }
}
