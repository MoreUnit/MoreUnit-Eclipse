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
        IPackageFragmentRoot sourceFolder = nameEvaluation.isTestCase()
            ? preferences.getTestSourceFolder(compilationUnit.getJavaProject(), PluginTools.getSourceFolder(compilationUnit))
            : preferences.getMainSourceFolder(compilationUnit.getJavaProject(), PluginTools.getSourceFolder(compilationUnit));
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
        catch (CoreException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }

        return emptySet();
    }

    private Collection<IType> findPotentialTargets(boolean withLikelyMatches) throws CoreException
    {
        boolean qualifyWithPackage = ! withLikelyMatches;
        Set<String> patterns = new LinkedHashSet<>(nameEvaluation.getAllCorrespondingClassPatterns(qualifyWithPackage));

        if(type != null && ! nameEvaluation.isTestCase())
        {
            try
            {
                ITypeHierarchy hierarchy = type.newTypeHierarchy(new NullProgressMonitor());
                for (IType superType : hierarchy.getAllSupertypes(type))
                {
                    if(! superType.getFullyQualifiedName().startsWith("java.lang."))
                    {
                        ClassNameEvaluation superEval = preferences.getTestClassNamePattern().evaluate(superType);
                        patterns.addAll(superEval.getAllCorrespondingClassPatterns(qualifyWithPackage));
                    }
                }

                if(type.isInterface() || Flags.isAbstract(type.getFlags()))
                {
                    for (IType subType : hierarchy.getAllSubtypes(type))
                    {
                        if(! Flags.isAbstract(subType.getFlags()) && ! subType.isInterface())
                        {
                            ClassNameEvaluation subEval = preferences.getTestClassNamePattern().evaluate(subType);
                            patterns.addAll(subEval.getAllCorrespondingClassPatterns(qualifyWithPackage));
                        }
                    }
                }
            }
            catch (JavaModelException e)
            {
                LogHandler.getInstance().handleExceptionLog(e);
            }
        }

        Set<IType> matches = SearchTools.searchFor(patterns, searchScope);

        if(nameEvaluation.isTestCase())
        {
            Set<IType> allMatches = new LinkedHashSet<>(matches);
            Set<IType> concreteImplementations = new LinkedHashSet<>();
            for (IType match : matches)
            {
                try
                {
                    if(match.isInterface() || Flags.isAbstract(match.getFlags()))
                    {
                        concreteImplementations.addAll(SearchTools.findConcreteSubclasses(match));
                    }
                }
                catch (JavaModelException e)
                {
                    // ignore
                }
            }

            if(! concreteImplementations.isEmpty())
            {
                allMatches.addAll(concreteImplementations);

                for (Iterator<IType> it = allMatches.iterator(); it.hasNext();)
                {
                    IType match = it.next();
                    try
                    {
                        if((match.isInterface() || Flags.isAbstract(match.getFlags())) && ! hasImplementation(match))
                        {
                            it.remove();
                        }
                    }
                    catch (JavaModelException e)
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
        for (IMethod method : type.getMethods())
        {
            if(Flags.isDefaultMethod(method.getFlags()))
            {
                return true;
            }
            if(! Flags.isAbstract(method.getFlags()) && ! type.isInterface())
            {
                return true;
            }
        }
        return false;
    }
}
