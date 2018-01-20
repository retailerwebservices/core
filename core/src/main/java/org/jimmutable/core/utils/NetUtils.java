package org.jimmutable.core.utils;

public class NetUtils
{
	/**
	 * Extracts host names from strings. (e.g. www.slashdot.org:8080 ->
	 * www.slashdot.org, www.google.com -> www.google.com)
	 * 
	 * @param src
	 *            The string to extract the host from
	 * @param default_value
	 *            The value to return if the host can not be extracted
	 * @return The host or default_value if it could not be extracted
	 */
	
	static public String extractHostFromHostPortPair(String src, String default_value)
	{
		if ( src == null ) return default_value;
		
		src = src.trim();
		if ( src == null || src.length() == 0 ) return default_value;
		
		int idx = src.indexOf(':');
		if ( idx == -1 ) return src;
		
		src = src.substring(0, idx);
		if ( src.length() == 0 ) return default_value;
		
		return src;
	}

	/**
	 * Extracts port names from strings. (e.g. www.slashdot.org:8080 ->
	 * 8080)
	 * 
	 * @param src
	 *            The string to extract the port from
	 * @param default_value
	 *            The value to return if the port can not be extracted
	 * @return The port or default_value if it could not be extracted
	 */
	
	static public int extractPortFromHostPortPair(String src, int default_value)
	{
		if ( src == null ) return default_value;
		
		src = src.trim();
		if ( src == null ) return default_value;
		
		int idx = src.indexOf(':');
		if ( idx == -1 ) return default_value;
		
		src = src.substring(idx+1);
		if ( src.length() == 0 ) return default_value;
		
		try
		{
			return Integer.parseInt(src.trim());
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
	
}
