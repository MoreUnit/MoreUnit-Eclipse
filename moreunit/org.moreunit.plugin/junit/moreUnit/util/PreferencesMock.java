/**
 * 
 */
package moreUnit.util;

import moreUnit.preferences.Preferences;

/**
 * @author giana
 * 13.05.2006 18:56:57
 */
public class PreferencesMock extends Preferences {
	
	private String[] prefixes;
	private String[] suffixes;
	
	public String[] getPrefixes() {
		return prefixes;
	}
	
	public String[] getSuffixes() {
		return suffixes;
	}

	public void setPrefixes(String[] prefixes) {
		this.prefixes = prefixes;
	}

	public void setSuffixes(String[] suffixes) {
		this.suffixes = suffixes;
	}

}


// $Log: not supported by cvs2svn $
// Revision 1.1  2006/05/13 18:32:47  gianasista
// Searching for testcases for a class (based on preferences) + Tests
//