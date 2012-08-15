package org.moreunit.core.matching;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits a CamelCase name into tokens.
 */
public class CamelCaseNameTokenizer extends NameTokenizer
{
    private String nextWord(WordScanner scanner)
    {
        while (scanner.hasNext())
        {
            scanner.forward();
            if(scanner.hasNext() //
               && (Character.isUpperCase(scanner.next()) //
               || (Character.isDigit(scanner.next()) && ! Character.isDigit(scanner.current()))))
            {
                break;
            }
        }
        return scanner.getCurrentWord();
    }

    private boolean hasNextWord(WordScanner scanner)
    {
        return scanner.hasNext();
    }

    public List<String> getWords(String name)
    {
        List<String> words = new ArrayList<String>();

        WordScanner scanner = new WordScanner(name);
        while (hasNextWord(scanner))
        {
            words.add(nextWord(scanner));
        }

        return words;
    }
}
