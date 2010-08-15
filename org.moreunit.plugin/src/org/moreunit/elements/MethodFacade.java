package org.moreunit.elements;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.util.MoreUnitContants;

public class MethodFacade
{
    private final IMethod method;

    public MethodFacade(IMethod method)
    {
        this.method = method;
    }

    public boolean isTestMethod()
    {
        if(shouldTestMethodsBeAnnotated(method.getJavaProject()))
        {
            return method.getAnnotation(MoreUnitContants.TEST_ANNOTATION_NAME).exists();
        }

        try
        {
            return Signature.SIG_VOID.equals(method.getReturnType()) && (method.getFlags() & ClassFileConstants.AccPublic) != 0;
        }
        catch (JavaModelException e)
        {
            return false;
        }
    }

    private static boolean shouldTestMethodsBeAnnotated(IJavaProject project)
    {
        String testType = Preferences.getInstance().getTestType(project);
        return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4.equals(testType) || PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(testType);
    }

    public boolean isAnonymous()
    {
        return isAnonymous(this.method);
    }

    private boolean isAnonymous(IMethod method)
    {
        try
        {
            return method.getParent() instanceof IType && ((IType) method.getParent()).isAnonymous();
        }
        catch (JavaModelException e)
        {
            return false;
        }
    }

    public IMethod getFirstNonAnonymousMethodCallingThisMethod()
    {
        IMethod result = this.method;
        while (isAnonymous(result) && result.getParent().getParent() instanceof IMethod)
        {
            result = (IMethod) result.getParent().getParent();
        }
        return result;
    }
}
