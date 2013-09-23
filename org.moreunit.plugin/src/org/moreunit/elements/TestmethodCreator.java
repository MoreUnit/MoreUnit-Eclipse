package org.moreunit.elements;

import static java.util.Collections.addAll;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.junit.Messages;
import org.eclipse.jdt.internal.junit.util.JUnitStubUtility;
import org.eclipse.jdt.internal.junit.wizards.WizardMessages;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.moreunit.core.util.StringConstants;
import org.moreunit.elements.ClassTypeFacade.CorrespondingTestCase;
import org.moreunit.extensionpoints.AddTestMethodParticipatorHandler;
import org.moreunit.extensionpoints.IAddTestMethodContext;
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
    // to be used for testing only
    public static boolean discardExtensions;

    private ICompilationUnit compilationUnit;
    private ICompilationUnit testCaseCompilationUnit;
    private String testType;
    private String defaultTestMethodContent = "";
    private TestMethodDiviner testMethodDiviner;

    private boolean generateComments;
    private boolean shouldCreateFinalMethod;
    private boolean shouldCreateTasks;
    private CodeFormatter testFormatter;
    private boolean testCaseJustCreated;


    public TestmethodCreator(TestMethodCreationSettings settings)
    {
        compilationUnit = settings.compilationUnit;
        testType = settings.testType;
        generateComments = settings.generateComments;
        defaultTestMethodContent = settings.defaultTestMethodContent;
        shouldCreateFinalMethod = settings.shouldCreateFinalMethod;
        shouldCreateTasks = settings.shouldCreateTasks;

        testMethodDiviner = new TestMethodDivinerFactory(compilationUnit).create(testType);

        if (settings.maybeTestCaseCompilationUnit != null) {
            setTestCaseCompilationUnit(settings.maybeTestCaseCompilationUnit, settings.testCaseJustCreated);
        }
    }

    private void setTestCaseCompilationUnit(ICompilationUnit cu, boolean testCaseJustCreated)
    {
        testCaseCompilationUnit = cu;
        this.testCaseJustCreated = testCaseJustCreated;
        testFormatter = ToolFactory.createCodeFormatter(cu.getJavaProject().getOptions(true));
    }

    public List<IMethod> createTestMethods(List<IMethod> methodsUnderTest)
    {
        List<IMethod> createdMethods = new ArrayList<IMethod>(methodsUnderTest.size());
        List<IMethod> overloadedMethods = getOverloadedMethods();

        for (IMethod methodUnderTest : methodsUnderTest)
        {
            MethodCreationResult creationResult = createFirstTestMethod(methodUnderTest, overloadedMethods);
            if(creationResult.methodCreated())
            {
                createdMethods.add(creationResult.getMethod());
            }
        }

        return createdMethods;
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
            setTestCaseCompilationUnit(compilationUnit, false);
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
            CorrespondingTestCase testCase = classTypeFacade.getOneCorrespondingTestCase(true);

            // This happens if the user chooses cancel from the wizard
            if(! testCase.found())
            {
                return MethodCreationResult.noMethodCreated();
            }
            setTestCaseCompilationUnit(testCase.get().getCompilationUnit(), testCase.hasJustBeenCreated());
        }

        String testMethodName = testMethodDiviner.getTestMethodNameFromMethodName(methodUnderTest.getElementName());

        if(overloadedMethods.contains(methodUnderTest))
        {
            testMethodName = appendParameterNamesToMethodName(testMethodName, methodUnderTest.getParameterTypes());
        }

        // If test method exists, ready
        IMethod existingMethod = findTestMethod(testMethodName);
        if(existingMethod != null)
            return MethodCreationResult.methodAlreadyExists(existingMethod);

        String comment = generateTestMethodComment(methodUnderTest);

        IMethod testMethod = null;
        if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4.equals(testType))
            testMethod = createJUnit4Testmethod(testMethodName, null, comment);
        else if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3.equals(testType))
            testMethod = createJUnit3Testmethod(testMethodName, null, comment);
        else if(PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(testType))
            testMethod = createTestNgTestMethod(testMethodName, null, comment);

        if(! discardExtensions && testMethod != null)
        {
            IAddTestMethodContext testMethodContext = AddTestMethodParticipatorHandler.getInstance().callExtension(testMethod, methodUnderTest, testCaseJustCreated);
            if(testMethodContext.getTestMethod() != null)
                testMethod = testMethodContext.getTestMethod();
        }

        return MethodCreationResult.from(testMethod);
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

        String comment = getComments(testMethod);

        IMethod newTestMethod = null;
        if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4.equals(testType))
            newTestMethod = createJUnit4Testmethod(testMethodName, getSiblingForInsert(testMethod), comment);
        else if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3.equals(testType))
            newTestMethod = createJUnit3Testmethod(testMethodName, getSiblingForInsert(testMethod), comment);
        else if(PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(testType))
            newTestMethod = createTestNgTestMethod(testMethodName, getSiblingForInsert(testMethod), comment);

        if(! discardExtensions && newTestMethod != null)
        {
            IAddTestMethodContext testMethodContext = AddTestMethodParticipatorHandler.getInstance().maybeCallExtension(newTestMethod);
            if(testMethodContext != null && testMethodContext.getTestMethod() != null)
                return testMethodContext.getTestMethod();
        }

        return newTestMethod;
    }

    private String getComments(IMethod testMethod)
    {
        if(! generateComments)
            return "";

        try
        {
            String comment = "";
            ISourceRange javadocRange = testMethod.getJavadocRange();
            if(javadocRange != null)
            {
                String source = testMethod.getCompilationUnit().getSource();
                comment = source.substring(javadocRange.getOffset(), javadocRange.getOffset() + javadocRange.getLength());
                comment += "\n";
            }
            return comment;
        }
        catch (JavaModelException e)
        {
            return "";
        }
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

    protected IMethod createJUnit3Testmethod(String testMethodName, IMethod sibling, String comment)
    {
        return createMethod(testMethodName, getJUnit3MethodStub(testMethodName, comment), sibling);
    }

    private IMethod createTestNgTestMethod(String testMethodName, IMethod sibling, String comment)
    {
        return createMethod(testMethodName, getTestNgMethodStub(testMethodName, comment), sibling);
    }

    private String getTestNgMethodStub(String testmethodName, String comment)
    {
        StringBuilder methodContent = new StringBuilder();
        methodContent.append(comment);
        methodContent.append("@Test").append(StringConstants.NEWLINE);
        methodContent.append(getTestMethodString(testmethodName));

        return methodContent.toString();
    }

    private String getJUnit3MethodStub(String testmethodName, String comment)
    {
        StringBuilder methodContent = new StringBuilder();
        methodContent.append(comment);
        methodContent.append(getTestMethodString(testmethodName));

        return methodContent.toString();
    }

    protected IMethod createJUnit4Testmethod(String testMethodName, IMethod sibling, String comment)
    {
        return createMethod(testMethodName, getJUnit4MethodStub(testMethodName, comment), sibling);
    }

    private String getJUnit4MethodStub(String testmethodName, String comment)
    {
        StringBuilder methodContent = new StringBuilder();
        methodContent.append(comment);
        methodContent.append("@Test").append(StringConstants.NEWLINE);
        methodContent.append(getTestMethodString(testmethodName));

        return methodContent.toString();
    }

    // copied from
    // org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageOne.appendMethodComment(StringBuffer,
    // IMethod)
    private String generateTestMethodComment(IMethod testedMethod)
    {
        if(! generateComments)
            return "";

        String recommendedLineSeparator = findRecommendedLineSeparator();

        final StringBuffer buf = new StringBuffer("{@link "); //$NON-NLS-1$
        JavaElementLabels.getTypeLabel(testedMethod.getDeclaringType(), JavaElementLabels.T_FULLY_QUALIFIED, buf);
        buf.append('#');
        buf.append(testedMethod.getElementName());
        buf.append('(');
        String[] paramTypes = JUnitStubUtility.getParameterTypeNamesForSeeTag(testedMethod);
        for (int i = 0; i < paramTypes.length; i++)
        {
            if(i != 0)
            {
                buf.append(", "); //$NON-NLS-1$
            }
            buf.append(paramTypes[i]);

        }
        buf.append(')');
        buf.append('}');

        StringBuilder buffer = new StringBuilder();
        buffer.append("/**");//$NON-NLS-1$
        buffer.append(recommendedLineSeparator);
        buffer.append(" * ");//$NON-NLS-1$
        buffer.append(Messages.format(WizardMessages.NewTestCaseWizardPageOne_comment_class_to_test, buf.toString()));
        buffer.append(recommendedLineSeparator);
        buffer.append(" */");//$NON-NLS-1$
        buffer.append(recommendedLineSeparator);

        return buffer.toString();
    }

    private String getTestMethodString(String testmethodName)
    {
        String finalPlaceholder = " ";
        if(shouldCreateFinalMethod)
            finalPlaceholder = "final ";

        String recommendedLineSeparator = findRecommendedLineSeparator();

        String methodBody = defaultTestMethodContent;
        if(shouldCreateTasks)
        {
            String todoTaskTag = JUnitStubUtility.getTodoTaskTag(compilationUnit.getJavaProject());
            if(todoTaskTag != null)
            {
                methodBody = "// " + todoTaskTag + recommendedLineSeparator + defaultTestMethodContent;
            }
        }
        return String.format("public %svoid %s() throws Exception {%s%s%s}",  finalPlaceholder, testmethodName, recommendedLineSeparator, methodBody, recommendedLineSeparator);
    }

    private String findRecommendedLineSeparator()
    {
        String recommendedLineSeparator = StringConstants.NEWLINE;
        try
        {
            recommendedLineSeparator = testCaseCompilationUnit.findRecommendedLineSeparator();
        }
        catch (JavaModelException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }
        return recommendedLineSeparator;
    }

    private IMethod createMethod(String methodName, String methodString, IMethod sibling)
    {
        if(doesMethodExist(methodName))
            return null;

        try
        {
            return testCaseCompilationUnit.findPrimaryType().createMethod(format(methodString), sibling, true, null);
        }
        catch (JavaModelException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }
        catch (MalformedTreeException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }
        catch (BadLocationException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }
        return null;
    }

    private String format(String methodString) throws MalformedTreeException, BadLocationException
    {
        IDocument document = new Document(methodString);
        String lineDelimiter = TextUtilities.getDefaultLineDelimiter(document);
        TextEdit edit = testFormatter.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, document.get(), 0, document.getLength(), 0, lineDelimiter);

        if(edit != null)
        {
            edit.apply(document);
        }

        return document.get();
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

    public static class TestMethodCreationSettings
    {
        private boolean generateComments;
        private ICompilationUnit compilationUnit;
        private String defaultTestMethodContent;
        private boolean shouldCreateFinalMethod;
        private boolean shouldCreateTasks;
        private ICompilationUnit maybeTestCaseCompilationUnit;
        private boolean testCaseJustCreated;
        private String testType;

        public TestMethodCreationSettings generateComments(boolean generateComments)
        {
            this.generateComments = generateComments;
            return this;
        }

        /**
         * @param compilationUnit Could be CUT or a test. createTestMethod will
         *            distinguish
         */
        public TestMethodCreationSettings compilationUnit(ICompilationUnit compilationUnit)
        {
            this.compilationUnit = compilationUnit;
            return this;
        }

        public TestMethodCreationSettings compilationUnit(ICompilationUnit compilationUnitOfClassUnderTest, ICompilationUnit testCaseCompilationUnit)
        {
            this.compilationUnit = compilationUnitOfClassUnderTest;
            this.maybeTestCaseCompilationUnit = testCaseCompilationUnit;
            return this;
        }

        public TestMethodCreationSettings createFinalMethod(boolean createFinalMethod)
        {
            shouldCreateFinalMethod = createFinalMethod;
            return this;
        }

        public TestMethodCreationSettings createTasks(boolean createTasks)
        {
            shouldCreateTasks = createTasks;
            return this;
        }

        public TestMethodCreationSettings defaultTestMethodContent(String content)
        {
            this.defaultTestMethodContent = content;
            return this;
        }

        public TestMethodCreationSettings testCaseJustCreated(boolean testCaseJustCreated)
        {
            this.testCaseJustCreated = testCaseJustCreated;
            return this;
        }

        public TestMethodCreationSettings testType(String testType)
        {
            this.testType = testType;
            return this;
        }
    }
}
