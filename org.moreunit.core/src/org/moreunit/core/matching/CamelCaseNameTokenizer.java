package org.moreunit.core.matching;

public class CamelCaseNameTokenizer extends NameTokenizer
{

    private static final String SEPARATOR = "";

    public CamelCaseNameTokenizer()
    {
        super(SEPARATOR);
    }

    @Override
    protected String nextWord(WordScanner scanner)
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

    @Override
    protected boolean hasNextWord(WordScanner scanner)
    {
        return scanner.hasNext();
    }
}
