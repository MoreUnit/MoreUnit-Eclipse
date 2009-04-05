package org.moreunit.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author vera
 */
public class WordTokenizer implements Enumeration<String>
{

    private List<String> tokens = new ArrayList<String>();
    private int position = 0;

    public WordTokenizer(String string)
    {
        if(string == null || string.length() == 0)
            tokens = new ArrayList<String>();
        else
        {
            char[] charArray = string.toCharArray();
            StringBuilder token = new StringBuilder();

            for (char singleChar : charArray)
            {
                if(Character.isUpperCase(singleChar))
                {
                    addTokenToList(token.toString());
                    token = new StringBuilder();
                }
                token.append(singleChar);
            }
            addTokenToList(token.toString());
        }
    }

    private void addTokenToList(String token)
    {
        if(token.length() > 0)
            tokens.add(token);
    }

    public boolean hasMoreElements()
    {
        return position < tokens.size();
    }

    public String nextElement()
    {
        return tokens.get(position++);
    }
}
