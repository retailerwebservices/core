package org.jimmutable.cloud.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

public class HttpUtils 
{
	/**
	 * Simple utility function that gets the contents of a url as a string.
	 * Slow. Do not use for large files etc.
	 * 
	 * @param url
	 *            The url to fetch data from
	 * @param default_value
	 *            The value to return if any sort of error occours
	 * @return The contents of the URL as a string
	 */
	static public String getURLContentsToString(String url, String default_value)
	{ 
		try
		{
			InputStream src = new URL(url).openStream();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			IOUtils.copy(src, out);
			
			src.close();
			out.close();
			
			return new String(out.toByteArray(), Charset.forName("UTF-8"));
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
}
