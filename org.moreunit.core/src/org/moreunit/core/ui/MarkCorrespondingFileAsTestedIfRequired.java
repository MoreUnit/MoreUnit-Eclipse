package org.moreunit.core.ui;

import static org.moreunit.core.config.CoreModule.$;

import org.moreunit.core.decorators.TestedFileDecorator;
import org.moreunit.core.matching.DoesNotMatchConfigurationException;
import org.moreunit.core.matching.MatchingFile;
import org.moreunit.core.resources.SrcFile;

/**
 * Forces decoration of a source file when a test file is created for it (for
 * better user experience).
 */
public class MarkCorrespondingFileAsTestedIfRequired implements FileCreationListener
{
    private final TestedFileDecorator decorator;

    public MarkCorrespondingFileAsTestedIfRequired()
    {
        this(TestedFileDecorator.getInstanceIfExisting());
    }

    public MarkCorrespondingFileAsTestedIfRequired(TestedFileDecorator decorator)
    {
        this.decorator = decorator;
    }

    @Override
    public void fileCreated(SrcFile maybeTestFile)
    {
        if(decorator == null)
            return;

        // if not a test file, the new file will be automatically decorated
        if(! maybeTestFile.isTestFile())
            return;

        try
        {
            MatchingFile matchingFile = maybeTestFile.findUniqueMatch();
            if(matchingFile.isFound())
                decorator.refreshIndicatorFor(matchingFile.get());
        }
        catch (DoesNotMatchConfigurationException e)
        {
            $().getLogger().warn("Could not find corresponding source file", e);
        }
    }
}
