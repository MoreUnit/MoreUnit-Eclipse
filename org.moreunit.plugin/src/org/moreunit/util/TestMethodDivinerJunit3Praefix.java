package org.moreunit.util;

import org.moreunit.core.util.StringConstants;
import org.moreunit.log.LogHandler;

public class TestMethodDivinerJunit3Praefix implements TestMethodDiviner
{
    public static final String TEST_METHOD_PRAEFIX = "test";

    public String getTestMethodNameFromMethodName(String methodName)
    {
        if(methodName == null || methodName.length() == 0)
        {
            LogHandler.getInstance().handleWarnLog("Methodname is null or has length of 0");
            return StringConstants.EMPTY_STRING;
        }
        String firstChar = String.valueOf(methodName.charAt(0));
        return TEST_METHOD_PRAEFIX + methodName.replaceFirst(firstChar, firstChar.toUpperCase());
    }

    public String getTestMethodNameAfterRename(String methodNameBeforeRename, String methodNameAfterRename, String testMethodName)
    {
        String old = getStringWithFirstCharToUpperCase(methodNameBeforeRename);
        String newName = getStringWithFirstCharToUpperCase(methodNameAfterRename);
        return testMethodName.replaceFirst(old, newName);
    }

    private String getStringWithFirstCharToUpperCase(String string)
    {
        if(string == null || string.length() == 0)
            return string;

        char firstChar = string.charAt(0);
        StringBuffer result = new StringBuffer();
        result.append(Character.toUpperCase(firstChar));
        result.append(string.substring(1));

        return result.toString();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.moreunit.util.TestMethodDiviner#getMethodNameFromTestMethodName(java
     * .lang.String)
     */
    public String getMethodNameFromTestMethodName(String testMethodName)
    {
        if(testMethodName == null || ! testMethodName.startsWith(TEST_METHOD_PRAEFIX) || testMethodName.length() <= 4)
            return null;

        char erstesZeichen = testMethodName.charAt(4);
        StringBuffer result = new StringBuffer();
        result.append(Character.toLowerCase(erstesZeichen));
        result.append(testMethodName.substring(5));
        return result.toString();
    }

}
