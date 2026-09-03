package org.moreunit.matching;

import static java.util.Collections.emptySet;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
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
    private final IType type;
    private final ProjectPreferences preferences;
    private final ClassNameEvaluation nameEvaluation;
    private final IJavaSearchScope searchScope;
    private Collection<IType> perfectMatches;
    private Collection<IType> likelyMatches;

    public CorrespondingTypeSearcher(ICompilationUnit compilationUnit, Preferences preferences)
    {
        this.type = compilationUnit.findPrimaryType();
        this.preferences = preferences.getProjectView(compilationUnit.getJavaProject());
        nameEvaluation = this.preferences.getTestClassNamePattern().evaluate(this.type);
        final IPackageFragmentRoot sourceFolder = nameEvaluation.isTestCase()
            ? preferences.getTestSourceFolder(compilationUnit.getJavaProject(), PluginTools.getSourceFolder(compilationUnit))
            : this.preferences.getMainSourceFolder(PluginTools.getSourceFolder(compilationUnit));
        searchScope = SearchScopeSingelton.getInstance().getSearchScope(sourceFolder);
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
        catch (final CoreException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }

        return emptySet();
    }

    private Collection<IType> findPotentialTargets(boolean withLikelyMatches) throws CoreException
    {
        final boolean qualifyWithPackage = ! withLikelyMatches;
        final Set<String> patterns = new LinkedHashSet<>(nameEvaluation.getAllCorrespondingClassPatterns(qualifyWithPackage));

        Set<IType> matches = SearchTools.searchFor(patterns, searchScope);

        if(matches.size() == 1 && ! matches.iterator().next().isInterface())
        {
            return matches;
        }

        if(type != null && ! nameEvaluation.isTestCase())
        {
            try
            {
                final boolean interfaceOfAbstract = type.isInterface() || Flags.isAbstract(type.getFlags());
                final ITypeHierarchy hierarchy = interfaceOfAbstract ? type.newTypeHierarchy(new NullProgressMonitor()) : type.newSupertypeHierarchy(new NullProgressMonitor());
                for (final IType superType : hierarchy.getAllSupertypes(type))
                {
                    if(! (superType.getFullyQualifiedName().startsWith("java.lang.") || superType.getFullyQualifiedName().startsWith("java.io.")))
                    {
                        final ClassNameEvaluation superEval = preferences.getTestClassNamePattern().evaluate(superType);
                        patterns.addAll(superEval.getAllCorrespondingClassPatterns(qualifyWithPackage));
                    }
                }

                if(interfaceOfAbstract)
                {
                    final IType[] subtypes = hierarchy.getAllSubtypes(type);
                    for (final IType subType : subtypes)
                    {
                        if(! Flags.isAbstract(subType.getFlags()) && ! subType.isInterface())
                        {
                            final ClassNameEvaluation subEval = preferences.getTestClassNamePattern().evaluate(subType);
                            patterns.addAll(subEval.getAllCorrespondingClassPatterns(qualifyWithPackage));
                        }
                    }
                }
            }
            catch (final JavaModelException e)
            {
                LogHandler.getInstance().handleExceptionLog(e);
            }
        }

        matches = SearchTools.searchFor(patterns, searchScope);

        if(nameEvaluation.isTestCase())
        {
            final Set<IType> allMatches = new LinkedHashSet<>(matches);
            final Set<IType> concreteImplementations = new LinkedHashSet<>();
            for (final IType match : matches)
            {
                try
                {
                    if(match.isInterface() || Flags.isAbstract(match.getFlags()))
                    {
                        concreteImplementations.addAll(SearchTools.findConcreteSubclasses(match));
                    }
                }
                catch (final JavaModelException e)
                {
                    // ignore
                }
            }

            if(! concreteImplementations.isEmpty())
            {
                allMatches.addAll(concreteImplementations);

                for (final Iterator<IType> it = allMatches.iterator(); it.hasNext();)
                {
                    final IType match = it.next();
                    try
                    {
                        if((match.isInterface() || Flags.isAbstract(match.getFlags())) && ! hasImplementation(match))
                        {
                            it.remove();
                        }
                    }
                    catch (final JavaModelException e)
                    {
                        // ignore
                    }
                }
            }

            return allMatches;
        }

        return matches;
    }

    private boolean hasImplementation(IType type) throws JavaModelException
    {
        for (final IMethod method : type.getMethods())
        {
            if(Flags.isDefaultMethod(method.getFlags()) || (! Flags.isAbstract(method.getFlags()) && ! type.isInterface()))
            {
                return true;
            }
        }
        return false;
    }
}
