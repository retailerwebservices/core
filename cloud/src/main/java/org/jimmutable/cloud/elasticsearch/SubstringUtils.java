package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.core.utils.Validator;

public class SubstringUtils
{
	static public void appendAllSubstringsOfLength(StringBuilder builder, String str, int length)
	{
		Validator.notNull(builder, str);
		Validator.min(length, 1);

		if (length > str.length())
			return;

		for (int start = 0; start < str.length() - length + 1; start++) {
			if (builder.length() != 0 && builder.charAt(builder.length() - 1) != ' ')
				builder.append(' ');

			builder.append(str.substring(start, start + length));

		}
	}

	static public String createSubstringMatchingText(String str, int min_substring_size, int max_substring_size, String default_value)
	{
		if (str == null || min_substring_size < 1 || max_substring_size < 1 || min_substring_size > max_substring_size)
			return default_value;

		str = str.trim().toLowerCase();

		StringBuilder ret = new StringBuilder(str);

		for (int cur_length = min_substring_size; cur_length < max_substring_size; cur_length++) {
			appendAllSubstringsOfLength(ret, str, cur_length);
		}

		return ret.toString();
	}

	static public void main(String args[])
	{
		System.out.println(createSubstringMatchingText("abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMN", 1, 50, null));
		System.out.println(writeTextWithPrefixMatchingSupport("monkey", 50, null));
	}

	/**
	 * 
	 * Returns all possible prefixes in a space separated string. For example,
	 * "monkey" would return "monkey monke monk mon mo m"
	 * 
	 * @param text
	 *            The text to write prefixes for
	 * @param max_text_length
	 *            The maximum substring length the prefixes will be created for
	 * @param default_value
	 *            The default value to return in the event the text is null or empty
	 * @return The String with space separated prefixes
	 */
	static public String writeTextWithPrefixMatchingSupport(String text, int max_text_length, String default_value)
	{
		Validator.notNull(text);

		if (text == null)
			return default_value;
		text = text.trim();
		if (text.length() == 0)
			return default_value;
		if (text.length() > max_text_length)
			text = text.substring(0, max_text_length);

		StringBuilder sb = new StringBuilder();

		sb.append(text);
		int count = text.length() - 1;
		while (count > 0) {
			sb.append(" ").append(text.substring(0, count));
			count--;
		}
		return sb.toString();
	}

}
