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

// $Log: not supported by cvs2svn $
// Revision 1.15  2010/06/30 22:55:56  makkimesser
// CodeWarnings resolved
// Deprecated API removed
// Missing AnnotationType Extension added
//
// Revision 1.14  2009/10/19 19:33:16  gianasista
// Bugfix switching for enums
//
// Revision 1.13  2009/04/05 19:14:27  gianasista
// code formatter
//
// Revision 1.12 2008/03/21 18:21:00 gianasista
// First version of new property page with source folder mapping
//
// Revision 1.11 2008/02/27 09:03:52 channingwalton
// corrected a misspelt method
//
// Revision 1.10 2008/02/20 19:24:14 gianasista
// Rename of classes for constants
//
// Revision 1.9 2008/02/04 20:10:52 gianasista
// Bugfix: project specific settings
//
// Revision 1.8 2007/11/19 21:13:51 gianasista
// Patch from Bjoern: project specific settings
//
// Revision 1.7 2007/01/24 20:13:38 gianasista
// Property for felxible testcase matching
//
// Revision 1.6 2007/01/14 21:14:14 gianasista
// Changed logging behaviour
//
// Revision 1.5 2007/01/12 21:56:14 gianasista
// Better matching for testcases [1575497]
//
// Revision 1.4 2006/09/18 20:00:06 channingwalton
// the CVS substitions broke with my last check in because I put newlines in
// them
//
// Revision 1.3 2006/09/18 19:56:03 channingwalton
// Fixed bug [ 1537839 ] moreunit cannot find test class if it is in wrong
// package. Also found a classcast exception in UnitDecorator whicj I've guarded
// for. Fixed the Class wizard icon
//
// Revision 1.2 2006/08/29 19:35:40 gianasista
// Bugfix to avoid NPE
//
// Revision 1.1.1.1 2006/08/13 14:31:16 gianasista
// initial
//
// Revision 1.1 2006/06/22 20:22:28 gianasista
// package rename
//
// Revision 1.1 2006/06/19 20:08:48 gianasista
// CVS Refactoring
//
// Revision 1.4 2006/06/03 16:50:26 gianasista
// findPotentialTargets wasn't null-safe
//
// Revision 1.3 2006/05/14 22:27:10 channingwalton
// made use of generics to remove some warnings
//
// Revision 1.2 2006/05/14 19:10:58 gianasista
// Smaller enhancements
//
// Revision 1.1 2006/05/13 18:30:24 gianasista
// Searching for testcases for a class (based on preferences) + Tests
//
