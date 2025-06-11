package org.moreunit.util;

import static org.eclipse.jdt.core.search.IJavaSearchConstants.*;
import static org.eclipse.jdt.core.search.SearchPattern.*;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
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
    private SearchTools()
    {
    }

    public static Set<IType> searchFor(Collection<String> typeNamePatterns, IJavaSearchScope scope) throws CoreException
    {
        return search(createSearchPattern(typeNamePatterns, TYPE, DECLARATIONS, R_PATTERN_MATCH), scope);
    }

    private static Set<IType> search(SearchPattern pattern, IJavaSearchScope scope) throws CoreException
    {
        SearchParticipant[] participants = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
        MatchCollector collector = new MatchCollector();

        new SearchEngine().search(pattern, participants, scope, collector, null);

        return getMatchesPreservingOrder(collector);
    }

    private static Set<IType> getMatchesPreservingOrder(MatchCollector collector)
    {
        // copy preserving order (we don't pass the TreeSet, since it does not
        // allow for calling contains() with an object that is not an IType)
        return new LinkedHashSet<>(collector.matches);
    }

    private static SearchPattern createSearchPattern(Collection<String> typeNamePatterns, int searchFor, int limitTo, int matchRule)
    {
        SearchPattern result = null;
        SearchPattern lastPattern = null;

        for (String p : typeNamePatterns)
        {
            SearchPattern currentPattern = createPattern(p, searchFor, limitTo, matchRule);
            if(lastPattern == null)
            {
                result = currentPattern;
            }
            else
            {
                result = createOrPattern(lastPattern, currentPattern);
            }
            lastPattern = result;
        }

        return result;
    }

    private static class MatchCollector extends SearchRequestor
    {
        private final Set<IType> matches = new TreeSet<>(new TypeComparator());

        public void acceptSearchMatch(SearchMatch match)
        {
            matches.add((IType) match.getElement());
        }
    }
}
