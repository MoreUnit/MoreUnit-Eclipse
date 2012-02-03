/*
 * Created on 20.02.2005 20:03:01
 * Created by Vera
 */
package org.moreunit.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IMethod;

public class BaseTools
{

    /**
     * This method tries to find out the name of the class which is under test
     * by the name of a given testcase.
     * 
     * @param testCaseClass name of the testcase
     * @param prefixes possible prefixes of the testcase
     * @param suffixes possible suffixes of the testcase
     * @param packagePrefix
     * @param packageSuffix
     * @return name of the class under test
     */
    @SuppressWarnings("unchecked")
    public static List<String> getTestedClass(String testCaseClass, String[] prefixes, String[] suffixes, String packagePrefix, String packageSuffix)
    {
        if(testCaseClass == null || testCaseClass.length() <= 1)
            return Collections.EMPTY_LIST;

        if(packagePrefix != null && packagePrefix.length() > 0)
        {
            testCaseClass = testCaseClass.replaceFirst(packagePrefix + "\\.", "");
        }
        if(packageSuffix != null && packageSuffix.length() > 0)
        {
            testCaseClass = removeSuffixFromTestCase(testCaseClass, packageSuffix);
        }
        List<String> results = new ArrayList<String>();
        if(suffixes != null)
        {
            for (String suffix : suffixes)
            {
                if(testCaseClass.endsWith(suffix))
                    results.add(testCaseClass.substring(0, testCaseClass.length() - suffix.length()));
            }
        }

        if(prefixes != null)
        {
            for (String prefix : prefixes)
            {
                if(testCaseClass.startsWith(prefix))
                    results.add(testCaseClass.replaceFirst(prefix, StringConstants.EMPTY_STRING));
            }
        }

        return results;
    }

    public static String removeSuffixFromTestCase(String testClassName, String packageSuffix)
    {
        String[] pathElements = testClassName.split("\\.");
        int theLastButOne = pathElements.length - 2;
        if(theLastButOne < 0)
            return testClassName;
        if(pathElements[theLastButOne].equals(packageSuffix))
        {
            pathElements[theLastButOne] = StringConstants.EMPTY_STRING;

            StringBuffer result = new StringBuffer();
            for (int i = 0; i < theLastButOne; i++)
            {
                result.append(pathElements[i]).append(StringConstants.STRING_DOT);
            }
            return result.append(pathElements[pathElements.length - 1]).toString();
        }

        return testClassName;
    }

    /**
     * Returns the first method which has the same beginning.<br>
     * Example:<br>
     * methods[0] = some()<br>
     * methods[1] = foo()<br>
     * methodName = "fooSome"<br>
     * returns foo()<br>
     * If no method is found in the array, this method returns <code>null</code>
     * .
     * 
     * @param methods
     * @param methodName
     * @return
     */
    public static IMethod getFirstMethodWithSameNamePrefix(IMethod[] methods, String methodName)
    {
        if(methodName != null)
        {
            for (IMethod method : methods)
            {
                if(methodName.startsWith(method.getElementName()) && method.exists())
                {
                    return method;
                }
            }
        }

        return null;
    }

    // public static String firstCharToUpperCase(String aString) {
    // if(aString == null || aString.length() == 0)
    // return aString;
    //
    // String firstChar = String.valueOf(aString.charAt(0));
    // return aString.replaceFirst(firstChar, firstChar.toUpperCase());
    // }

    /**
     * Returns a list of String which are possible unqualified names for the
     * testedClassString.<br>
     * Example:<br>
     * testedClassString: "OneTwoThree"<br>
     * returns: {"One", "OneTwo", "OneTwoThree"}
     * 
     * @param testedClassString The name of the test class.
     * @return a <code>List</code> of <code>String</code>s containing possible
     *         names for the class under test derived from \a testedClassString.
     */
    public static List<String> getListOfUnqualifiedTypeNames(String testedClassString)
    {
        List<String> resultList = new ArrayList<String>();

        WordTokenizer wordTokenizer = new WordTokenizer(testedClassString);
        while (wordTokenizer.hasMoreElements())
        {
            resultList.add(getNewWord(resultList, wordTokenizer.nextElement()));
        }

        return resultList;
    }

    public static List<String> getListOfUnqualifiedTypeNames(List<String> testedClasses)
    {
        List<String> result = new ArrayList<String>();
        for (String clazz : testedClasses)
        {
            result.addAll(getListOfUnqualifiedTypeNames(clazz));
        }
        Collections.sort(result, new StringLengthComparator());
        Collections.reverse(result);
        return result;
    }

    /**
     * Returns a String which is a concatenation of the last but on element of
     * wordList and appends word to this String.<br>
     * Example:<br>
     * wordList: {"One"}<br>
     * word: Two<br>
     * returns: "OneTwo"
     */
    private static String getNewWord(List<String> wordList, String word)
    {
        StringBuilder stringBuilder = new StringBuilder();
        if(wordList.size() > 0)
            stringBuilder.append(wordList.get(wordList.size() - 1));
        stringBuilder.append(word);
        return stringBuilder.toString();
    }

    public static boolean isStringTrimmedEmpty(String aString)
    {
        return aString == null || aString.trim().length() == 0;
    }

}