package org.moreunit.util;

import org.moreunit.core.util.StringConstants;
import org.moreunit.log.LogHandler;

public class TestMethodDivinerJunit3Praefix implements TestMethodDiviner
{
    public static final String TEST_METHOD_PRAEFIX = "test";

    @Override
    public String getTestMethodNameFromMethodName(String methodName)
    {
        if(methodName == null || methodName.length() == 0)
        {
            LogHandler.getInstance().handleWarnLog("Methodname is null or has length of 0");
            return StringConstants.EMPTY_STRING;
        }
        // Performance optimization: Avoids replaceFirst by using the helper method.
        // It is faster to use substring/concatenation instead of regex matching.
        return TEST_METHOD_PRAEFIX + getStringWithFirstCharToUpperCase(methodName);
    }

    @Override
    public String getTestMethodNameAfterRename(String methodNameBeforeRename, String methodNameAfterRename, String testMethodName)
    {
        final String old = getStringWithFirstCharToUpperCase(methodNameBeforeRename);
        final String newName = getStringWithFirstCharToUpperCase(methodNameAfterRename);

        // Performance optimization: Replaced regex-based replaceFirst with
        // faster manual indexOf and substring extraction for literal replacement.
        final int index = testMethodName.indexOf(old);
        if (index != -1) {
            return testMethodName.substring(0, index) + newName + testMethodName.substring(index + old.length());
        }
        return testMethodName;
    }

    private String getStringWithFirstCharToUpperCase(String string)
    {
        if(string == null || string.length() == 0)
            return string;

        final char firstChar = string.charAt(0);
        final StringBuilder result = new StringBuilder();
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
    @Override
    public String getMethodNameFromTestMethodName(String testMethodName)
    {
        if(testMethodName == null || ! testMethodName.startsWith(TEST_METHOD_PRAEFIX) || testMethodName.length() <= 4)
            return null;

        final char erstesZeichen = testMethodName.charAt(4);
        final StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(erstesZeichen));
        result.append(testMethodName.substring(5));
        return result.toString();
    }

}
