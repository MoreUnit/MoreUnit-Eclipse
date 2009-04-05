package org.moreunit.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.moreunit.SourceFolderContext;
import org.moreunit.log.LogHandler;

/**
 * @author vera 14.03.2008 20:52:17
 */
public class SearchScopeSingelton
{

    private static final SearchScopeSingelton instance = new SearchScopeSingelton();

    private Map<IPackageFragmentRoot, IJavaSearchScope> searchScopeMap = new HashMap<IPackageFragmentRoot, IJavaSearchScope>();

    public static SearchScopeSingelton getInstance()
    {
        return instance;
    }

    public IJavaSearchScope getSearchScope(IPackageFragmentRoot sourceFolder)
    {
        if(searchScopeMap.containsKey(sourceFolder))
            return searchScopeMap.get(sourceFolder);
        else
        {
            IJavaSearchScope scope = getSearchScopeFromContext(sourceFolder);
            searchScopeMap.put(sourceFolder, scope);
            return scope;
        }
    }

    private IJavaSearchScope getSearchScopeFromContext(IPackageFragmentRoot sourceFolder)
    {
        List<IPackageFragmentRoot> sourceFolderToSearch = SourceFolderContext.getInstance().getSourceFolderToSearch(sourceFolder);
        return SearchEngine.createJavaSearchScope(sourceFolderToSearch.toArray(new IPackageFragmentRoot[sourceFolderToSearch.size()]));
    }

    /**
     * This method gets called if the user changed global settings.
     */
    public void resetCachedSearchScopes()
    {
        searchScopeMap = new HashMap<IPackageFragmentRoot, IJavaSearchScope>();
    }
}
