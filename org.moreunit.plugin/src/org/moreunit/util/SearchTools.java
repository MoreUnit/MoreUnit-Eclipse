package org.moreunit.util;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

/**
 * @author vera
 */
public class SearchTools
{

    public static Set<IType> searchFor(String typeName, IJavaElement sourceCompilationUnit, IJavaSearchScope searchScope) throws JavaModelException, CoreException
    {
        SearchPattern pattern = SearchPattern.createPattern(typeName, IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
        IJavaSearchScope scope = searchScope;
        SearchEngine searchEngine = new SearchEngine();
        final Set<IType> matches = new TreeSet<IType>(new TypeComparator());
        SearchRequestor requestor = new SearchRequestor()
        {
            public void acceptSearchMatch(SearchMatch match)
            {
                matches.add((IType) match.getElement());
            }
        };
        searchEngine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope, requestor, null);
        return matches;
    }
}
