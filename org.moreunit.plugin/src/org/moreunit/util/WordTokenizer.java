package org.moreunit.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author vera
 *
 */
public class WordTokenizer implements Enumeration<String>{
	
	private List<String> tokens = new ArrayList<String>();
	private int position = 0;
	
	public WordTokenizer(String string) {
		if(string == null || string.length()==0)
			tokens = new ArrayList<String>();
		else {
			Pattern pattern = Pattern.compile("[A-Z][a-z]*");
			Matcher matcher = pattern.matcher(string);
			
			int i = 0; 
			while(matcher.find()) {
				tokens.add(matcher.group());
				i++;
			}
		}
	}

	public boolean hasMoreElements() {
		return position < tokens.size();
	}

	public String nextElement() {
		return tokens.get(position++);
	}
}