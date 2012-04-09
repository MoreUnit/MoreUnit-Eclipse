package org.moreunit.core.matching;

import static java.util.Collections.sort;
import static java.util.regex.Pattern.quote;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.moreunit.core.matching.NameTokenizer.TokenizationResult;

public class TestFileNamePattern
{

    public static final String SRC_FILE_VARIABLE = "${srcFile}";

    private static final Comparator<String> byDescendingLength = new Comparator<String>()
    {
        public int compare(String s1, String s2)
        {
            int result = Integer.valueOf(s2.length()).compareTo(s1.length());
            return result != 0 ? result : s1.compareToIgnoreCase(s2);
        }
    };

    private final NameTokenizer tokenizer;
    private final String prefix;
    private final String suffix;
    private final boolean wcBefore;
    private final boolean wcAfter;
    private final String patternString;
    private final Pattern pattern;

    public TestFileNamePattern(String template, NameTokenizer tokenizer)
    {
        this.tokenizer = tokenizer;

        patternString = template.replace("*", ".*");

        int varStart = patternString.indexOf("$");
        int varEnd = patternString.indexOf("}");

        String pre = patternString.substring(0, varStart);
        if(pre.endsWith(tokenizer.getSeparator()))
        {
            pre = pre.substring(0, pre.length() - tokenizer.getSeparator().length());
        }
        wcBefore = pre.endsWith(".*");
        if(wcBefore)
        {
            prefix = pre.substring(0, pre.length() - 2);
        }
        else
        {
            prefix = pre;
        }

        String suf = varEnd + 1 == patternString.length() ? "" : patternString.substring(varEnd + 1);
        if(suf.startsWith(tokenizer.getSeparator()))
        {
            suf = suf.substring(tokenizer.getSeparator().length());
        }
        wcAfter = suf.startsWith(".*");
        if(wcAfter)
        {
            suffix = suf.substring(2);
        }
        else
        {
            suffix = suf;
        }

        pattern = Pattern.compile(patternString.replace(SRC_FILE_VARIABLE, ".*"));
    }

    public FileNameEvaluation evaluate(String fileBaseName)
    {
        if(pattern.matcher(fileBaseName).matches())
        {
            String separator = String.format("(%s)?", quote(tokenizer.getSeparator()));
            String cleanName = fileBaseName.replaceFirst("^" + prefix + separator, "").replaceFirst(separator + suffix + "$", "");

            TokenizationResult result = tokenizer.tokenize(cleanName);

            List<String> otherFileNames = new ArrayList<String>();
            if(wcBefore)
            {
                otherFileNames.addAll(result.getCombinationsFromEnd());
            }
            if(wcAfter)
            {
                otherFileNames.addAll(result.getCombinationsFromStart());
            }

            sort(otherFileNames, byDescendingLength);

            // if wcBefore && wcAfter, doesn't search for "middle" token
            // combinations as it would be very time consuming
            // to search for them, plus it would give lots of garbage results

            return new FileNameEvaluation(true, cleanName, otherFileNames);
        }

        String testFile = patternString.replace(SRC_FILE_VARIABLE, fileBaseName);
        return new FileNameEvaluation(false, testFile, new ArrayList<String>());
    }
}
