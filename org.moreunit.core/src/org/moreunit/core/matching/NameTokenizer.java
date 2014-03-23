package org.moreunit.core.matching;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Splits a name into tokens.
 */
public abstract class NameTokenizer
{
    private static final String DEFAULT_SEPARATOR = "";

    private final String separator;

    public NameTokenizer()
    {
        this.separator = DEFAULT_SEPARATOR;
    }

    public NameTokenizer(String separator)
    {
        this.separator = separator;
    }

    public String separator()
    {
        return separator;
    }

    private List<String> removeEmptyWords(List<String> words)
    {
        List<String> wordsNotEmpty = new ArrayList<String>();
        for (String word : words)
        {
            if(word.length() != 0)
            {
                wordsNotEmpty.add(word);
            }
        }
        return wordsNotEmpty;
    }

    public TokenizationResult tokenize(String name)
    {
        checkName(name);

        List<String> words = getWords(name);
        words = removeEmptyWords(words);

        return new TokenizationResult(words, getCombinationsFromStart(words), getCombinationsFromEnd(words));
    }

    protected abstract List<String> getWords(String name);

    private void checkName(String name)
    {
        if(name == null || name.length() == 0 || ! name.trim().equals(name))
        {
            throw new IllegalArgumentException("Invalid name: " + name);
        }
    }

    private List<String> getCombinationsFromStart(List<String> tokens)
    {
        if(tokens.size() < 2)
        {
            return emptyList();
        }

        List<String> combinations = new ArrayList<String>();
        StringBuilder combination = null;

        for (Iterator<String> it = tokens.iterator(); it.hasNext();)
        {
            String token = it.next();
            if(! it.hasNext())
            {
                break;
            }

            if(combination == null)
            {
                combination = new StringBuilder(token);
            }
            else
            {
                combination.append(separator);
                combination.append(token);
            }
            combinations.add(combination.toString());
        }

        return combinations;
    }

    private List<String> getCombinationsFromEnd(List<String> tokens)
    {
        if(tokens.size() < 2)
        {
            return emptyList();
        }

        List<String> combinations = new ArrayList<String>();
        StringBuilder combination = null;

        for (ListIterator<String> it = tokens.listIterator(tokens.size()); it.hasPrevious();)
        {
            String token = it.previous();
            if(! it.hasPrevious())
            {
                break;
            }

            if(combination == null)
            {
                combination = new StringBuilder(token);
            }
            else
            {
                combination.insert(0, separator);
                combination.insert(0, token);
            }
            combinations.add(0, combination.toString());
        }

        return combinations;
    }

    public static class TokenizationResult
    {
        private final List<String> tokens;
        private final List<String> combinationsFromStart;
        private final List<String> combinationsFromEnd;

        public TokenizationResult(List<String> tokens, List<String> combinationsFromStart, List<String> combinationsFromEnd)
        {
            this.tokens = unmodifiableList(tokens);
            this.combinationsFromStart = unmodifiableList(combinationsFromStart);
            this.combinationsFromEnd = unmodifiableList(combinationsFromEnd);
        }

        public List<String> getTokens()
        {
            return tokens;
        }

        public List<String> getCombinationsFromStart()
        {
            return combinationsFromStart;
        }

        public List<String> getCombinationsFromEnd()
        {
            return combinationsFromEnd;
        }
    }
}
