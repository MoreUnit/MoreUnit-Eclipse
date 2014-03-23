package org.moreunit.core.matching;

import static java.util.Collections.sort;
import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.quote;
import static org.moreunit.core.util.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class TestFileNamePatternParser
{
    public static final String SRC_FILE_VARIABLE = "${srcFile}";
    private static final Pattern CHARS_TO_ESCAPE = Pattern.compile("([\\*\\(\\)\\|])");

    private final String patternToParse;
    private final NameTokenizer tokenizer;

    public TestFileNamePatternParser(String patternToParse, NameTokenizer tokenizer)
    {
        this.patternToParse = checkNotNull(patternToParse);
        this.tokenizer = checkNotNull(tokenizer);
    }

    public Result parse()
    {
        int varIdx = patternToParse.indexOf(SRC_FILE_VARIABLE);
        if(varIdx == - 1)
        {
            return Failure.MISSING_SRC_FILE_VARIABLE;
        }
        if(varIdx == 0 && patternToParse.length() == SRC_FILE_VARIABLE.length())
        {
            return Failure.TEST_FILE_NAME_IS_EQUAL_TO_SRC_FILE_NAME;
        }

        UserDefinedPart prefix = parseUserDefinedPart(patternToParse.substring(0, varIdx));
        if(prefix.failure != null)
        {
            return prefix.failure;
        }

        UserDefinedPart suffix = parseUserDefinedPart(patternToParse.substring(varIdx + SRC_FILE_VARIABLE.length()));
        if(suffix.failure != null)
        {
            return suffix.failure;
        }

        return new Success(tokenizer.separator(), prefix, suffix);
    }

    private UserDefinedPart parseUserDefinedPart(String str)
    {
        return new UserDefinedPart(str, tokenizer.separator());
    }

    public static interface Result
    {
        boolean success();

        Success get();
    }

    public static class Success implements Result
    {
        private final String separator;
        private final UserDefinedPart prefix;
        private final UserDefinedPart suffix;

        public Success(String separator, UserDefinedPart prefix, UserDefinedPart suffix)
        {
            this.separator = separator;
            this.prefix = prefix;
            this.suffix = suffix;
        }

        @Override
        public boolean success()
        {
            return true;
        }

        @Override
        public Success get()
        {
            return this;
        }

        public String separator()
        {
            return separator;
        }

        public UserDefinedPart prefix()
        {
            return prefix;
        }

        public UserDefinedPart suffix()
        {
            return suffix;
        }
    }

    public static enum Failure implements Result
    {
        MISSING_SRC_FILE_VARIABLE, TEST_FILE_NAME_IS_EQUAL_TO_SRC_FILE_NAME;

        @Override
        public boolean success()
        {
            return false;
        }

        @Override
        public Success get()
        {
            throw new IllegalStateException("this result is a failure");
        }
    }

    public static class UserDefinedPart
    {
        private final String rawString;
        private final char[] chars;
        private String quotedSeparator;
        private final char[] separatorChars;
        private int currentCharIdx;
        private final List<String> alternatives = new ArrayList<String>();
        private final String firstAlternative;
        public Failure failure;
        private boolean wildcardBefore;
        private boolean wildcardAfter;
        private boolean escapementOn;
        private StringBuilder before = new StringBuilder();
        private StringBuilder after = new StringBuilder();

        private UserDefinedPart(String str, String separator)
        {
            this.rawString = str;
            this.chars = str.toCharArray();
            this.quotedSeparator = quote(separator);
            this.separatorChars = escapeCharsWhereNeeded(separator).toCharArray();
            parse();
            firstAlternative = alternatives.isEmpty() ? null : alternatives.get(0);
            sort(alternatives, new ReversedLengthComparator());
        }

        private static String escapeCharsWhereNeeded(String str)
        {
            return CHARS_TO_ESCAPE.matcher(str).replaceAll(quoteReplacement("\\$1"));
        }

        private void parse()
        {
            while (failure == null && isNotEndOfChars())
            {
                parseCurrentChar();
            }
        }

        private boolean isNotEndOfChars()
        {
            return currentCharIdx < chars.length;
        }

        private void parseCurrentChar()
        {
            if(parseEscapeChar())
                return;

            if(parseSeparator())
            {
                addToBeforeOrAfterBuffer(quotedSeparator);
                return;
            }

            if(parseWildcard())
            {
                if(alternatives.isEmpty())
                    wildcardBefore = true;
                else
                    wildcardAfter = true;

                addToBeforeOrAfterBuffer(".*");
                return;
            }

            if(parseAlternatives())
                return;

            moveToNextChar();
        }

        private void addToBeforeOrAfterBuffer(String string)
        {
            if(alternatives.isEmpty())
                before.append(string);
            else
                after.append(string);
        }

        private boolean parseEscapeChar()
        {
            if(isNotEndOfChars() && currentChar() == '\\' && ! escapementOn)
            {
                currentCharIdx++;
                escapementOn = true;
                return true;
            }
            return false;
        }

        private void moveToNextChar()
        {
            addOffset(1);
        }

        private void addOffset(int offset)
        {
            currentCharIdx += offset;

            if(! parseEscapeChar())
                escapementOn = false;
        }

        private boolean parseAlternatives()
        {
            StringBuilder buffer = new StringBuilder();

            while (isAlternativeChar())
            {
                char currentChar = currentChar();
                if(! escapementOn && currentChar == '(')
                {
                    moveToNextChar();
                }
                else if(! escapementOn && (currentChar == '|' || currentChar == ')'))
                {
                    alternatives.add(buffer.toString());
                    buffer.delete(0, buffer.length());
                    moveToNextChar();
                }
                else
                {
                    buffer.append(currentChar);
                    moveToNextChar();
                }
            }

            if(buffer.length() != 0)
            {
                alternatives.add(buffer.toString());
            }

            return ! alternatives.isEmpty();
        }

        private char currentChar()
        {
            return chars[currentCharIdx];
        }

        private boolean isAlternativeChar()
        {
            return isNotEndOfChars() && ! currentCharsEqual(separatorChars) && (escapementOn || currentChar() != '*');
        }

        private boolean parseSeparator()
        {
            if(currentCharsEqual(separatorChars))
            {
                addOffset(separatorChars.length);
                return true;
            }
            return false;
        }

        private boolean currentCharsEqual(char[] otherChars)
        {
            if(otherChars.length == 0)
                return false;

            for (int i = 0; i < otherChars.length && currentCharIdx + i < chars.length; i++)
            {
                if(otherChars[i] != chars[currentCharIdx + i])
                    return false;
            }
            return true;
        }

        private boolean parseWildcard()
        {
            if(! escapementOn && currentChar() == '*')
            {
                moveToNextChar();
                return true;
            }
            return false;
        }

        public String after()
        {
            return after.toString();
        }

        public List<String> alternatives()
        {
            return alternatives;
        }

        public String before()
        {
            return before.toString();
        }

        public boolean hasWildcardBefore()
        {
            return wildcardBefore;
        }

        public boolean hasWildcardAfter()
        {
            return wildcardAfter;
        }

        public String raw()
        {
            return rawString;
        }

        public boolean hasAlternatives()
        {
            return ! alternatives.isEmpty();
        }

        public String firstAlternative()
        {
            if(firstAlternative == null)
            {
                throw new NoSuchElementException();
            }
            return firstAlternative;
        }
    }

    private static final class ReversedLengthComparator implements Comparator<String>
    {
        @Override
        public int compare(String s1, String s2)
        {
            return s2.length() - s1.length();
        }
    }
}
