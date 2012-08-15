package org.moreunit.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.log.LogHandler;
import org.moreunit.util.MethodCallFinder;
import org.moreunit.util.TestMethodCalleeFinder;
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
    public TestCaseTypeFacade(ICompilationUnit compilationUnit)
    {
        super(compilationUnit);
    }

    public IType getCorrespondingClassUnderTest()
    {
        Collection<IType> correspondingCuts = getCorrespondingClasses(false);
        if(correspondingCuts.isEmpty())
        {
            return null;
        }

        return correspondingCuts.iterator().next();
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
