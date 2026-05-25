package org.moreunit.util;

public class TestMethodDivinerNoPraefix implements TestMethodDiviner
{

    @Override
    public String getTestMethodNameFromMethodName(String methodName)
    {
        return methodName;
    }

    @Override
    public String getTestMethodNameAfterRename(String methodNameBeforeRename, String methodNameAfterRename, String testMethodName)
    {
        // Performance optimization: Replaced regex-based replaceFirst with
        // faster manual indexOf and substring extraction for literal replacement.
        int index = testMethodName.indexOf(methodNameBeforeRename);
        if (index != -1) {
            return testMethodName.substring(0, index) + methodNameAfterRename + testMethodName.substring(index + methodNameBeforeRename.length());
        }
        return testMethodName;
    }

    @Override
    public String getMethodNameFromTestMethodName(String testMethodName)
    {
        return testMethodName;
    }

}
