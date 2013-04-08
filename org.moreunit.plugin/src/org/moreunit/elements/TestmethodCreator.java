package org.moreunit.elements;

import static java.util.Collections.addAll;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.junit.util.JUnitStubUtility;
import org.moreunit.core.util.StringConstants;
import org.moreunit.elements.ClassTypeFacade.CorrespondingTestCase;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.util.MoreUnitContants;
import org.moreunit.util.TestMethodDiviner;
import org.moreunit.util.TestMethodDivinerFactory;

/**
 * @author vera 27.06.2007 20:59:15<br>
 *         This class is responsible for creating the testmethod-stubs. There
 *         are 3 different types of stubs:<br>
 *         <ul>
 *         <li>JUnit 3 tests</li>
 *         <li>JUnit 4 tests</li>
 *         <li>TestNG tests</li>
 *         </ul>
 */
public class TestmethodCreator
{
    private ICompilationUnit compilationUnit;
    private ICompilationUnit testCaseCompilationUnit;
    private String testType;
    private String defaultTestMethodContent = "";
    private TestMethodDiviner testMethodDiviner;

    private boolean shouldCreateFinalMethod;
    private boolean shouldCreateTasks;

    /**
     * @param compilationUnit Could be CUT or a test. createTestMethod will
     *            distinguish
     */
    public TestmethodCreator(ICompilationUnit compilationUnit, String testType, String defaultTestMethodContent)
    {
        this.compilationUnit = compilationUnit;
        this.testType = testType;
        testMethodDiviner = new TestMethodDivinerFactory(compilationUnit).create(testType);
        this.defaultTestMethodContent = defaultTestMethodContent;
    }

    public TestmethodCreator(ICompilationUnit compilationUnit, String testType, String defaultTestMethodContent, boolean shouldCreateFinalMethod, boolean shouldCreateTasks)
    {
        this(compilationUnit, testType, defaultTestMethodContent);

        this.shouldCreateFinalMethod = shouldCreateFinalMethod;
        this.shouldCreateTasks = shouldCreateTasks;
    }

    public TestmethodCreator(ICompilationUnit compilationUnit, ICompilationUnit testCaseCompilationUnit, String testType, String defaultTestMethodContent)
    {
        this(compilationUnit, testType, defaultTestMethodContent);

        this.testCaseCompilationUnit = testCaseCompilationUnit;
    }

    public void createTestMethods(List<IMethod> methodsUnderTest)
    {
        List<IMethod> overloadedMethods = getOverloadedMethods();

        for (IMethod methodUnderTest : methodsUnderTest)
        {
            createFirstTestMethod(methodUnderTest, overloadedMethods);
        }
    }

    // borrowed from org.eclipse.jdt.ui.wizards.NewTypeWizardPage
    private List<IMethod> getOverloadedMethods()
    {
        List<IMethod> allMethods = new ArrayList<IMethod>();
        try
        {
            addAll(allMethods, compilationUnit.findPrimaryType().getMethods());
        }
        catch (JavaModelException e)
        {
            // we can live without them
            return allMethods;
        }

        List<IMethod> overloadedMethods = new ArrayList<IMethod>();
        for (int i = 0; i < allMethods.size(); i++)
        {
            IMethod current = allMethods.get(i);
            String currentName = current.getElementName();
            boolean currentAdded = false;
            for (ListIterator<IMethod> iter = allMethods.listIterator(i + 1); iter.hasNext();)
            {
                IMethod iterMethod = iter.next();
                if(iterMethod.getElementName().equals(currentName))
                {
                    // method is overloaded
                    if(! currentAdded)
                    {
                        overloadedMethods.add(current);
                        currentAdded = true;
                    }
                    overloadedMethods.add(iterMethod);
                    iter.remove();
                }
            }
        }
        return overloadedMethods;
    }

    public MethodCreationResult createTestMethod(IMethod method)
    {
        if(method == null)
            return MethodCreationResult.noMethodCreated();

        if(TypeFacade.isTestCase(compilationUnit.findPrimaryType()))
        {
            // testcase code is created based on the testCaseCompilationUnit
            // instance
            // if TestMethodCreator got created with testcase only, the
            // testCaseCompilationUnit must be set here
            testCaseCompilationUnit = compilationUnit;
            return MethodCreationResult.from(createAnotherTestMethod(method));
        }

        List<IMethod> overloadedMethods = getOverloadedMethods();
        return createFirstTestMethod(method, overloadedMethods);
    }

    /**
     * Create the first test method, e.g. create the new test class. This method
     * calls the JUnit Wizard to create the new test class and tries to find the
     * expected test method corresponding to the source method under test.
     * 
     * @param methodUnderTest Method under test.
     * @return Test method, or <code>null</code> if it is not found.
     */
    private MethodCreationResult createFirstTestMethod(IMethod methodUnderTest, List<IMethod> overloadedMethods)
    {
        ClassTypeFacade classTypeFacade = new ClassTypeFacade(compilationUnit);
        if(testCaseCompilationUnit == null)
        {
            CorrespondingTestCase oneCorrespondingTestCase = classTypeFacade.getOneCorrespondingTestCase(true);

            // This happens if the user chooses cancel from the wizard
            if(! oneCorrespondingTestCase.found())
            {
                return MethodCreationResult.noMethodCreated();
            }
            testCaseCompilationUnit = oneCorrespondingTestCase.get().getCompilationUnit();
        }

        // compilationUnit = oneCorrespondingTestCase.getCompilationUnit();
        String testMethodName = testMethodDiviner.getTestMethodNameFromMethodName(methodUnderTest.getElementName());

        if(overloadedMethods.contains(methodUnderTest))
        {
            testMethodName = appendParameterNamesToMethodName(testMethodName, methodUnderTest.getParameterTypes());
        }

        // If test method exists, ready
        IMethod existingMethod = findTestMethod(testMethodName);
        if(existingMethod != null)
            return MethodCreationResult.methodAlreadyExists(existingMethod);

        if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4.equals(testType))
            return MethodCreationResult.from(createJUnit4Testmethod(testMethodName, null));
        else if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3.equals(testType))
            return MethodCreationResult.from(createJUnit3Testmethod(testMethodName, null));
        else if(PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(testType))
            return MethodCreationResult.from(createTestNgTestMethod(testMethodName, null));

        // This should never be called;
        return MethodCreationResult.noMethodCreated();
    }

    // borrowed from org.eclipse.jdt.ui.wizards.NewTypeWizardPage
    private String appendParameterNamesToMethodName(String name, String[] parameters)
    {
        StringBuilder buffer = new StringBuilder(name);
        for (int i = 0; i < parameters.length; i++)
        {
            final StringBuilder buf = new StringBuilder(Signature.getSimpleName(Signature.toString(Signature.getElementType(parameters[i]))));
            final char character = buf.charAt(0);
            if(buf.length() > 0 && ! Character.isUpperCase(character))
                buf.setCharAt(0, Character.toUpperCase(character));
            buffer.append(buf);
            for (int j = 0, arrayCount = Signature.getArrayCount(parameters[i]); j < arrayCount; j++)
            {
                buffer.append("Array"); //$NON-NLS-1$
            }
        }
        return buffer.toString();
    }

    private IMethod createAnotherTestMethod(IMethod testMethod)
    {
        String testMethodName = testMethod.getElementName();

        if(doesMethodExist(testMethodName))
            testMethodName = testMethodName.concat(MoreUnitContants.SUFFIX_NAME);

        if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4.equals(testType))
            return createJUnit4Testmethod(testMethodName, getSiblingForInsert(testMethod));
        else if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3.equals(testType))
            return createJUnit3Testmethod(testMethodName, getSiblingForInsert(testMethod));
        else if(PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(testType))
            return createTestNgTestMethod(testMethodName, getSiblingForInsert(testMethod));

        return null;
    }

    /**
     * If a additional test method should be created it would be nice if this
     * method is placed directly below the test method. As the {@link IType}
     * createTestMethod placed the method above the sibling the method after the
     * testmethod must be the sibling parameter for this method. This method
     * returns null if the testmethod is the last method in the type
     * 
     * @return
     */
    private IMethod getSiblingForInsert(IMethod testMethod)
    {
        try
        {
            IMethod[] methods = testCaseCompilationUnit.findPrimaryType().getMethods();
            for (int i = 0; i < methods.length; i++)
            {
                boolean isNotLastMethodInClass = i < methods.length - 1;
                if(testMethod == methods[i] && isNotLastMethodInClass)
                {
                    return methods[i + 1];
                }
            }
        }
        catch (JavaModelException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }

        return null;
    }

    protected IMethod createJUnit3Testmethod(String testMethodName, IMethod sibling)
    {
        return createMethod(testMethodName, getJUnit3MethodStub(testMethodName), sibling);
    }

    private IMethod createTestNgTestMethod(String testMethodName, IMethod sibling)
    {
        return createMethod(testMethodName, getTestNgMethodStub(testMethodName), sibling);
    }

    private String getTestNgMethodStub(String testmethodName)
    {
        StringBuffer methodContent = new StringBuffer();
        methodContent.append("@Test").append(StringConstants.NEWLINE);
        methodContent.append(getTestMethodString(testmethodName));

        return methodContent.toString();
    }

    private String getJUnit3MethodStub(String testmethodName)
    {
        StringBuffer methodContent = new StringBuffer();
        methodContent.append(getTestMethodString(testmethodName));

        return methodContent.toString();
    }

    protected IMethod createJUnit4Testmethod(String testMethodName, IMethod sibling)
    {
        return createMethod(testMethodName, getJUnit4MethodStub(testMethodName), sibling);
    }

    private String getJUnit4MethodStub(String testmethodName)
    {
        StringBuffer methodContent = new StringBuffer();
        methodContent.append("@Test").append(StringConstants.NEWLINE);
        methodContent.append(getTestMethodString(testmethodName));

        return methodContent.toString();
    }

    private String getTestMethodString(String testmethodName)
    {
        String finalPlaceholder = " ";
        if(shouldCreateFinalMethod)
            finalPlaceholder = "final ";

        String recommendedLineSeparator = StringConstants.NEWLINE;
        try
        {
            recommendedLineSeparator = testCaseCompilationUnit.findRecommendedLineSeparator();
        }
        catch (JavaModelException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }

        String methodBody = defaultTestMethodContent;
        if(shouldCreateTasks)
        {
            String todoTaskTag = JUnitStubUtility.getTodoTaskTag(compilationUnit.getJavaProject());
            if(todoTaskTag != null)
            {
                methodBody = "// " + todoTaskTag + recommendedLineSeparator + defaultTestMethodContent;
            }
        }
        return String.format("public %svoid %s() throws Exception {%s%s%s}", finalPlaceholder, testmethodName, recommendedLineSeparator, methodBody, recommendedLineSeparator);
    }

    private IMethod createMethod(String methodName, String methodString, IMethod sibling)
    {
        if(doesMethodExist(methodName))
            return null;

        try
        {
            return testCaseCompilationUnit.findPrimaryType().createMethod(methodString, sibling, true, null);
        }
        catch (JavaModelException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }
        return null;
    }

    /**
     * Does test method exists? In case of any error, <code>false</code> is
     * returned.
     * 
     * @param testMethodName Name of test method.
     * @return Test method exists?
     */
    protected boolean doesMethodExist(String testMethodName)
    {
        return findTestMethod(testMethodName) != null;
    }

    /**
     * Try to find the test method. The first match is returned.
     * <p>
     * In case of any error, <code>null</code> is returned.
     * 
     * @param testMethodName Name of test method.
     * @return testmethod, or <code>null</code> if not found.
     */
    protected IMethod findTestMethod(String testMethodName)
    {
        try
        {
            IMethod[] existingTests = testCaseCompilationUnit.findPrimaryType().getMethods();
            for (int i = 0; i < existingTests.length; i++)
            {
                IMethod method = existingTests[i];
                if(testMethodName.equals(method.getElementName()))
                    return method;
            }
        }
        catch (JavaModelException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }

        return null;
    }
}
