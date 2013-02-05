package org.moreunit.core.matching;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.core.text.TextSearchScope;
import org.moreunit.core.log.Logger;
import org.moreunit.core.resources.Resource;

public class SearchEngine
{
    private static final Pattern ANY_CONTENT = Pattern.compile("");

    private final TextSearchEngine searchEngine;
    private final Logger logger;

    public SearchEngine(TextSearchEngine searchEngine, Logger logger)
    {
        this.searchEngine = searchEngine;
        this.logger = logger;
    }

    public void searchFiles(Resource rootResource, Pattern fileNamePattern, TextSearchRequestor requestor)
    {
        try
        {
            TextSearchScope scope = createScope(rootResource.getUnderlyingPlatformResource(), fileNamePattern);
            IStatus searchStatus = searchEngine.search(scope, requestor, ANY_CONTENT, null);

            if(searchStatus.getCode() != IStatus.OK)
            {
                logger.warn("Search failed with status: " + searchStatus);
            }
        }
        catch (Exception e)
        {
            logger.error("Search failed", e);
        }
    }

    private TextSearchScope createScope(IResource rootResource, Pattern fileNamePattern)
    {
        return TextSearchScope.newSearchScope(new IResource[] { rootResource }, fileNamePattern, false);
    }
}
