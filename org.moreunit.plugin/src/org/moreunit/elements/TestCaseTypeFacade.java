package org.moreunit.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.ui.IEditorPart;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
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
        List<IType> correspondingClassesUnderTest = getCorrespondingClassesUnderTest();
        if(correspondingClassesUnderTest == null || correspondingClassesUnderTest.size() == 0)
            return null;

        return correspondingClassesUnderTest.get(0);
    }

    public List<IType> getCorrespondingClassesUnderTest()
    {
        Preferences preferences = Preferences.getInstance();
        List<String> testedClasses = BaseTools.getTestedClass(getType().getTypeQualifiedName(), preferences.getPrefixes(getJavaProject()), preferences.getSuffixes(getJavaProject()), preferences.getTestPackagePrefix(getJavaProject()), preferences.getTestPackageSuffix(getJavaProject()));
        if(testedClasses.isEmpty())
        {
            return null;
        }

        List<IType> resultList = new ArrayList<IType>();
        try
        {
            List<String> typeNames = testedClasses;
            if(preferences.shouldUseFlexibleTestCaseNaming(getJavaProject()))
            {
                typeNames = BaseTools.getListOfUnqualifiedTypeNames(testedClasses);
            }
                    
            for (String typeName : typeNames)
            {
                Set<IType> searchFor = SearchTools.searchFor(typeName, compilationUnit, getSearchScope(compilationUnit));
                for (IType searchForResult : searchFor)
                {
                    resultList.add(searchForResult);
                }
            }
        }
        catch (Exception exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }
        return resultList;
    }

    private static IJavaSearchScope getSearchScope(ICompilationUnit compilationUnit)
    {
        return SearchScopeSingelton.getInstance().getSearchScope(getSourceFolder(compilationUnit));
    }

    private static IPackageFragmentRoot getSourceFolder(ICompilationUnit compilationUnit)
    {
        return PluginTools.getSourceFolder(compilationUnit);
    }
    
    public List<IMethod> getCorrespondingTestedMethods(IMethod testMethod, Set<IType> classesUnderTest)
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
    protected Set<IType> getCorrespondingClasses()
    {
        return new LinkedHashSet<IType>(getCorrespondingClassesUnderTest());
    }

    @Override
    protected Collection<IMethod> getCorrespondingMethodsInClasses(IMethod method, Set<IType> classes)
    {
        return getCorrespondingTestedMethods(method, classes);
    }

    @Override
    protected MethodCallFinder getCallRelationshipFinder(IMethod method, Set<IType> searchScope)
    {
        return new TestMethodCalleeFinder(method, searchScope);
    }
    
    @Override
    protected NewClassyWizard newCorrespondingClassWizard(IType fromType)
    {
        return new NewClassWizard(fromType);
    }
}