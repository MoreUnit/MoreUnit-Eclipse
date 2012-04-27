package org.moreunit.core.matching;

public class WordScanner
{
    private final char[] name;
    private int index = - 1;
    private int currentWordStart = 0;
    private int currentWordEnd = - 1;

    public WordScanner(String name)
    {
        this.name = name.toCharArray();
    }

    public boolean hasNext()
    {
        return hasNext(1);
    }

    public boolean hasNext(int offset)
    {
        return index + offset < name.length;
    }

    public boolean hasPrevious()
    {
        return hasPrevious(1);
    }

    public boolean hasPrevious(int offset)
    {
        return index >= offset;
    }

    public char current()
    {
        return name[index];
    }

    public char next()
    {
        return next(1);
    }

    public char next(int offset)
    {
        checkNext(offset);
        return name[index + offset];
    }

    public char previous()
    {
        return previous(1);
    }

    public char previous(int offset)
    {
        checkPrevious(offset);
        return name[index - offset];
    }

    public WordScanner backward()
    {
        return backward(1);
    }

    public WordScanner backward(int offset)
    {
        checkPrevious(offset);
        index -= offset;
        return this;
    }

    public WordScanner forward()
    {
        return forward(1);
    }

    public WordScanner forward(int offset)
    {
        checkNext(offset);
        index += offset;
        return this;
    }

    private void checkNext(int offset)
    {
        if(! hasNext(offset))
        {
            throw new IndexOutOfBoundsException(String.valueOf(index + offset));
        }
    }

    private void checkPrevious(int offset)
    {
        if(! hasPrevious(offset))
        {
            throw new IndexOutOfBoundsException(String.valueOf(index - offset));
        }
    }

    public String getCurrentWord()
    {
        int end = currentWordEnd != - 1 ? currentWordEnd : index;
        String currentWord = new String(name, currentWordStart, end - currentWordStart + 1);
        currentWordStart = end + 1;
        currentWordEnd = - 1;
        return currentWord;
    }
}
