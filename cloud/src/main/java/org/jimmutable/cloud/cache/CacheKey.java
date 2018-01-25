package org.jimmutable.cloud.cache;

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
 * Name(s) can contain any character. Name(s) are trimmed (whitespace at the beginning
 * and ending deleted). Name(s) are case sensitive.
 * 
 * Examples of valid cache keys include
 * 
 * foo/bar://quz
 * foo/bar://https://www.google.com
 * foo://11248
 * 
 * @author kanej
 */
/*
 * CODEREVIEW
 * I'm super confused. I've read the docs and classes for CachePatha and CacheKey.
 * I can't figure out what's what. You have what I think are copy-paste errors in
 * class comments where you use CachePath/path and CacheKey/key interchangeably.
 * See the first paragraph above. "Cache path(s) are / separated....limit on cache keys."
 * Also, the field is called "name", but the error message says "value".
 * Finally, neither javadoc gives a reasonable explanation for _what_ a CachePath or CacheKey
 * _is_, what it is used for, and how they differ/relate. What would be a real-world
 * example of a CachePath and CacheKey's?
 * 
 * Since this is a client-facing concept, I want to make sure it is nailed down.
 * -JMD
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


