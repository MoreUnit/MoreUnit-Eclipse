package org.moreunit.util;

import org.eclipse.jdt.core.ICompilationUnit;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;

public class TestMethodDivinerFactory
{
    private ICompilationUnit compilationUnit;
    private Preferences preferences;

    public TestMethodDivinerFactory(ICompilationUnit compilationUnit)
    {
        this.compilationUnit = compilationUnit;
        this.preferences = Preferences.getInstance();
    }

    public TestMethodDiviner create()
    {
        String methodType = preferences.getTestMethodType(compilationUnit.getJavaProject());
        // LogHandler.getInstance().handleInfoLog(
        // "TestMethodDivinerFactory.create() - " +methodType);
        if(methodType.equals(PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX))
        {
            return new TestMethodDivinerNoPraefix();
        }
        return new TestMethodDivinerJunit3Praefix();
    }

    public TestMethodDiviner create(String testType)
    {
        return PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3.equals(testType) ? new TestMethodDivinerJunit3Praefix() : create();
    }
}
