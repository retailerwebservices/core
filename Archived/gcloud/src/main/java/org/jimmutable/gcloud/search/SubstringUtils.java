package org.jimmutable.gcloud.search;

import org.jimmutable.core.utils.Validator;

public class SubstringUtils 
{
	static public void appendAllSubstringsOfLength(StringBuilder builder, String str, int length)
	{
		Validator.notNull(builder, str);
		Validator.min(length, 1);
		
		if ( length > str.length() ) return;
		
		for ( int start = 0; start < str.length() - length+1; start++ )
		{
			if ( builder.length() != 0 && builder.charAt(builder.length()-1) != ' ' ) 
				builder.append(' ');
			
			builder.append(str.substring(start, start+length));
		}
	}
	
	static public String createSubstringMatchingText(String str, int min_substring_size, int max_substring_size, String default_value)
	{
		if ( str == null || min_substring_size < 1 || max_substring_size < 1 || min_substring_size > max_substring_size ) return default_value;
		
		str = str.trim().toLowerCase();
		
		StringBuilder ret = new StringBuilder(str);
		
		for ( int cur_length = min_substring_size; cur_length < max_substring_size; cur_length++ )
		{
			appendAllSubstringsOfLength(ret, str, cur_length);
		}
		
		return ret.toString();
	}
	
	static public void main(String args[])
	{
		System.out.println(createSubstringMatchingText("abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMN", 1, 10, null).length());
	}
}
