package org.moreunit.util;

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

/**
 * Encalpsulates the implementation to find the testcases from a given
 * compilationUnit.
 * 
 * @author giana 13.05.2006 12:49:12
 */
public class TestCaseDiviner
{
    private static final String WC = StringConstants.WILDCARD;
    
    private ICompilationUnit compilationUnit;
    private Set<IType> matches = new LinkedHashSet<IType>();;
    private IType source;

    private Preferences preferences;

    public TestCaseDiviner(ICompilationUnit compilationUnit, Preferences preferences)
    {
        this.compilationUnit = compilationUnit;
        this.source = getSource();
        this.preferences = preferences;
        try
        {
            findPotentialTargets();
        }
        catch (CoreException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }
    }

    public TestCaseDiviner(ICompilationUnit compilationUnit, Preferences preferences, IType source)
    {
        this.compilationUnit = compilationUnit;
        this.source = source;
        this.preferences = preferences;
        try
        {
            findPotentialTargets();
        }
        catch (CoreException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }
    }

    public Set<IType> getMatches()
    {
        return this.matches;
    }

    private void findPotentialTargets() throws CoreException
    {
        if(this.source == null)
        {
            return;
        }

        this.matches = new LinkedHashSet<IType>();
        String[] prefixes = this.preferences.getPrefixes(getJavaProject());
        for (String element : prefixes)
        {
            this.matches.addAll(SearchTools.searchFor(getSearchTerm(this.source, element, true), this.compilationUnit, getSearchScope()));
        }
        String[] suffixes = this.preferences.getSuffixes(getJavaProject());
        for (String element : suffixes)
        {
            this.matches.addAll(SearchTools.searchFor(getSearchTerm(this.source, element, false), this.compilationUnit, getSearchScope()));
        }
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
    public IType getSource()
    {
        try
        {
            IType[] allTypes = this.compilationUnit.getAllTypes();
            if(allTypes.length > 0)
            {
                IType primaryType = allTypes[0]; 
                if (primaryType.isClass() || primaryType.isEnum())
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

    private String getSearchTerm(IType type, String qualifier, boolean prefixMatch)
    {
        if(this.preferences.shouldUseFlexibleTestCaseNaming(getJavaProject()))
        {
            // TODO Nicolas: should we systematically _surround_ name with wildcards or let the user choose with better options?
            return prefixMatch ? qualifier + WC + type.getTypeQualifiedName() + WC : type.getTypeQualifiedName() + WC + qualifier;
        }
        else
        {
            return prefixMatch ? qualifier + type.getTypeQualifiedName() : type.getTypeQualifiedName() + qualifier;
        }
    }

}