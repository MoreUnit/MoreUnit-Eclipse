package org.moreunit.util;

public interface TestMethodDiviner
{
    public String getTestMethodNameFromMethodName(String methodName);

    public String getTestMethodNameAfterRename(String methodNameBeforeRename, String methodNameAfterRename, String testMethodName);

    public String getMethodNameFromTestMethodName(String testMethodName);
}
