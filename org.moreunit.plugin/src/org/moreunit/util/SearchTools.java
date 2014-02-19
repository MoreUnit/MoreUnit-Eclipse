package org.moreunit.util;

import static org.eclipse.jdt.core.search.IJavaSearchConstants.DECLARATIONS;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.TYPE;
import static org.eclipse.jdt.core.search.SearchPattern.R_EXACT_MATCH;
import static org.eclipse.jdt.core.search.SearchPattern.R_PATTERN_MATCH;
import static org.eclipse.jdt.core.search.SearchPattern.createOrPattern;
import static org.eclipse.jdt.core.search.SearchPattern.createPattern;

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
    public static Set<IType> searchFor(String typeName, IJavaSearchScope scope) throws CoreException
    {
        return search(createPattern(typeName, TYPE, DECLARATIONS, R_EXACT_MATCH), scope);
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
        return new LinkedHashSet<IType>(collector.matches);
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
        private final Set<IType> matches = new TreeSet<IType>(new TypeComparator());

        public void acceptSearchMatch(SearchMatch match)
        {
            matches.add((IType) match.getElement());
        }
    }
}
