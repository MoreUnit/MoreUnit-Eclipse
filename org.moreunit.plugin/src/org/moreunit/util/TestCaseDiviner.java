package org.moreunit.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.ProjectPreferences;

/**
 * Encalpsulates the implementation to find the testcases from a given
 * compilationUnit.
 * 
 * @author giana 13.05.2006 12:49:12
 */
public class TestCaseDiviner
{
    private final ICompilationUnit compilationUnit;
    private final ProjectPreferences preferences;
    private final IType source;
    private Collection<IType> perfectMatches;
    private Collection<IType> likelyMatches;

    public TestCaseDiviner(ICompilationUnit compilationUnit, Preferences preferences)
    {
        this(compilationUnit, preferences, getSource(compilationUnit));
    }

    public TestCaseDiviner(ICompilationUnit compilationUnit, Preferences preferences, IType source)
    {
        this.compilationUnit = compilationUnit;
        this.preferences = preferences.getProjectView(getJavaProject());
        this.source = source;
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

        return Collections.<IType> emptySet();
    }

    private Collection<IType> findPotentialTargets(boolean withLikelyMatches) throws CoreException
    {
        if(this.source == null)
        {
            return Collections.<IType> emptySet();
        }

        if(withLikelyMatches)
        {
            return findPotentialTargetsWithPackage(null);
        }
        else
        {
            String testPackageName = getTestPackageName(source.getPackageFragment().getElementName());
            return findPotentialTargetsWithPackage(testPackageName);
        }
    }

    private String getTestPackageName(String cutPackageName)
    {
        String testPackagePrefix = this.preferences.getPackagePrefix();
        String testPackageSuffix = this.preferences.getPackageSuffix();
        String testPackageName = cutPackageName;

        if(! BaseTools.isStringTrimmedEmpty(testPackagePrefix))
        {
            testPackageName = String.format("%s.%s", testPackagePrefix, testPackageName);
        }

        if(! BaseTools.isStringTrimmedEmpty(testPackageSuffix))
        {
            testPackageName = String.format("%s.%s", testPackageName, testPackageSuffix);
        }

        return testPackageName;
    }

    private Set<IType> findPotentialTargetsWithPackage(String packageName) throws CoreException
    {
        Set<IType> matches = new LinkedHashSet<IType>();

        for (String prefix : this.preferences.getClassPrefixes())
        {
            matches.addAll(SearchTools.searchFor(getSearchTerm(packageName, this.source, prefix, true), getSearchScope()));
        }

        for (String suffix : this.preferences.getClassSuffixes())
        {
            matches.addAll(SearchTools.searchFor(getSearchTerm(packageName, this.source, suffix, false), getSearchScope()));
        }

        return matches;
    }

    private IJavaSearchScope getSearchScope()
    {
        return SearchScopeSingelton.getInstance().getSearchScope(PluginTools.getSourceFolder(compilationUnit));
    }

    private IJavaProject getJavaProject()
    {
        return compilationUnit.getJavaProject();
    }

    /*
     * public for Testing purposes
     */
    public static IType getSource(ICompilationUnit compilationUnit)
    {
        try
        {
            IType[] allTypes = compilationUnit.getAllTypes();
            if(allTypes.length > 0)
            {
                IType primaryType = allTypes[0];
                if(primaryType.isClass() || primaryType.isEnum())
                {
                    return primaryType;
                }
            }
        }
        catch (JavaModelException exc)
        {
            LogHandler.getInstance().handleInfoLog(exc.getMessage());
        }

        return null;
    }

    private String getSearchTerm(String packageName, IType type, String qualifier, boolean prefixMatch)
    {
        final String wildCard = this.preferences.shouldUseFlexibleTestCaseNaming() ? StringConstants.WILDCARD : "";
        String packageDot = packageName == null ? "" : packageName + ".";

        if(prefixMatch)
        {
            // TODO Nicolas: should we systematically _surround_ name with
            // wildcards or let the user choose with better options?
            return String.format("%s%s%s%s%s", packageDot, qualifier, wildCard, type.getTypeQualifiedName(), wildCard);
        }
        else
        {
            return String.format("%s%s%s%s", packageDot, type.getTypeQualifiedName(), wildCard, qualifier);
        }
    }

}
