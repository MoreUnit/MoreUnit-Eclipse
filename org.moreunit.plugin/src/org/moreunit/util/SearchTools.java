package org.moreunit.util;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
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
    public static Set<IType> searchFor(String typeName, IJavaSearchScope searchScope) throws CoreException
    {
        SearchPattern pattern = SearchPattern.createPattern(typeName, IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
        SearchParticipant[] participants = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
        MatchCollector collector = new MatchCollector();

        new SearchEngine().search(pattern, participants, searchScope, collector, null);

        return collector.matches;
    }

    private static class MatchCollector extends SearchRequestor
    {
        private final Set<IType> matches = new TreeSet<IType>(new TypeComparator());

        public void acceptSearchMatch(SearchMatch match)
        {
            matches.add((IType) match.getElement());
        }
    }
}
