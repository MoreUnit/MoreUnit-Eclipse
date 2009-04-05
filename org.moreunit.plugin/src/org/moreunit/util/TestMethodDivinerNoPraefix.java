package org.moreunit.util;

public class TestMethodDivinerNoPraefix implements TestMethodDiviner
{

    public String getTestMethodNameFromMethodName(String methodName)
    {
        return methodName;
    }

    public String getTestMethodNameAfterRename(String methodNameBeforeRename, String methodNameAfterRename, String testMethodName)
    {
        return testMethodName.replaceFirst(methodNameBeforeRename, methodNameAfterRename);
    }

    public String getMethodNameFromTestMethodName(String testMethodName)
    {
        return testMethodName;
    }

}
