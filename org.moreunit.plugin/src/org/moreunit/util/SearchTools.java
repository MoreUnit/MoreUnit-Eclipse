package org.moreunit.util;

import static org.eclipse.jdt.core.search.SearchPattern.*;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeNameRequestor;

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
        Set<IType> result = new LinkedHashSet<>();
        SearchEngine engine = new SearchEngine();
        TypeNameRequestor requestor = new TypeNameRequestor()
        {
            @Override
            public void acceptType(int modifiers, char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path)
            {
                IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(Path.fromPortableString(path));
                IJavaElement element = JavaCore.create(file);
                if(element instanceof ICompilationUnit unit)
                {
                    IType type = unit.getType(new String(simpleTypeName));
                    if(type.exists())
                    {
                        result.add(type);
                    }
                }
            }
        };
        for (String pattern : typeNamePatterns)
        {
            int lastDot = pattern.lastIndexOf('.');
            char[] qualification;
            char[] typeName;
            if(lastDot >= 0)
            {
                qualification = pattern.substring(0, lastDot).toCharArray();
                typeName = pattern.substring(lastDot + 1).toCharArray();
            }
            else
            {
                qualification = null;
                typeName = pattern.toCharArray();
            }
            engine.searchAllTypeNames(qualification, SearchPattern.R_EXACT_MATCH, typeName, SearchPattern.R_PATTERN_MATCH | SearchPattern.R_CASE_SENSITIVE, IJavaSearchConstants.CLASS, scope, requestor, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
        }
        return result;
    }

    public static Set<IType> findConcreteSubclasses(IType type) throws JavaModelException
    {
        Set<IType> concreteSubclasses = new LinkedHashSet<>();
        ITypeHierarchy hierarchy = type.newTypeHierarchy(new NullProgressMonitor());
        IType[] subtypes = hierarchy.getAllSubtypes(type);
        for (IType subtype : subtypes)
        {
            if(! Flags.isAbstract(subtype.getFlags()) && ! Flags.isInterface(subtype.getFlags()))
            {
                concreteSubclasses.add(subtype);
            }
        }
        return concreteSubclasses;
    }

    public static Set<IType> search(SearchPattern pattern, IJavaSearchScope scope) throws CoreException
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

        @Override
        public void acceptSearchMatch(SearchMatch match)
        {
            matches.add((IType) match.getElement());
        }
    }
}
