package org.moreunit.core.matching;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class NameTokenizer
{

    private final String separator;

    public NameTokenizer(String separator)
    {
        this.separator = separator;
    }

    public String getSeparator()
    {
        return separator;
    }

    public TokenizationResult tokenize(String name)
    {
        checkName(name);

        List<String> words = new ArrayList<String>();

        WordScanner scanner = new WordScanner(name);
        while (hasNextWord(scanner))
        {
            words.add(nextWord(scanner));
        }

        return new TokenizationResult(words, getCombinationsFromStart(words), getCombinationsFromEnd(words));
    }

    private void checkName(String name)
    {
        if(name == null || name.length() == 0 || ! name.trim().equals(name))
        {
            throw new IllegalArgumentException("Invalid name: " + name);
        }
    }

    protected abstract String nextWord(WordScanner scanner);

    protected abstract boolean hasNextWord(WordScanner scanner);

    private List<String> getCombinationsFromStart(List<String> tokens)
    {
        if(tokens.size() < 2)
        {
            return emptyList();
        }

        List<String> combinations = new ArrayList<String>();
        String combination = null;

        for (Iterator<String> it = tokens.iterator(); it.hasNext();)
        {
            String token = it.next();
            if(! it.hasNext())
            {
                break;
            }

            if(combination == null)
            {
                combination = token;
            }
            else
            {
                combination = combination + token;
            }
            combinations.add(combination);
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
        String combination = null;

        for (ListIterator<String> it = tokens.listIterator(tokens.size()); it.hasPrevious();)
        {
            String token = it.previous();
            if(! it.hasPrevious())
            {
                break;
            }

            if(combination == null)
            {
                combination = token;
            }
            else
            {
                combination = token + combination;
            }
            combinations.add(0, combination);
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
