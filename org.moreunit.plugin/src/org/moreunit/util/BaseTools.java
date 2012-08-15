/*
 * Created on 20.02.2005 20:03:01
 * Created by Vera
 */
package org.moreunit.util;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.moreunit.core.util.StringLengthComparator;

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

        JavaType testType = new JavaType(testCaseClass);

        String packagePath = testType.getQualifierWithFinalDot();
        if(packagePath.length() != 0)
        {
            if(packagePrefix != null && packagePrefix.length() > 0)
            {
                packagePath = packagePath.replaceFirst("^" + packagePrefix + "\\.", "");
            }
            if(packageSuffix != null && packageSuffix.length() > 0)
            {
                packagePath = packagePath.replaceFirst("\\b" + packageSuffix + "\\.$", "");
            }
        }

        String typeName = testType.getSimpleName();

        List<String> results = new ArrayList<String>();
        if(suffixes != null)
        {
            for (String suffix : suffixes)
            {
                if(typeName.endsWith(suffix))
                {
                    results.add(packagePath + typeName.substring(0, typeName.length() - suffix.length()));
                }
            }
        }

        if(prefixes != null)
        {
            for (String prefix : prefixes)
            {
                if(typeName.startsWith(prefix))
                {
                    results.add(packagePath + typeName.replaceFirst(prefix, ""));
                }
            }
        }

        return results;
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
        List<String> results = new ArrayList<String>();
        List<String> typeNames = new ArrayList<String>();

        JavaType testedType = new JavaType(testedClassString);

        WordTokenizer wordTokenizer = new WordTokenizer(testedType.getSimpleName());
        while (wordTokenizer.hasMoreElements())
        {
            String newTypeName = getNewWord(typeNames, wordTokenizer.nextElement());
            typeNames.add(newTypeName);
            results.add(testedType.getQualifierWithFinalDot() + newTypeName);
        }

        return results;
    }

    public static List<String> getListOfUnqualifiedTypeNames(List<String> testedClasses)
    {
        List<String> result = new ArrayList<String>();
        for (String clazz : testedClasses)
        {
            result.addAll(getListOfUnqualifiedTypeNames(clazz));
        }
        sort(result, reverseOrder(new StringLengthComparator()));
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
}
