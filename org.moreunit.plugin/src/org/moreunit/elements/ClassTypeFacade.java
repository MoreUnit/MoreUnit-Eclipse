package org.moreunit.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.ui.ChooseDialog;
import org.moreunit.ui.MemberContentProvider;
import org.moreunit.util.MethodCallFinder;
import org.moreunit.util.MethodTestCallerFinder;
import org.moreunit.util.TestCaseDiviner;
import org.moreunit.util.TestMethodDiviner;
import org.moreunit.util.TestMethodDivinerFactory;
import org.moreunit.wizards.NewClassyWizard;
import org.moreunit.wizards.NewTestCaseWizard;

/**
 * ClassTypeFacade offers easy access to a simple java file within eclipse. The
 * file represented by this instance is not a testcase.
 * <p>
 * 30.09.2010 Gro Added Method {@link #isNewTestClassCreated()}
 * 
 * @author vera 23.05.2006 20:28:52
 * @version 30.09.2010
 */
public class ClassTypeFacade extends TypeFacade
{

    private TestCaseDiviner testCaseDiviner;
    TestMethodDivinerFactory testMethodDivinerFactory;
    TestMethodDiviner testMethodDiviner;

    // Is a new test class created? Important for extension point clients.
    private boolean newTestClassCreated = false;

    public ClassTypeFacade(ICompilationUnit compilationUnit)
    {
        super(compilationUnit);
        testMethodDivinerFactory = new TestMethodDivinerFactory(compilationUnit);
        testMethodDiviner = testMethodDivinerFactory.create();
    }

    public ClassTypeFacade(IEditorPart editorPart)
    {
        super(editorPart);
        testMethodDivinerFactory = new TestMethodDivinerFactory(compilationUnit);
        testMethodDiviner = testMethodDivinerFactory.create();
    }

    public ClassTypeFacade(IFile file)
    {
        super(file);
        testMethodDivinerFactory = new TestMethodDivinerFactory(compilationUnit);
        testMethodDiviner = testMethodDivinerFactory.create();
    }

    /**
     * Is a new test class created?
     * 
     * @return New test class?
     */
    public boolean isNewTestClassCreated()
    {
        return newTestClassCreated;
    }

    /**
     * Returns the corresponding testcase of the javaFileFacade. If there are
     * more than one testcases the user has to make a choice via a dialog. If no
     * test is found <code>null</code> is returned.
     * 
     * @return one of the corresponding testcases
     */
    public IType getOneCorrespondingTestCase(boolean createIfNecessary, String promptText)
    {
        Set<IType> testcases = getCorrespondingTestCaseList();

        IType testcaseToJump = null;
        if(testcases.size() == 1)
        {
            testcaseToJump = (IType) testcases.toArray()[0];
        }
        else if(testcases.size() > 1)
        {
            MemberContentProvider contentProvider = new MemberContentProvider(testcases, null);
            testcaseToJump = new ChooseDialog<IType>(promptText, contentProvider).getChoice();
        }
        else if(createIfNecessary)
        {
            testcaseToJump = new NewTestCaseWizard(getType()).open();

            // Remember, if we created a new test class, cause extension point
            // client need it to know
            if(testcaseToJump != null)
            {
                newTestClassCreated = true;
            }
        }

        return testcaseToJump;
    }

    public Set<IType> getCorrespondingTestCaseList()
    {
        return getTestCaseDiviner().getMatches();
    }

    public IMethod getCorrespondingTestMethod(IMethod method, IType testCaseType)
    {
        String nameOfCorrespondingTestMethod = testMethodDiviner.getTestMethodNameFromMethodName(method.getElementName());

        if(testCaseType == null)
        {
            return null;
        }

        try
        {
            IMethod[] methodsOfType = testCaseType.getCompilationUnit().findPrimaryType().getMethods();
            for (IMethod testmethod : methodsOfType)
            {
                if(testmethod.getElementName().startsWith(nameOfCorrespondingTestMethod))
                {
                    return testmethod;
                }
            }
        }
        catch (JavaModelException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }

        return null;
    }

    public List<IMethod> getCorrespondingTestMethods(IMethod method)
    {
        Set<IType> allTestCases = getCorrespondingTestCaseList();
        return getTestMethodsForTestCases(method, allTestCases);
    }

    private List<IMethod> getTestMethodsForTestCases(IMethod method, Set<IType> testCases)
    {
        List<IMethod> result = new ArrayList<IMethod>();

        for (IType testCaseType : testCases)
        {
            result.addAll(getTestMethodsForTestCase(method, testCaseType));
        }

        return result;
    }

    private List<IMethod> getTestMethodsForTestCase(IMethod method, IType testCaseType)
    {
        List<IMethod> result = new ArrayList<IMethod>();

        if(testCaseType == null)
        {
            return result;
        }

        String nameOfCorrespondingTestMethod = testMethodDiviner.getTestMethodNameFromMethodName(method.getElementName());

        try
        {
            IMethod[] methodsOfType = testCaseType.getCompilationUnit().findPrimaryType().getMethods();
            for (IMethod testmethod : methodsOfType)
            {
                if(testmethod.getElementName().startsWith(nameOfCorrespondingTestMethod))
                {
                    result.add(testmethod);
                }
            }
        }
        catch (JavaModelException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }

        return result;
    }

    public boolean hasTestMethod(IMethod method, MethodSearchMode searchMethod)
    {
        final Set<IMethod> correspondingTestMethods = new HashSet<IMethod>();
        if(searchMethod == MethodSearchMode.BY_CALL)
        {
            Set<IType> correspondingClasses = getCorrespondingClasses();
            if(! correspondingClasses.isEmpty())
            {
                correspondingTestMethods.addAll(getCallRelationshipFinder(method, correspondingClasses).getMatches(new NullProgressMonitor()));
            }
        }
        else
        {
            correspondingTestMethods.addAll(getCorrespondingTestMethods(method));
        }
        return ! correspondingTestMethods.isEmpty();
    }

    /**
     * Getter uses lazy caching.
     */
    private TestCaseDiviner getTestCaseDiviner()
    {
        if(this.testCaseDiviner == null)
        {
            this.testCaseDiviner = new TestCaseDiviner(this.compilationUnit, Preferences.getInstance());
        }

        return this.testCaseDiviner;
    }

    @Override
    protected Set<IType> getCorrespondingClasses()
    {
        return new LinkedHashSet<IType>(getCorrespondingTestCaseList());
    }

    @Override
    protected Collection<IMethod> getCorrespondingMethodsInClasses(IMethod method, Set<IType> classes)
    {
        return getTestMethodsForTestCases(method, classes);
    }

    @Override
    protected MethodCallFinder getCallRelationshipFinder(IMethod method, Set<IType> searchScope)
    {
        return new MethodTestCallerFinder(method, searchScope);
    }

    @Override
    protected NewClassyWizard newCorrespondingClassWizard(IType fromType)
    {
        return new NewTestCaseWizard(fromType);
    }
    
    public IType getOneCorrespondingTestCase(boolean createIfNecessary)
    {
        return getOneCorrespondingTestCase(createIfNecessary, "Please choose a test case...");
    }

    public Collection<IType> getCorrespondingTestCases()
    {
        return getCorrespondingTestCaseList();
    }

    public Collection< ? extends IMember> getCorrespondingTestMembers(IMethod method, boolean extendedSearch)
    {
        Set<IMethod> testMethods = new LinkedHashSet<IMethod>();

        Set<IType> testCases = getCorrespondingClasses();

        if(method != null && ! testCases.isEmpty())
        {
            testMethods.addAll(getCorrespondingMethodsInClasses(method, testCases));
            if(extendedSearch)
            {
                testMethods.addAll(getCallRelationshipFinder(method, testCases).getMatches(new NullProgressMonitor()));
            }
        }

        return testMethods.isEmpty() ? testCases : testMethods;
    }

}
