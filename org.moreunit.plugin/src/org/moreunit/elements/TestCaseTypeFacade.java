package org.moreunit.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.ui.IEditorPart;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.ProjectPreferences;
import org.moreunit.util.BaseTools;
import org.moreunit.util.MethodCallFinder;
import org.moreunit.util.PluginTools;
import org.moreunit.util.SearchScopeSingelton;
import org.moreunit.util.SearchTools;
import org.moreunit.util.TestMethodCalleeFinder;
import org.moreunit.util.TestMethodDiviner;
import org.moreunit.util.TestMethodDivinerFactory;
import org.moreunit.wizards.NewClassWizard;
import org.moreunit.wizards.NewClassyWizard;

/**
 * ClassTypeFacade offers easy access to a simple java file within eclipse. The
 * file represented by this instance is not a testcase.
 * 
 * @author vera 23.05.2006 20:29:57
 */
public class TestCaseTypeFacade extends TypeFacade
{
    TestMethodDivinerFactory testMethodDivinerFactory;
    TestMethodDiviner testMethodDiviner;

    public TestCaseTypeFacade(ICompilationUnit compilationUnit)
    {
        super(compilationUnit);
        testMethodDivinerFactory = new TestMethodDivinerFactory(compilationUnit);
        testMethodDiviner = testMethodDivinerFactory.create();
    }

    public TestCaseTypeFacade(IEditorPart editorPart)
    {
        super(editorPart);
        testMethodDivinerFactory = new TestMethodDivinerFactory(compilationUnit);
        testMethodDiviner = testMethodDivinerFactory.create();
    }

    public TestCaseTypeFacade(IFile file)
    {
        super(file);
        testMethodDivinerFactory = new TestMethodDivinerFactory(compilationUnit);
        testMethodDiviner = testMethodDivinerFactory.create();
    }

    public IType getCorrespondingClassUnderTest()
    {
        Collection<IType> correspondingCuts = getCorrespondingClassesUnderTest(false);
        if(correspondingCuts.isEmpty())
        {
            return null;
        }

        return correspondingCuts.iterator().next();
    }

    public Collection<IType> getCorrespondingClassesUnderTest(boolean alsoIncludeLikelyMatches)
    {
        Collection<IType> matches = new LinkedHashSet<IType>();

        ProjectPreferences prefs = Preferences.forProject(getJavaProject());
        List<String> testedClasses = BaseTools.getTestedClass(getType().getFullyQualifiedName(), prefs.getClassPrefixes(), prefs.getClassSuffixes(), prefs.getPackagePrefix(), prefs.getPackageSuffix());
        if(testedClasses.isEmpty())
        {
            return matches;
        }

        List<String> typeNames = testedClasses;
        if(prefs.shouldUseFlexibleTestCaseNaming())
        {
            typeNames = BaseTools.getListOfUnqualifiedTypeNames(testedClasses);
        }

        try
        {
            if(alsoIncludeLikelyMatches)
            {
                for (String typeName : typeNames)
                {
                    String typeNameOnly = typeName.substring(typeName.lastIndexOf(".") + 1);
                    matches.addAll(SearchTools.searchFor(typeNameOnly, getSearchScope(compilationUnit)));
                }
            }
            else
            {
                for (String typeName : typeNames)
                {
                    matches.addAll(SearchTools.searchFor(typeName, getSearchScope(compilationUnit)));
                }
            }
        }
        catch (CoreException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }

        return matches;
    }

    private static IJavaSearchScope getSearchScope(ICompilationUnit compilationUnit)
    {
        return SearchScopeSingelton.getInstance().getSearchScope(getSourceFolder(compilationUnit));
    }

    private static IPackageFragmentRoot getSourceFolder(ICompilationUnit compilationUnit)
    {
        return PluginTools.getSourceFolder(compilationUnit);
    }

    public List<IMethod> getCorrespondingTestedMethods(IMethod testMethod, Collection<IType> classesUnderTest)
    {
        List<IMethod> result = new ArrayList<IMethod>();

        for (IType classUnderTest : classesUnderTest)
        {
            result.addAll(getCorrespondingTestedMethods(testMethod, classUnderTest));
        }

        return result;
    }

    public List<IMethod> getCorrespondingTestedMethods(IMethod testMethod, IType classUnderTest)
    {
        List<IMethod> testedMethods = new ArrayList<IMethod>();
        try
        {
            String testedMethodName = testMethodDiviner.getMethodNameFromTestMethodName(testMethod.getElementName());
            if(testedMethodName != null)
            {
                IMethod[] foundTestMethods = classUnderTest.getMethods();
                for (IMethod method : foundTestMethods)
                {
                    if(method.exists())
                    {
                        if(testedMethodName.equals(method.getElementName()))
                        {
                            testedMethods.clear();
                            testedMethods.add(method);
                            break;
                        }
                        if(testedMethodName.startsWith(method.getElementName()))
                        {
                            testedMethods.add(method);
                        }
                    }
                }
            }
        }
        catch (JavaModelException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }

        return testedMethods;
    }

    @Override
    protected Collection<IType> getCorrespondingClasses(boolean alsoIncludeLikelyMatches)
    {
        return getCorrespondingClassesUnderTest(alsoIncludeLikelyMatches);
    }

    @Override
    protected Collection<IMethod> getCorrespondingMethodsInClasses(IMethod method, Collection<IType> classes)
    {
        return getCorrespondingTestedMethods(method, classes);
    }

    @Override
    protected MethodCallFinder getCallRelationshipFinder(IMethod method, Collection<IType> searchScope)
    {
        return new TestMethodCalleeFinder(method, searchScope);
    }

    @Override
    protected NewClassyWizard newCorrespondingClassWizard(IType fromType)
    {
        return new NewClassWizard(fromType);
    }
}
