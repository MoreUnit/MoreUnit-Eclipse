package org.moreunit.matching;

import static java.util.Collections.emptySet;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.ProjectPreferences;
import org.moreunit.util.PluginTools;
import org.moreunit.util.SearchScopeSingelton;
import org.moreunit.util.SearchTools;

/**
 * Encapsulates the logic to find the test classes - respectively the types
 * under test - corresponding to a given compilation unit which contains a type
 * under test - respectively a test case.
 * <p>
 * Note: in this context, the word "type" either translates to class or enum.
 * </p>
 */
public class CorrespondingTypeSearcher
{
    private final ProjectPreferences preferences;
    private final ClassNameEvaluation nameEvaluation;
    private final IJavaSearchScope searchScope;
    private Collection<IType> perfectMatches;
    private Collection<IType> likelyMatches;

    public CorrespondingTypeSearcher(ICompilationUnit compilationUnit, Preferences preferences)
    {
        this.preferences = preferences.getProjectView(compilationUnit.getJavaProject());
        nameEvaluation = this.preferences.getTestClassNamePattern().evaluate(compilationUnit.findPrimaryType());
        searchScope = SearchScopeSingelton.getInstance().getSearchScope(PluginTools.getSourceFolder(compilationUnit));
    }

    public Collection<IType> getMatches(boolean alsoIncludeLikelyMatches)
    {
        try
        {
            if(alsoIncludeLikelyMatches)
            {
                if(this.likelyMatches == null)
                {
                    this.likelyMatches = findPotentialTargets(true);
                }
                return this.likelyMatches;
            }
            else
            {
                if(this.perfectMatches == null)
                {
                    this.perfectMatches = findPotentialTargets(false);
                }
                return this.perfectMatches;
            }
        }
        catch (CoreException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }

        return emptySet();
    }

    private Collection<IType> findPotentialTargets(boolean withLikelyMatches) throws CoreException
    {
        boolean qualifyWithPackage = ! withLikelyMatches;
        return SearchTools.searchFor(nameEvaluation.getAllCorrespondingClassPatterns(qualifyWithPackage), searchScope);
    }
}
