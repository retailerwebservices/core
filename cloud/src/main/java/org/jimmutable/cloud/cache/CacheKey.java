package org.jimmutable.cloud.cache;

import java.util.List;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * 
 * Stringable, the format is path://name
 * 
 * Cache path(s) are / separated elements. Elements are normalized to lower case
 * and limited to letters, numbers and dashes. There is no practical length
 * limit on cache keys.
 * 
 * Name(s) can contain any character. Name(s) are trimmed (whitespace at the begging
 * and ending whitespace deleted).  Name(s) are case sensitive
 * 
 * Examples of valid cache keys include
 * 
 * foo/bar://quz foo/bar://https://www.google.com foo://11248
 * 
 * @author kanej
 *
 */
public class CacheKey extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	private CachePath path;
	private String name;
	
	public CacheKey(String str)
	{
		super(str);
	}
	
	public void normalize() 
	{
	}

	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		
		int idx = getSimpleValue().indexOf("://");
		
		if ( idx == -1 ) 
			throw new ValidationException("Cache keys must contain a \"://\" to separate the cache path and the value");
		
		path = new CachePath(getSimpleValue().substring(0, idx));
		name = getSimpleValue().substring(idx+3, getSimpleValue().length()).trim();
		
		setValue(path+"://"+name);
	}
	
	static public class MyConverter extends Stringable.Converter<CacheKey>
	{
		public CacheKey fromString(String str, CacheKey default_value)
		{
			try
			{
				return new CacheKey(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
	
	public CachePath getSimplePath() { return path; }
	public String getSimpleName() { return name; }
}


